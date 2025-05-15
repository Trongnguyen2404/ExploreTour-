package com.vivu.api.dtos.location;

import com.vivu.api.enums.BlockType; // Đảm bảo import này đúng
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LocationContentBlockRequestDto {

    @NotNull(message = "Order index cannot be null")
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;

    @NotNull(message = "Block type cannot be null")
    private BlockType blockType; // TEXT or IMAGE

    // Không còn @NotBlank nữa, vì có thể là file upload
    private String contentValue; // Dùng cho TEXT, hoặc URL ảnh nếu blockType là IMAGE và người dùng dán link



    @Size(max = 255)
    private String caption;
}