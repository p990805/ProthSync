package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.PostImage;

public record PostImageResponseDTO(
    Long postImageId,
    String imagePath,
    String originalFileName,
    int displayOrder
) {

    public static PostImageResponseDTO from(PostImage postImage) {
        return new PostImageResponseDTO(
            postImage.getPostImageId(),
            postImage.getImagePath(),
            postImage.getOriginalFileName(),
            postImage.getDisplayOrder()
        );
    }
}
