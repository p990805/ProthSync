package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.CommentCreateRequestDTO;
import com.prothsync.prothsync.dto.CommentResponseDTO;
import com.prothsync.prothsync.dto.CommentUpdateRequestDTO;
import com.prothsync.prothsync.entity.post.Comment;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.CommentRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDTO createComment(Long postId, Long userId, CommentCreateRequestDTO request) {
        Post post = findPostOrThrow(postId);

        Comment comment = Comment.createComment(postId, userId, request.content());
        Comment savedComment = commentRepository.save(comment);

        post.incrementCommentCount();
        postRepository.save(post);

        return CommentResponseDTO.from(savedComment);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDTO> getComments(Long postId, Pageable pageable) {
        findPostOrThrow(postId);

        Page<Comment> commentPage = commentRepository.findAllByPostId(postId, pageable);

        List<CommentResponseDTO> comments = commentPage.getContent().stream()
            .map(CommentResponseDTO::from)
            .toList();

        return PageResponse.of(comments, commentPage);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, Long userId, CommentUpdateRequestDTO request) {
        Comment comment = findCommentOrThrow(commentId);
        validateCommentOwner(comment, userId);

        comment.updateContent(request.content());
        Comment updatedComment = commentRepository.save(comment);

        return CommentResponseDTO.from(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentOrThrow(commentId);
        validateCommentOwner(comment, userId);

        Post post = findPostOrThrow(comment.getPostId());
        post.decrementCommentCount();
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteAllByPostId(Long postId) {
        commentRepository.deleteAllByPostId(postId);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));
    }

    private Comment findCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.isOwnedBy(userId)) {
            throw new BusinessException(PostErrorCode.COMMENT_ACCESS_DENIED);
        }
    }
}