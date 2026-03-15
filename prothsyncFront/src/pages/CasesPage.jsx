import CasesShowcase from '../components/CasesShowcase';
import CTA from '../components/CTA';
import './PageHeader.css';

export default function CasesPage() {
  return (
    <>
      <div className="page-header">
        <div className="page-header-inner">
          <div className="section-label">Case Categories</div>
          <h1 className="section-title">
            <em>의뢰</em> 카테고리
          </h1>
          <p className="section-desc">
            크라운, 브릿지, 임플란트 등 모든 보철물 유형의 외주를 한 곳에서 관리하세요
          </p>
        </div>
      </div>
      <CasesShowcase />
      <CTA />
    </>
  );
}