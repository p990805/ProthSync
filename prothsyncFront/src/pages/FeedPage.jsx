import { useState, useEffect, useCallback, useRef } from 'react';
import { apiFetch } from '../utils/api';
import PostCard from '../components/PostCard';
import './FeedPage.css';

const CATEGORIES = [
  { value: null, label: '전체', emoji: '📋' },
  { value: 'WORK_SHOWCASE', label: '작업물', emoji: '🦷' },
  { value: 'EQUIPMENT', label: '장비/재료', emoji: '⚙️' },
  { value: 'TECHNIQUE', label: '기술/노하우', emoji: '💡' },
  { value: 'DAILY', label: '일상', emoji: '☕' },
  { value: 'QUESTION', label: '질문', emoji: '❓' },
  { value: 'INFO', label: '정보공유', emoji: '📢' },
];

export default function FeedPage() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [hasNext, setHasNext] = useState(true);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [category, setCategory] = useState(null);
  const [error, setError] = useState('');

  const observerRef = useRef(null);
  const bottomRef = useRef(null);

  // 피드 불러오기
  const fetchFeed = useCallback(async (pageNum, reset = false) => {
    if (loading) return;
    setLoading(true);
    setError('');

    try {
      let path = `/feed?page=${pageNum}&size=10&sort=createdAt,desc`;
      if (category) {
        path += `&category=${category}`;
      }

      const data = await apiFetch(path);

      setPosts((prev) => reset ? data.data : [...prev, ...data.data]);
      setHasNext(data.pageable.hasNext);
      setPage(pageNum);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
      setInitialLoading(false);
    }
  }, [category, loading]);

  // 카테고리 변경 시 리셋
  useEffect(() => {
    setPosts([]);
    setPage(0);
    setHasNext(true);
    setInitialLoading(true);
    fetchFeed(0, true);
  }, [category]);

  // 무한 스크롤 — IntersectionObserver
  useEffect(() => {
    if (observerRef.current) observerRef.current.disconnect();

    observerRef.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasNext && !loading) {
          fetchFeed(page + 1);
        }
      },
      { threshold: 0.1 }
    );

    if (bottomRef.current) {
      observerRef.current.observe(bottomRef.current);
    }

    return () => observerRef.current?.disconnect();
  }, [hasNext, loading, page, fetchFeed]);

  // PostCard에서 좋아요/북마크 업데이트 시
  const handlePostUpdate = (updatedPost) => {
    setPosts((prev) =>
      prev.map((p) => (p.postId === updatedPost.postId ? updatedPost : p))
    );
  };

  return (
    <div className="feed-page">
      <div className="feed-layout">
        {/* Sidebar */}
        <aside className="feed-sidebar">
          <div className="sidebar-card">
            <h3 className="sidebar-title">카테고리</h3>
            <nav className="category-nav">
              {CATEGORIES.map((c) => (
                <button
                  key={c.label}
                  className={`category-item${category === c.value ? ' active' : ''}`}
                  onClick={() => setCategory(c.value)}
                >
                  <span className="category-emoji">{c.emoji}</span>
                  <span className="category-label">{c.label}</span>
                </button>
              ))}
            </nav>
          </div>

          <div className="sidebar-card">
            <h3 className="sidebar-title">인기 해시태그</h3>
            <div className="trending-tags">
              <span className="trending-tag">#지르코니아</span>
              <span className="trending-tag">#임플란트크라운</span>
              <span className="trending-tag">#CAD_CAM</span>
              <span className="trending-tag">#교정장치</span>
              <span className="trending-tag">#도재</span>
            </div>
          </div>
        </aside>

        {/* Feed */}
        <main className="feed-main">
          {/* Header */}
          <div className="feed-header">
            <h1 className="feed-title">피드</h1>
            <p className="feed-subtitle">팔로우한 전문가들의 최신 게시물</p>
          </div>

          {/* Category filter — mobile */}
          <div className="category-tabs-mobile">
            {CATEGORIES.map((c) => (
              <button
                key={c.label}
                className={`cat-tab${category === c.value ? ' active' : ''}`}
                onClick={() => setCategory(c.value)}
              >
                {c.emoji} {c.label}
              </button>
            ))}
          </div>

          {/* Error */}
          {error && <div className="feed-error">{error}</div>}

          {/* Initial Loading */}
          {initialLoading && (
            <div className="feed-skeleton">
              {[1, 2, 3].map((i) => (
                <div className="skeleton-card" key={i}>
                  <div className="skeleton-header">
                    <div className="skeleton-avatar" />
                    <div className="skeleton-lines">
                      <div className="skeleton-line w60" />
                      <div className="skeleton-line w40" />
                    </div>
                  </div>
                  <div className="skeleton-image" />
                  <div className="skeleton-body">
                    <div className="skeleton-line w80" />
                    <div className="skeleton-line w50" />
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Empty state */}
          {!initialLoading && posts.length === 0 && !error && (
            <div className="feed-empty">
              <div className="empty-icon">📭</div>
              <h3>아직 게시물이 없습니다</h3>
              <p>전문가를 팔로우하면 피드에 게시물이 표시됩니다</p>
            </div>
          )}

          {/* Post list */}
          <div className="feed-list">
            {posts.map((post) => (
              <PostCard
                key={post.postId}
                post={post}
                onUpdate={handlePostUpdate}
              />
            ))}
          </div>

          {/* Loading more */}
          {loading && !initialLoading && (
            <div className="feed-loading">
              <div className="spinner" />
              <span>불러오는 중...</span>
            </div>
          )}

          {/* End of feed */}
          {!hasNext && posts.length > 0 && (
            <div className="feed-end">
              모든 게시물을 확인했습니다
            </div>
          )}

          {/* Infinite scroll trigger */}
          <div ref={bottomRef} className="scroll-trigger" />
        </main>
      </div>
    </div>
  );
}