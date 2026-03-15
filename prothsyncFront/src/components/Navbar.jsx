import { useEffect, useState } from 'react';
import { NavLink, Link } from 'react-router-dom';
import './Navbar.css';

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 50);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

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
        <Link to="/login" className="nav-cta" onClick={() => setMobileOpen(false)}>
          시작하기 →
        </Link>
      </div>
    </nav>
  );
}