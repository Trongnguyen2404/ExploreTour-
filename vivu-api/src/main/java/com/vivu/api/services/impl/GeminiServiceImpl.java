package com.vivu.api.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivu.api.entities.ChatHistory; // *** THÊM IMPORT NÀY ***
import com.vivu.api.enums.SenderType;    // *** THÊM IMPORT NÀY ***
import com.vivu.api.services.GeminiService;
// Bỏ import jakarta.annotation.PostConstruct
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // Import HashMap

@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiServiceImpl.class);

    @Value("${google.gemini.apiKey}")
    private String apiKey;

    private static final String GEMINI_API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    private static final String DEFAULT_MODEL = "gemini-1.5-flash-latest";

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- PHƯƠNG THỨC CŨ (Chỉ dựa trên tin nhắn cuối) ---
    // Giữ lại để dùng cho phân tích intent ban đầu
    @Override
    public GeminiAnalysisResult analyzeIntent(String message) {
        String promptText = String.format(
                "Phân tích tin nhắn sau của người dùng trong ứng dụng du lịch và phân loại ý định chính thành một trong các loại: TRAVEL_RECOMMENDATION, GENERAL_CHAT. " +
                        "Nếu ý định là TRAVEL_RECOMMENDATION, hãy trích xuất các từ khóa hoặc thực thể quan trọng liên quan đến địa điểm, đặc điểm hoặc loại hình du lịch (ví dụ: 'Nha Trang', 'biển đẹp', 'hoa anh đào', 'leo núi', 'giá rẻ'). Bỏ qua các từ không quan trọng như 'địa điểm', 'chỗ nào', 'có', 'không'. " +
                        "Chỉ trả về kết quả dưới dạng JSON hợp lệ và chỉ JSON mà thôi, với cấu trúc sau: {\"intent\": \"<LOẠI_Ý_ĐỊNH>\", \"keywords\": [\"từ khóa 1\", \"từ khóa 2\", ...]}." +
                        "Nếu không trích xuất được từ khóa nào, trả về mảng keywords rỗng []." +
                        "\n\nTin nhắn người dùng: \"%s\"",
                message
        );

        String jsonResponse = callGeminiApi(promptText); // Vẫn dùng hàm gọi cũ cho intent
        if (jsonResponse == null) {
            return new GeminiAnalysisResult("UNKNOWN", Collections.emptyList());
        }

        try {
            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7, jsonResponse.length() - 3).trim();
            } else if (jsonResponse.startsWith("```")) {
                jsonResponse = jsonResponse.substring(3, jsonResponse.length() - 3).trim();
            }

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            String intent = rootNode.path("intent").asText("UNKNOWN");
            List<String> keywords = new ArrayList<>();
            if (rootNode.hasNonNull("keywords") && rootNode.get("keywords").isArray()) {
                for (JsonNode keywordNode : rootNode.get("keywords")) {
                    keywords.add(keywordNode.asText());
                }
            }
            logger.info("Analyzed Intent: {}, Keywords: {}", intent, keywords);
            return new GeminiAnalysisResult(intent, keywords);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response from Gemini for intent analysis: {}. Response: {}", e.getMessage(), jsonResponse);
            return new GeminiAnalysisResult("UNKNOWN", Collections.emptyList());
        }
    }

    // --- PHƯƠNG THỨC MỚI (Sử dụng lịch sử nếu có) ---

    // Lấy gợi ý bên ngoài, ƯU TIÊN dùng lịch sử
    @Override
    public String getExternalRecommendation(String query) { // query là tin nhắn cuối
        // Tạm thời vẫn gọi hàm cũ để đơn giản, nếu muốn dùng lịch sử thì tạo hàm ...WithHistory
        // return getExternalRecommendationWithHistory(List.of(new ChatHistory(...))); // Ví dụ
        String promptText = String.format(
                "Là một trợ lý du lịch, hãy gợi ý NGẮN GỌN (tối đa 3-4 gợi ý chính) về các địa điểm hoặc loại hình du lịch phù hợp với yêu cầu sau: \"%s\". " +
                        "Tập trung vào tên địa điểm/loại hình và một mô tả rất ngắn gọn cho mỗi gợi ý. " +
                        "KHÔNG cần hỏi thêm thông tin người dùng. "+
                        "Sử dụng các dấu gạch đầu dòng (-). " +
                        "Trả lời một cách thân thiện.",
                query
        );
        String reply = callGeminiApi(promptText);
        return (reply != null && !reply.isBlank()) ? reply : "Xin lỗi, mình chưa tìm được gợi ý phù hợp ngay lúc này.";
    }

    // Trò chuyện thông thường, ƯU TIÊN dùng lịch sử
    @Override
    public String getGeneralChatResponse(String message) { // message là tin nhắn cuối
        // Tạm thời vẫn gọi hàm cũ để đơn giản, nếu muốn dùng lịch sử thì tạo hàm ...WithHistory
        // return getGeneralChatResponseWithHistory(List.of(new ChatHistory(...))); // Ví dụ
        String promptText = String.format(
                "Bạn là một chatbot trợ lý du lịch thân thiện. Hãy trả lời tin nhắn sau của người dùng một cách tự nhiên: \"%s\"",
                message);
        String reply = callGeminiApi(promptText);
        return (reply != null && !reply.isBlank()) ? reply : "Xin lỗi, mình chưa hiểu ý bạn lắm.";
    }

    // Tạo câu dẫn cho gợi ý nội bộ, dùng userMessage cuối để xác định ngôn ngữ
    @Override
    public String generateInternalSuggestionIntro(String userMessage, List<String> keywordsFound) {
        String keywordsString = (keywordsFound == null || keywordsFound.isEmpty())
                ? "một số gợi ý du lịch"
                : "'" + String.join(", ", keywordsFound) + "'";
        String promptText = String.format(
                "Dựa trên tin nhắn gốc sau của người dùng: \"%s\"\n\n" +
                        "Hãy tạo một câu giới thiệu NGẮN GỌN và thân thiện bằng ngôn ngữ tương ứng với tin nhắn gốc đó, để thông báo rằng ứng dụng đã tìm thấy gợi ý về %s. " +
                        "Ví dụ: 'Mình tìm thấy một vài gợi ý về %s trong ứng dụng nè:' hoặc 'Here are some suggestions about %s from the app:'. " +
                        "CHỈ trả về câu giới thiệu đó thôi, không thêm lời chào hay thông tin nào khác.",
                userMessage, keywordsString, keywordsString, keywordsString
        );
        String introReply = callGeminiApi(promptText); // Vẫn dùng hàm gọi đơn giản
        return (introReply != null && !introReply.isBlank()) ? introReply : "Đây là một vài gợi ý từ ứng dụng:";
    }

    // --- PHƯƠNG THỨC MỚI ĐỂ XỬ LÝ CÓ LỊCH SỬ (Được gọi từ ChatbotServiceImpl) ---
    // Được gọi khi cần trả lời chat thông thường hoặc gợi ý bên ngoài CÓ dùng lịch sử
    public String getResponseWithHistory(List<ChatHistory> history) {
        if (history == null || history.isEmpty()) {
            return "Xin chào! Bạn cần giúp gì?";
        }
        // Chỉ cần gọi API với cấu trúc contents[]
        return callGeminiApiWithHistory(history, null);
    }


    // --- Hàm gọi API Helper ---

    // Hàm gọi API Gemini chỉ với 1 prompt (Dùng cho analyzeIntent, generateInternalSuggestionIntro)
    private String callGeminiApi(String prompt) {
        if (apiKey == null || apiKey.isBlank()) { logger.error("Gemini API Key is missing!"); return null; }
        String apiUrl = String.format(GEMINI_API_URL_TEMPLATE, DEFAULT_MODEL, apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> safetySettingHarmBlock = Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE");
        Map<String, Object> safetySettingHateSpeech = Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE");
        Map<String, Object> generationConfig = Map.of("temperature", 0.5f);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("contents", List.of(content)); // Chỉ gửi 1 content
        requestBodyMap.put("safetySettings", List.of(safetySettingHarmBlock, safetySettingHateSpeech));
        requestBodyMap.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return extractTextFromGeminiResponse(response.getBody()); // Gọi hàm trích xuất text
            } else {
                logger.error("Error calling Gemini API (single prompt). Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP Error calling Gemini API (single prompt): {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return null;
        } catch (RestClientException | JsonProcessingException e) { // Bắt cả lỗi parse JSON
            logger.error("Error calling Gemini API (single prompt) or parsing response: {}", e.getMessage(), e);
            return null;
        }
    }

    // Hàm gọi API Gemini VỚI LỊCH SỬ
    // additionalPrompt: có thể thêm vào cuối cùng để hướng dẫn thêm
    private String callGeminiApiWithHistory(List<ChatHistory> history, String additionalPrompt) {
        if (apiKey == null || apiKey.isBlank()) { logger.error("Gemini API Key is missing!"); return null; }
        if (history == null || history.isEmpty()) { logger.warn("Attempted to call Gemini with empty history."); return null; }

        String apiUrl = String.format(GEMINI_API_URL_TEMPLATE, DEFAULT_MODEL, apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Xây dựng mảng 'contents' từ lịch sử
        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatHistory msg : history) {
            String role = (msg.getSenderType() == SenderType.USER) ? "user" : "model"; // Map SenderType sang role của API
            Map<String, Object> textPart = Map.of("text", msg.getMessageContent());
            Map<String, Object> content = Map.of("role", role, "parts", List.of(textPart));
            contents.add(content);
        }

        // Thêm prompt bổ sung (nếu có) vào cuối cùng (Gemini thường coi đây là yêu cầu chính)
        if (additionalPrompt != null && !additionalPrompt.isBlank()) {
            Map<String, Object> textPart = Map.of("text", additionalPrompt);
            Map<String, Object> content = Map.of("role", "user", "parts", List.of(textPart));
            contents.add(content);
            logger.debug("Adding additional prompt to Gemini history call.");
        }

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("contents", contents); // Gửi toàn bộ lịch sử

        // Thêm safety settings và generation config
        Map<String, Object> safetySettingHarmBlock = Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE");
        Map<String, Object> safetySettingHateSpeech = Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE");
        Map<String, Object> generationConfig = Map.of("temperature", 0.7f); // Nhiệt độ cho chat
        requestBodyMap.put("safetySettings", List.of(safetySettingHarmBlock, safetySettingHateSpeech));
        requestBodyMap.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);

        logger.debug("Calling Gemini API with history ({} messages)", contents.size());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return extractTextFromGeminiResponse(response.getBody()); // Gọi hàm trích xuất text
            } else {
                logger.error("Error calling Gemini API with history. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP Error calling Gemini API with history: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return null;
        } catch (RestClientException | JsonProcessingException e) {
            logger.error("Error calling Gemini API with history or parsing response: {}", e.getMessage(), e);
            return null;
        }
    }

    // *** HÀM HELPER MỚI: Trích xuất text từ response của Gemini ***
    private String extractTextFromGeminiResponse(String responseBody) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // 1. Kiểm tra bị block do prompt feedback
        if (rootNode.hasNonNull("promptFeedback") &&
                rootNode.path("promptFeedback").hasNonNull("blockReason")) {
            String blockReason = rootNode.path("promptFeedback").path("blockReason").asText();
            logger.warn("Gemini request blocked due to safety settings. Reason: {}", blockReason);
            return "Xin lỗi, yêu cầu của bạn không phù hợp hoặc mình không thể xử lý."; // Trả về thông báo lỗi
        }

        // 2. Kiểm tra candidates
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode firstCandidate = candidates.get(0);

            // 3. Kiểm tra safety ratings của candidate
            JsonNode safetyRatings = firstCandidate.path("safetyRatings");
            boolean safe = true;
            if (safetyRatings.isArray()) {
                for (JsonNode rating : safetyRatings) {
                    // Chỉ chấp nhận NEGLIGIBLE hoặc LOW
                    String probability = rating.path("probability").asText("UNKNOWN");
                    if (!"NEGLIGIBLE".equalsIgnoreCase(probability) && !"LOW".equalsIgnoreCase(probability)) {
                        logger.warn("Gemini response candidate flagged by safety settings: Category={}, Probability={}",
                                rating.path("category").asText("UNKNOWN"), probability);
                        safe = false;
                        break; // Chỉ cần 1 cái không an toàn là đủ
                    }
                }
            }

            // 4. Nếu an toàn, lấy text
            if (safe) {
                JsonNode contentNode = firstCandidate.path("content");
                JsonNode parts = contentNode.path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    String text = parts.get(0).path("text").asText();
                    logger.debug("Extracted safe text from Gemini response: {}", text);
                    return text; // Trả về text hợp lệ
                }
            } else {
                return "Xin lỗi, nội dung được tạo ra không phù hợp để hiển thị."; // Trả về thông báo nếu không an toàn
            }
        }

        // Nếu không có candidates hợp lệ hoặc không có text part
        logger.warn("Could not extract valid text from Gemini response structure: {}", responseBody);
        return null; // Trả về null nếu không có text hợp lệ
    }
}