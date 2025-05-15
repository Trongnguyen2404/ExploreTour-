package com.vivu.api.dtos.location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class LocationRequestDto { // Dùng cho Create/Update Location

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Header image URL cannot be blank")
    @Size(max = 512)
    private String headerImageUrl;

// Average rating không cần nhập

    @NotEmpty(message = "Content blocks cannot be empty") // Phải có ít nhất 1 block
    @Valid // Validate các phần tử trong list
    private List<LocationContentBlockRequestDto> contentBlocks;
}