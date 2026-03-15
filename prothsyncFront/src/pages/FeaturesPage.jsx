import Features from '../components/Features';
import CTA from '../components/CTA';
import './PageHeader.css';

export default function FeaturesPage() {
  return (
    <>
      <div className="page-header">
        <div className="page-header-inner">
          <div className="section-label">Features</div>
          <h1 className="section-title">
            ProthSync의 <em>핵심 기능</em>
          </h1>
          <p className="section-desc">
            치과 기공 전문가를 위해 설계된 모든 기능을 살펴보세요
          </p>
        </div>
      </div>
      <Features />
      <CTA />
    </>
  );
}