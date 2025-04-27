package com.vivu.api.dtos.location;

import com.vivu.api.enums.BlockType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationContentBlockRequestDto {

    @NotNull(message = "Order index cannot be null")
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;

    @NotNull(message = "Block type cannot be null")
    private BlockType blockType; // TEXT or IMAGE

    @NotBlank(message = "Content value cannot be blank") // Dù là text hay URL ảnh
    private String contentValue;

    @Size(max = 255)
    private String caption; // Chú thích cho ảnh, có thể null
}