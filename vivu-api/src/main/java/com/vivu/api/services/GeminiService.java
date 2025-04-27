package com.vivu.api.services;

import java.util.List;

public interface GeminiService {

    record GeminiAnalysisResult(String intent, List<String> keywords) {}

    GeminiAnalysisResult analyzeIntent(String message);
    String getExternalRecommendation(String query);
    String getGeneralChatResponse(String message);

    // *** THÊM PHƯƠNG THỨC NÀY ***
    // userMessage: Tin nhắn gốc của người dùng (để Gemini biết ngữ cảnh ngôn ngữ)
    // keywordsFound: Các từ khóa thực sự dùng để tìm ra gợi ý
    String generateInternalSuggestionIntro(String userMessage, List<String> keywordsFound);
}