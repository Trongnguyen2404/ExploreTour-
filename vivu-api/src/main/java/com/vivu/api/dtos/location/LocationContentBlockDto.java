package com.vivu.api.dtos.location;

import com.vivu.api.entities.LocationContentBlock;
import com.vivu.api.enums.BlockType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationContentBlockDto {
    private Integer id;
    private BlockType blockType; // TEXT or IMAGE
    private String contentValue; // Text content or Image URL
    private String caption; // Caption for image
    private Integer orderIndex;

    public static LocationContentBlockDto fromEntity(LocationContentBlock block) {
        if (block == null) return null;
        return LocationContentBlockDto.builder()
                .id(block.getId())
                .blockType(block.getBlockType())
                .contentValue(block.getContentValue())
                .caption(block.getCaption())
                .orderIndex(block.getOrderIndex())
                .build();
    }
}