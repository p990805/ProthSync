import { useState } from 'react';
import { apiFetch } from '../utils/api';
import './PostCard.css';

const CATEGORY_MAP = {
  WORK_SHOWCASE: { label: '작업물', emoji: '🦷' },
  EQUIPMENT: { label: '장비/재료', emoji: '⚙️' },
  TECHNIQUE: { label: '기술/노하우', emoji: '💡' },
  DAILY: { label: '일상', emoji: '☕' },
  QUESTION: { label: '질문', emoji: '❓' },
  INFO: { label: '정보공유', emoji: '📢' },
};

export default function PostCard({ post, onUpdate }) {
  const [imgIndex, setImgIndex] = useState(0);
  const [likeAnim, setLikeAnim] = useState(false);

  const cat = CATEGORY_MAP[post.category] || { label: post.category, emoji: '' };
  const images = [...(post.images || [])].sort((a, b) => a.displayOrder - b.displayOrder);
  const hasImages = images.length > 0;

  const timeAgo = formatTimeAgo(post.createdAt);

  const handleLike = async () => {
    try {
      const data = await apiFetch(`/posts/${post.postId}/likes`, { method: 'POST' });
      setLikeAnim(true);
      setTimeout(() => setLikeAnim(false), 400);
      onUpdate?.({
        ...post,
        isLiked: data.liked,
        likeCount: data.likeCount,
      });
    } catch (err) {
      console.error(err);
    }
  };

  const handleBookmark = async () => {
    try {
      await apiFetch(`/posts/${post.postId}/bookmarks`, { method: 'POST' });
      onUpdate?.({
        ...post,
        isBookmarked: !post.isBookmarked,
      });
    } catch (err) {
      console.error(err);
    }
  };

  const prevImg = () => setImgIndex((i) => Math.max(0, i - 1));
  const nextImg = () => setImgIndex((i) => Math.min(images.length - 1, i + 1));

  return (
    <article className="post-card">
      {/* Header */}
      <div className="post-header">
        <div className="post-avatar">
          {post.userId?.toString().slice(-2) || 'U'}
        </div>
        <div className="post-user-info">
          <div className="post-username">User #{post.userId}</div>
          <div className="post-time">{timeAgo}</div>
        </div>
        <div className="post-category-tag">
          <span className="cat-emoji">{cat.emoji}</span>
          {cat.label}
        </div>
      </div>

      {/* Image Carousel */}
      {hasImages && (
        <div className="post-images">
          <div
            className="post-images-track"
            style={{ transform: `translateX(-${imgIndex * 100}%)` }}
          >
            {images.map((img) => (
              <div className="post-image-slide" key={img.postImageId}>
                <img src={img.imagePath} alt={img.originalFileName || '게시물 이미지'} />
              </div>
            ))}
          </div>

          {images.length > 1 && (
            <>
              {imgIndex > 0 && (
                <button className="img-nav img-nav-prev" onClick={prevImg}>‹</button>
              )}
              {imgIndex < images.length - 1 && (
                <button className="img-nav img-nav-next" onClick={nextImg}>›</button>
              )}
              <div className="img-dots">
                {images.map((_, i) => (
                  <span
                    key={i}
                    className={`img-dot${i === imgIndex ? ' active' : ''}`}
                    onClick={() => setImgIndex(i)}
                  />
                ))}
              </div>
            </>
          )}
        </div>
      )}

      {/* Actions */}
      <div className="post-actions">
        <button
          className={`action-btn like-btn${post.isLiked ? ' liked' : ''}${likeAnim ? ' pop' : ''}`}
          onClick={handleLike}
        >
          <HeartIcon filled={post.isLiked} />
          <span>{post.likeCount}</span>
        </button>
        <button className="action-btn">
          <CommentIcon />
          <span>{post.commentCount}</span>
        </button>
        <button className="action-btn">
          <EyeIcon />
          <span>{post.viewCount}</span>
        </button>
        <button
          className={`action-btn bookmark-btn${post.isBookmarked ? ' bookmarked' : ''}`}
          onClick={handleBookmark}
        >
          <BookmarkIcon filled={post.isBookmarked} />
        </button>
      </div>

      {/* Content */}
      <div className="post-content">
        <p>{post.content}</p>
      </div>

      {/* Hashtags */}
      {post.hashtags?.length > 0 && (
        <div className="post-hashtags">
          {post.hashtags.map((h) => (
            <span key={h.hashtagId} className="hashtag">
              #{h.displayName}
            </span>
          ))}
        </div>
      )}
    </article>
  );
}

/* ── Helper ── */
function formatTimeAgo(dateStr) {
  const now = new Date();
  const date = new Date(dateStr);
  const diff = Math.floor((now - date) / 1000);

  if (diff < 60) return '방금 전';
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;
  return date.toLocaleDateString('ko-KR');
}

/* ── Icons ── */
function HeartIcon({ filled }) {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill={filled ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
    </svg>
  );
}

function CommentIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function BookmarkIcon({ filled }) {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill={filled ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
    </svg>
  );
}