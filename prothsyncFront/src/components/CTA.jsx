import useScrollReveal from '../hooks/useScrollReveal';
import './CTA.css';

export default function CTA() {
  const wrapRef = useScrollReveal();

  return (
    <section className="cta" ref={wrapRef}>
      <div className="cta-inner reveal">
        <h2 className="cta-title">
          지금 <em>ProthSync</em>와<br />함께 시작하세요
        </h2>
        <p className="cta-desc">치과 기공 업계의 새로운 연결, 무료로 경험해 보세요</p>
        <button className="cta-btn">무료 회원가입 →</button>
      </div>
    </section>
  );
}