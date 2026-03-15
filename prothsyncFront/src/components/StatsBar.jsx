import useCounterAnimation from '../hooks/useCounterAnimation';
import './StatsBar.css';

const STATS = [
  { target: 2847, suffix: '+', label: '등록 전문가' },
  { target: 1523, suffix: '건', label: '완료된 의뢰' },
  { target: 456, suffix: '개', label: '등록 기공소' },
  { target: null, label: '평균 만족도' },
];

export default function StatsBar() {
  return (
    <div className="stats-bar">
      <div className="stats-inner">
        {STATS.map((s, i) => (
          <StatItem key={i} {...s} delay={i} />
        ))}
      </div>
    </div>
  );
}

function StatItem({ target, suffix, label, delay }) {
  const { ref, count } = useCounterAnimation(target);

  return (
    <div className={`stat-item reveal reveal-delay-${delay}`} ref={ref}>
      <div className="stat-num">
        {target ? (
          <>
            {count.toLocaleString()}
            <span>{suffix}</span>
          </>
        ) : (
          <>
            4.8<span>/5</span>
          </>
        )}
      </div>
      <div className="stat-label">{label}</div>
    </div>
  );
}