import { Link } from 'react-router-dom';
import './Footer.css';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <Link to="/" className="footer-logo">
          Proth<span>Sync</span>
        </Link>
        <div className="footer-links">
          <a href="#">이용약관</a>
          <a href="#">개인정보처리방침</a>
          <a href="#">문의하기</a>
          <a href="#">API 문서</a>
        </div>
        <div className="footer-copy">&copy; 2026 ProthSync. All rights reserved.</div>
      </div>
    </footer>
  );
}