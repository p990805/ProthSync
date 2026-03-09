package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.PostImageResponseDTO;
import com.prothsync.prothsync.entity.post.PostImage;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.repository.repository.PostImageRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private static final int MAX_IMAGES_PER_POST = 10;

    private final PostImageRepository postImageRepository;

    // TODO: 현재는 s3 도입이 안되어있어서 추후에 추가되면 url 다 수정할 것
    @Transactional
    public List<PostImage> saveImages(Long postId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return Collections.emptyList();
        }

        validateImageCount(imageUrls);

        List<PostImage> images = new java.util.ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            // TODO: S3 도입 시 실제 파일 업로드 처리로 교체
            PostImage image = PostImage.create(postId, url, url, url, i);
            images.add(image);
        }

        return postImageRepository.saveAll(images);
    }

    @Transactional
    public List<PostImage> replaceImages(Long postId, Long userId, List<String> imageUrls) {
        softDeleteAllByPostId(postId, userId);
        return saveImages(postId, imageUrls);
    }

    @Transactional(readOnly = true)
    public List<PostImageResponseDTO> getImagesByPostId(Long postId) {
        return postImageRepository.findAllByPostIdOrderByDisplayOrder(postId).stream()
            .map(PostImageResponseDTO::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public String getThumbnailUrl(Long postId) {
        List<PostImage> images = postImageRepository.findAllByPostIdOrderByDisplayOrder(postId);
        return images.isEmpty() ? null : images.get(0).getImagePath();
    }

    @Transactional
    public void softDeleteAllByPostId(Long postId, Long userId) {
        // TODO: S3 도입 시 실제 파일 삭제 처리 추가
        postImageRepository.softDeleteAllByPostId(postId, userId);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getThumbnailUrls(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return postImageRepository.findFirstImagesByPostIds(postIds).stream()
            .collect(Collectors.toMap(PostImage::getPostId, PostImage::getImagePath));
    }

    @Transactional(readOnly = true)
    public Map<Long, List<PostImageResponseDTO>> getImagesByPostIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<PostImage> allImages = postImageRepository.findAllByPostIdsOrderByDisplayOrder(postIds);

        return allImages.stream()
            .collect(Collectors.groupingBy(
                PostImage::getPostId,
                Collectors.mapping(PostImageResponseDTO::from, Collectors.toList())
            ));
    }

    private void validateImageCount(List<String> imageUrls) {
        if (imageUrls.size() > MAX_IMAGES_PER_POST) {
            throw new BusinessException(PostErrorCode.IMAGE_LIMIT_EXCEEDED);
        }
    }
}