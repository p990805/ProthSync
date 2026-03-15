import useScrollReveal from '../hooks/useScrollReveal';
import './CasesShowcase.css';

const CASE_PILLS = [
  { label: '크라운', cls: 'cp-1' },
  { label: '브릿지', cls: 'cp-2' },
  { label: '의치', cls: 'cp-3' },
  { label: '임플란트', cls: 'cp-4' },
  { label: '인레이 / 온레이', cls: 'cp-5' },
  { label: '비니어', cls: 'cp-6' },
  { label: '교정장치', cls: 'cp-7' },
  { label: '기타', cls: 'cp-8' },
];

const POST_CATS = [
  { emoji: '🦷', name: '작업물' },
  { emoji: '⚙️', name: '장비/재료' },
  { emoji: '💡', name: '기술/노하우' },
  { emoji: '☕', name: '일상' },
  { emoji: '❓', name: '질문' },
  { emoji: '📢', name: '정보공유' },
];

export default function CasesShowcase() {
  const wrapRef = useScrollReveal();

  return (
    <section className="section cases-section" id="cases" ref={wrapRef}>
      <div className="section-header">
        <div className="section-label">Case Categories</div>
        <h2 className="section-title">
          <em>모든</em> 보철 카테고리를 지원합니다
        </h2>
        <p className="section-desc">
          크라운부터 교정장치까지, 필요한 모든 유형의 의뢰를 등록하고 제안받으세요
        </p>
      </div>

      <div className="cases-grid">
        {CASE_PILLS.map((c, i) => (
          <div className={`case-pill ${c.cls} reveal reveal-delay-${Math.min(i, 4)}`} key={c.cls}>
            <div className="dot" />
            {c.label}
          </div>
        ))}
      </div>

      <div className="post-cats">
        {POST_CATS.map((p, i) => (
          <div className={`post-cat reveal reveal-delay-${Math.min(i, 3)}`} key={p.name}>
            <div className="post-cat-icon">{p.emoji}</div>
            <div className="post-cat-name">{p.name}</div>
          </div>
        ))}
      </div>
    </section>
  );
}