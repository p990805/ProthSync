import useScrollReveal from '../hooks/useScrollReveal';
import './UserTypes.css';

const TYPES = [
  {
    cls: 'ut-clinic',
    title: '치과',
    desc: '신뢰할 수 있는 기공소를 찾고, 외주 의뢰를 등록하세요',
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M3 21h18" /><path d="M5 21V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16" />
        <path d="M9 21v-4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4" /><path d="M10 9h4" /><path d="M12 7v4" />
      </svg>
    ),
  },
  {
    cls: 'ut-lab',
    title: '기공소',
    desc: '포트폴리오를 공개하고, 새로운 클라이언트와 연결되세요',
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M9 3h6v4H9z" /><path d="M9 7 5 21h14L15 7" /><path d="M8 14h8" />
      </svg>
    ),
  },
  {
    cls: 'ut-tech',
    title: '기공사',
    desc: '당신의 기술력을 보여주고, 최적의 의뢰를 받아보세요',
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" />
      </svg>
    ),
  },
  {
    cls: 'ut-student',
    title: '학생',
    desc: '선배 기공사들의 작업물을 배우고, 업계 네트워크를 쌓으세요',
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M22 10v6M2 10l10-5 10 5-10 5z" />
        <path d="M6 12v5c0 1.1 2.7 2 6 2s6-.9 6-2v-5" />
      </svg>
    ),
  },
];

export default function UserTypes() {
  const wrapRef = useScrollReveal();

  return (
    <section className="user-types" id="users" ref={wrapRef}>
      <div className="section-header">
        <div className="section-label">For Everyone</div>
        <h2 className="section-title">
          누구든 <em>환영</em>합니다
        </h2>
        <p className="section-desc">치과 업계의 모든 구성원을 위한 맞춤 경험</p>
      </div>

      <div className="ut-grid">
        {TYPES.map((t, i) => (
          <div className={`ut-card ${t.cls} reveal reveal-delay-${i}`} key={t.cls}>
            <div className="ut-icon">{t.svg}</div>
            <div className="ut-title">{t.title}</div>
            <div className="ut-desc">{t.desc}</div>
          </div>
        ))}
      </div>
    </section>
  );
}