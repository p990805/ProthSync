import UserTypes from '../components/UserTypes';
import StatsBar from '../components/StatsBar';
import CTA from '../components/CTA';
import './PageHeader.css';

export default function UsersPage() {
  return (
    <>
      <div className="page-header">
        <div className="page-header-inner">
          <div className="section-label">For Everyone</div>
          <h1 className="section-title">
            <em>모든 전문가</em>를 위한 플랫폼
          </h1>
          <p className="section-desc">
            치과, 기공소, 기공사, 학생 — 각자의 역할에 맞는 최적의 경험을 제공합니다
          </p>
        </div>
      </div>
      <UserTypes />
      <StatsBar />
      <CTA />
    </>
  );
}