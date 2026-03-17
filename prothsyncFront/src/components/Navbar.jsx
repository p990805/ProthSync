import { useEffect, useState } from 'react';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import useAuthStore from '../store/useAuthStore';
import { apiFetch } from '../utils/api';
import './Navbar.css';

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  const accessToken = useAuthStore((state) => state.accessToken);
  const user = useAuthStore((state) => state.user);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const navigate = useNavigate();

  const isLoggedIn = accessToken !== null;

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 50);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleLogout = async () => {
    try {
      await apiFetch('/auth/logout', { method: 'POST' });
    } catch {
      // 로그아웃 API 실패해도 클라이언트에서는 정리
    } finally {
      clearAuth();
      setMobileOpen(false);
      navigate('/');
    }
  };

  return (
    <nav className={`nav${scrolled ? ' nav--scrolled' : ''}`}>
      <Link to="/" className="nav-logo">
        <div className="nav-logo-icon" />
        <div className="nav-logo-text">
          Proth<span>Sync</span>
        </div>
      </Link>

      <button
        className={`nav-hamburger${mobileOpen ? ' open' : ''}`}
        onClick={() => setMobileOpen(!mobileOpen)}
        aria-label="메뉴 열기"
      >
        <span /><span /><span />
      </button>

      <div className={`nav-links${mobileOpen ? ' nav-links--open' : ''}`}>
        <NavLink to="/features" onClick={() => setMobileOpen(false)}>기능</NavLink>
        <NavLink to="/users" onClick={() => setMobileOpen(false)}>사용자</NavLink>
        <NavLink to="/cases" onClick={() => setMobileOpen(false)}>의뢰</NavLink>
        <NavLink to="/feed" onClick={() => setMobileOpen(false)}>피드</NavLink>

        {isLoggedIn ? (
          <>
            <span className="nav-user">{user?.nickName || user?.userName}</span>
            <button className="nav-cta nav-logout-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </>
        ) : (
          <Link to="/login" className="nav-cta" onClick={() => setMobileOpen(false)}>
            시작하기 →
          </Link>
        )}
      </div>
    </nav>
  );
}