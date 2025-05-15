package com.vivu.api.services.impl;

import com.vivu.api.dtos.chatbot.AppSuggestionDto;
import com.vivu.api.dtos.chatbot.ChatRequestDto;
import com.vivu.api.dtos.chatbot.ChatResponseDto;
import com.vivu.api.entities.ChatHistory;
import com.vivu.api.entities.Location;
import com.vivu.api.entities.Tour;
import com.vivu.api.repositories.LocationRepository;
import com.vivu.api.repositories.TourRepository;
import com.vivu.api.repositories.UserRepository; // Import UserRepository
import com.vivu.api.security.services.UserDetailsImpl; // Import UserDetailsImpl
import com.vivu.api.services.ChatHistoryService; // Import ChatHistoryService
import com.vivu.api.services.ChatbotService;
import com.vivu.api.services.GeminiService.GeminiAnalysisResult;
import com.vivu.api.services.GeminiService;
import com.vivu.api.services.impl.GeminiServiceImpl; // *** THÊM IMPORT NÀY ĐỂ CASTING ***
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotServiceImpl.class);
    private static final int MAX_SUGGESTIONS = 3;
    private static final int HISTORY_LIMIT = 12; // Giới hạn lịch sử gửi cho Gemini (ít hơn để tránh quá dài)

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ChatResponseDto handleMessage(ChatRequestDto requestDto) {
        String userMessage = requestDto.getMessage();
        String sessionId = requestDto.getSessionId();
        String responseSessionId;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            logger.error("User not authenticated for chatbot message.");
            return new ChatResponseDto("Lỗi: Bạn cần đăng nhập để sử dụng chatbot.", null, null);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Integer userId = userDetails.getId();

        logger.info("Handling chatbot message for user {}: {}", userId, userMessage);

        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
            responseSessionId = sessionId;
            logger.info("New chat session started for user {}: {}", userId, sessionId);
        } else {
            responseSessionId = sessionId;
            logger.debug("Continuing chat session for user {}: {}", userId, sessionId);
        }

        // 1. Lưu tin nhắn người dùng
        chatHistoryService.saveUserMessage(sessionId, userId, userMessage);

        // 2. Lấy lịch sử gần đây (bao gồm tin nhắn vừa lưu)
        // Lấy nhiều hơn HISTORY_LIMIT một chút để đảm bảo có đủ ngữ cảnh
        List<ChatHistory> history = chatHistoryService.getRecentHistory(sessionId, userId, HISTORY_LIMIT + 1);

        // 3. Phân tích ý định (dựa trên tin nhắn cuối)
        GeminiAnalysisResult analysis = geminiService.analyzeIntent(userMessage);

        // 4. Xử lý và tạo câu trả lời
        String botReplyContent = null;
        List<AppSuggestionDto> suggestions = null;
        boolean useHistoryForReply = true; // Mặc định dùng lịch sử cho trả lời

        switch (analysis.intent()) {
            case "TRAVEL_RECOMMENDATION":
                logger.info("Intent: TRAVEL_RECOMMENDATION, Keywords: {}", analysis.keywords());
                suggestions = findInternalSuggestions(analysis.keywords());

                if (!suggestions.isEmpty()) {
                    // Tìm thấy -> Tạo câu dẫn (chỉ dùng message cuối để xác định ngôn ngữ)
                    botReplyContent = geminiService.generateInternalSuggestionIntro(userMessage, analysis.keywords());
                    useHistoryForReply = false; // Không cần gọi lại Gemini với lịch sử cho câu dẫn
                } else {
                    // Không tìm thấy -> Hỏi Gemini gợi ý ngoài (sẽ dùng lịch sử)
                    logger.info("No internal suggestions. Getting external recommendation with history.");
                    // Đánh dấu để gọi getResponseWithHistory bên dưới
                }
                break;

            case "GENERAL_CHAT":
                logger.info("Intent: GENERAL_CHAT. Getting response with history.");
                // Đánh dấu để gọi getResponseWithHistory bên dưới
                break;

            default: // UNKNOWN
                logger.warn("Intent: UNKNOWN for message: {}", userMessage);
                botReplyContent = "Xin lỗi, mình chưa hiểu ý bạn lắm. Bạn có thể nói rõ hơn được không?";
                useHistoryForReply = false; // Không cần gọi Gemini cho câu trả lời mặc định
                break;
        }

        // Nếu cần tạo trả lời dựa trên lịch sử (không phải câu dẫn hoặc trả lời mặc định)
        if (useHistoryForReply && botReplyContent == null) {
            // *** Ép kiểu sang Implementation để gọi phương thức mới ***
            // Đây là một cách, hoặc bạn có thể thêm getResponseWithHistory vào Interface
            if (geminiService instanceof GeminiServiceImpl) {
                botReplyContent = ((GeminiServiceImpl) geminiService).getResponseWithHistory(history);
            } else {
                // Fallback nếu không phải implementation mong muốn (ít xảy ra với @Autowired)
                logger.error("GeminiService is not an instance of GeminiServiceImpl, cannot call getResponseWithHistory.");
                botReplyContent = geminiService.getGeneralChatResponse(userMessage); // Gọi hàm cũ
            }
        }

        // Xử lý trường hợp Gemini trả về null hoặc trống
        if (botReplyContent == null || botReplyContent.isBlank()) {
            logger.error("Gemini returned null or empty reply for user {} session {}", userId, sessionId);
            botReplyContent = "Xin lỗi, mình đang gặp chút trục trặc, bạn thử lại sau nhé.";
        }

        // 5. Lưu tin nhắn của Bot vào lịch sử
        chatHistoryService.saveBotMessage(sessionId, userId, botReplyContent);

        // 6. Trả về response cho client
        return new ChatResponseDto(botReplyContent, suggestions, responseSessionId);
    }

    // Tìm kiếm trong DB dựa trên keywords
    private List<AppSuggestionDto> findInternalSuggestions(List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return Collections.emptyList();
        }
        Pageable limit = PageRequest.of(0, MAX_SUGGESTIONS * 2);
        List<Location> allFoundLocations = new ArrayList<>();
        List<Tour> allFoundTours = new ArrayList<>();
        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim();
            if (!trimmedKeyword.isEmpty()) {
                logger.debug("Searching internal DB for keyword: {}", trimmedKeyword);
                allFoundLocations.addAll(locationRepository.searchLocations(trimmedKeyword, limit).getContent());
                allFoundTours.addAll(tourRepository.searchTours(trimmedKeyword, limit).getContent());
            }
        }
        List<Location> distinctLocations = allFoundLocations.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Location::getId, loc -> loc, (loc1, loc2) -> loc1),
                        map -> new ArrayList<>(map.values())
                ));
        List<Tour> distinctTours = allFoundTours.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Tour::getId, tour -> tour, (tour1, tour2) -> tour1),
                        map -> new ArrayList<>(map.values())
                ));
        List<AppSuggestionDto> locationSuggestions = distinctLocations.stream()
                .map(loc -> AppSuggestionDto.builder()
                        .type("LOCATION")
                        .id(loc.getId())
                        .title(loc.getTitle())
                        .imageUrl(loc.getHeaderImageUrl())
                        .summary(generateSummary(loc.getTitle()))
                        .build())
                .toList();
        List<AppSuggestionDto> tourSuggestions = distinctTours.stream()
                .map(tour -> AppSuggestionDto.builder()
                        .type("TOUR")
                        .id(tour.getId())
                        .title(tour.getTitle())
                        .imageUrl(tour.getMainImageUrl())
                        .summary(String.format("%s - %s", tour.getLocationName(), tour.getItineraryDuration()))
                        .build())
                .toList();
        return Stream.concat(locationSuggestions.stream(), tourSuggestions.stream())
                .limit(MAX_SUGGESTIONS)
                .collect(Collectors.toList());
    }

    // Helper tạo summary đơn giản
    private String generateSummary(String text) {
        if (text == null || text.length() < 100) { return text; }
        return text.substring(0, 100) + "...";
    }
}