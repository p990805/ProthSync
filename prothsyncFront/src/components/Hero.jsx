import { Link } from 'react-router-dom';
import './Hero.css';

export default function Hero() {
  return (
    <section className="hero">
      <div className="hero-content">
        {/* Left — Copy */}
        <div className="hero-left">
          <div className="hero-badge">
            <span />
            치과 기공 전문 네트워크
          </div>
          <h1 className="hero-title">
            기공의 가치를<br /><em>연결</em>하다
          </h1>
          <p className="hero-desc">
            치과와 기공소, 기공사와 학생을 하나로 잇는 전문 플랫폼.
            포트폴리오 공유부터 외주 매칭, 위치 기반 검색까지 —
            당신의 기술이 빛나는 곳.
          </p>
          <div className="hero-actions">
            <button className="btn-primary">무료로 시작하기</button>
            <Link to="/features" className="btn-secondary">더 알아보기</Link>
          </div>
        </div>

        {/* Right — Visual cards */}
        <div className="hero-right">
          <div className="hero-visual">
            {/* Portfolio card */}
            <div className="hero-card hero-card-main">
              <div className="card-img">
                <div className="tooth-grid">
                  {[...Array(6)].map((_, i) => (
                    <div className="tooth-icon" key={i} />
                  ))}
                </div>
              </div>
              <div className="card-body">
                <div className="card-tag">작업물</div>
                <div className="card-title">지르코니아 크라운 케이스</div>
                <div className="card-meta">
                  <span><HeartIcon /> 128</span>
                  <span><EyeIcon /> 2.4k</span>
                </div>
              </div>
            </div>

            {/* Profile card */}
            <div className="hero-card hero-card-float">
              <div className="float-header">
                <div className="float-avatar">JK</div>
                <div className="float-info">
                  <div className="name">박주찬 기공사</div>
                  <div className="role">하이리움 치과기공소</div>
                </div>
              </div>
              <div className="float-stats">
                <StatBubble num="999" label="작업물" />
                <StatBubble num="5.0" label="평점" />
                <StatBubble num="1.2k" label="팔로워" />
              </div>
            </div>

            {/* Notification badge */}
            <div className="hero-card hero-card-badge">
              <div className="badge-icon">
                <CheckCircleIcon />
              </div>
              <div className="badge-text">
                <div className="bt-title">새 의뢰 매칭</div>
                <div className="bt-sub">임플란트 크라운 · 서울 강남</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function StatBubble({ num, label }) {
  return (
    <div className="float-stat">
      <div className="num">{num}</div>
      <div className="label">{label}</div>
    </div>
  );
}

function HeartIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function CheckCircleIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
      <polyline points="22 4 12 14.01 9 11.01" />
    </svg>
  );
}