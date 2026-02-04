package com.prothsync.prothsync.entity.post;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postImageId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(nullable = false, length = 500)
    private String imagePath;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private int displayOrder;

    private PostImage(Long postId, String imagePath, String originalFileName,
        String storedFileName, int displayOrder) {
        this.postId = postId;
        this.imagePath = imagePath;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.displayOrder = displayOrder;
    }

    public static PostImage create(Long postId, String imagePath, String originalFileName,
        String storedFileName, int displayOrder) {
        validatePostId(postId);
        validateImagePath(imagePath);
        validateOriginalFileName(originalFileName);
        validateStoredFileName(storedFileName);
        validateDisplayOrder(displayOrder);

        return new PostImage(postId, imagePath, originalFileName, storedFileName, displayOrder);
    }

    private static void validatePostId(Long postId) {
        if (postId == null) {
            throw new BusinessException(PostErrorCode.POST_ID_NULL);
        }
    }

    private static void validateImagePath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new BusinessException(PostErrorCode.IMAGE_PATH_NULL);
        }
    }

    private static void validateOriginalFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BusinessException(PostErrorCode.IMAGE_ORIGINAL_NAME_NULL);
        }
    }

    private static void validateStoredFileName(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            throw new BusinessException(PostErrorCode.IMAGE_STORED_NAME_NULL);
        }
    }

    private static void validateDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new BusinessException(PostErrorCode.IMAGE_DISPLAY_ORDER_INVALID);
        }
    }

    public void updateDisplayOrder(int displayOrder) {
        validateDisplayOrder(displayOrder);
        this.displayOrder = displayOrder;
    }
}
