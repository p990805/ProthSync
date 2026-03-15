import useScrollReveal from '../hooks/useScrollReveal';
import './Features.css';

const FEATURES = [
  {
    key: 'portfolio',
    colorClass: 'fi-portfolio',
    title: '작업 포트폴리오',
    desc: '나만의 기공 작업물을 체계적으로 기록하고 공유하세요. 카테고리별 분류, 이미지 갤러리, 해시태그로 전문성을 어필할 수 있습니다.',
    tags: ['작업물', '장비/재료', '기술/노하우'],
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <rect x="3" y="3" width="18" height="18" rx="2" />
        <circle cx="8.5" cy="8.5" r="1.5" />
        <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21" />
      </svg>
    ),
  },
  {
    key: 'outsource',
    colorClass: 'fi-outsource',
    title: '외주 의뢰 매칭',
    desc: '보철물 외주가 필요하신가요? 의뢰를 등록하면 적합한 기공소와 기공사가 제안서를 보내드립니다. 크라운부터 교정장치까지 모든 카테고리를 지원합니다.',
    tags: ['의뢰 등록', '제안 수신', '실시간 알림'],
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M16 3h5v5" /><path d="M4 20 21 3" />
        <path d="M21 16v5h-5" /><path d="M15 15 3 21" />
      </svg>
    ),
  },
  {
    key: 'location',
    colorClass: 'fi-location',
    title: '위치 기반 검색',
    desc: '내 주변의 치과와 기공소를 한눈에. Haversine 기반 거리 계산으로 정확한 반경 내 업체를 찾아보세요. 가까운 파트너와 빠르게 연결됩니다.',
    tags: ['근처 기공소', '근처 치과', '거리순 정렬'],
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
        <circle cx="12" cy="10" r="3" />
      </svg>
    ),
  },
  {
    key: 'community',
    colorClass: 'fi-community',
    title: '전문가 커뮤니티',
    desc: '기공사, 치과의사, 학생이 함께하는 커뮤니티. 기술 노하우를 공유하고, 질문에 답하며, 업계 트렌드를 함께 만들어 가세요.',
    tags: ['정보공유', '질문', '일상'],
    svg: (
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
        <circle cx="9" cy="7" r="4" />
        <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
        <path d="M16 3.13a4 4 0 0 1 0 7.75" />
      </svg>
    ),
  },
];

export default function Features() {
  const wrapRef = useScrollReveal();

  return (
    <section className="section" id="features" ref={wrapRef}>
      <div className="section-header">
        <div className="section-label">Features</div>
        <h2 className="section-title">
          필요한 <em>모든 것</em>이<br />하나의 플랫폼에
        </h2>
        <p className="section-desc">
          포트폴리오 관리부터 외주 매칭, 위치 기반 검색까지. 치과 기공 업무의 모든 흐름을 ProthSync에서.
        </p>
      </div>

      <div className="features-grid">
        {FEATURES.map((f, i) => (
          <div className={`feature-card reveal reveal-delay-${i}`} key={f.key}>
            <div className={`feature-icon ${f.colorClass}`}>{f.svg}</div>
            <div className="feature-title">{f.title}</div>
            <div className="feature-desc">{f.desc}</div>
            <div className="feature-tags">
              {f.tags.map((t) => (
                <span key={t}>{t}</span>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}