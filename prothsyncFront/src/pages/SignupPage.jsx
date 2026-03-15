import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css';

const USER_TYPES = [
  { value: 'DENTAL_CLINIC', label: '치과' },
  { value: 'DENTAL_LAB', label: '기공소' },
  { value: 'DENTAL_TECHNICIAN', label: '기공사' },
  { value: 'STUDENT', label: '학생' },
];

export default function SignupPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    userName: '',
    password: '',
    passwordConfirm: '',
    nickName: '',
    birthday: '',
    address: '',
    email: '',
    userType: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
  };

  const handleTypeSelect = (type) => {
    setForm({ ...form, userType: type });
    setError('');
  };

  const validateStep1 = () => {
    if (!form.userType) return '사용자 유형을 선택해 주세요.';
    if (!form.userName || form.userName.length < 5 || form.userName.length > 20)
      return '아이디는 5자 이상 20자 이하여야 합니다.';
    if (!form.email) return '이메일을 입력해 주세요.';
    return '';
  };

  const validateStep2 = () => {
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!pwRegex.test(form.password))
      return '비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 포함해야 합니다.';
    if (form.password !== form.passwordConfirm) return '비밀번호가 일치하지 않습니다.';
    if (!form.nickName || form.nickName.length > 10)
      return '닉네임은 1~10자 이내여야 합니다.';
    if (!form.birthday) return '생년월일을 입력해 주세요.';
    if (!form.address) return '주소를 입력해 주세요.';
    return '';
  };

  const nextStep = () => {
    const msg = validateStep1();
    if (msg) return setError(msg);
    setStep(2);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const msg = validateStep2();
    if (msg) return setError(msg);

    setLoading(true);
    setError('');

    try {
      const { passwordConfirm, ...body } = form;

      const res = await fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.message || '회원가입에 실패했습니다.');
      }

      navigate('/login');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card auth-card--wide">
        <Link to="/" className="auth-logo">
          Proth<span>Sync</span>
        </Link>
        <h1 className="auth-title">회원가입</h1>
        <p className="auth-subtitle">ProthSync와 함께 시작하세요</p>

        {/* Step indicator */}
        <div className="auth-steps">
          <div className={`auth-step ${step >= 1 ? 'active' : ''}`}>
            <div className="step-num">1</div>
            <div className="step-label">기본 정보</div>
          </div>
          <div className="step-line" />
          <div className={`auth-step ${step >= 2 ? 'active' : ''}`}>
            <div className="step-num">2</div>
            <div className="step-label">상세 정보</div>
          </div>
        </div>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          {/* ── Step 1 ── */}
          {step === 1 && (
            <>
              <div className="form-group">
                <label>사용자 유형</label>
                <div className="type-grid">
                  {USER_TYPES.map((t) => (
                    <button
                      type="button"
                      key={t.value}
                      className={`type-btn${form.userType === t.value ? ' selected' : ''}`}
                      onClick={() => handleTypeSelect(t.value)}
                    >
                      {t.label}
                    </button>
                  ))}
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="userName">아이디</label>
                <input
                  id="userName"
                  name="userName"
                  type="text"
                  placeholder="5~20자"
                  value={form.userName}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label htmlFor="email">이메일</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="example@email.com"
                  value={form.email}
                  onChange={handleChange}
                />
              </div>

              <button type="button" className="auth-submit" onClick={nextStep}>
                다음 단계 →
              </button>
            </>
          )}

          {/* ── Step 2 ── */}
          {step === 2 && (
            <>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="password">비밀번호</label>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    placeholder="영문/숫자/특수문자 포함 8자 이상"
                    value={form.password}
                    onChange={handleChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="passwordConfirm">비밀번호 확인</label>
                  <input
                    id="passwordConfirm"
                    name="passwordConfirm"
                    type="password"
                    placeholder="비밀번호를 다시 입력하세요"
                    value={form.passwordConfirm}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="nickName">닉네임</label>
                  <input
                    id="nickName"
                    name="nickName"
                    type="text"
                    placeholder="최대 10자"
                    value={form.nickName}
                    onChange={handleChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="birthday">생년월일</label>
                  <input
                    id="birthday"
                    name="birthday"
                    type="date"
                    value={form.birthday}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="address">주소</label>
                <input
                  id="address"
                  name="address"
                  type="text"
                  placeholder="서울시 강남구 테헤란로 123"
                  value={form.address}
                  onChange={handleChange}
                />
              </div>

              <div className="auth-actions">
                <button type="button" className="auth-back" onClick={() => setStep(1)}>
                  ← 이전
                </button>
                <button type="submit" className="auth-submit" disabled={loading}>
                  {loading ? '가입 중...' : '가입하기'}
                </button>
              </div>
            </>
          )}
        </form>

        <p className="auth-switch">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}