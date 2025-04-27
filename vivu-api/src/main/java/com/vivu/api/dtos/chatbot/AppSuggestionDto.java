package com.vivu.api.dtos.chatbot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppSuggestionDto {
    private String type; // "TOUR" hoặc "LOCATION"
    private Integer id;
    private String title;
    private String summary; // Mô tả ngắn hoặc trích đoạn
    private String imageUrl; // Ảnh đại diện
}