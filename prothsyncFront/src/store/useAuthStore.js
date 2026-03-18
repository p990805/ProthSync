import { create } from 'zustand';

// JWT payload 디코딩 (서명 검증 없이 payload만 추출)
function parseJwtPayload(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

// JWT에서 user 정보 추출
function extractUserFromToken(token) {
  const payload = parseJwtPayload(token);
  if (!payload) return null;

  return {
    userId: Number(payload.sub),
    userName: payload.userName,
    nickName: payload.nickName,
    userType: payload.userType,
  };
}

const useAuthStore = create((set, get) => ({
  accessToken: null,
  user: null,       // { userId, userName, nickName?, userType? }
  isReady: false,   // 앱 초기화(refresh 시도) 완료 여부

  // 로그인 성공 시 — 응답 body의 전체 user 정보 저장
  setAuth: (accessToken, user) => set({ accessToken, user }),

  // 로그아웃 시
  clearAuth: () => set({ accessToken: null, user: null }),

  // 앱 초기화 완료
  setReady: () => set({ isReady: true }),

  // 앱 마운트 시 Cookie의 refreshToken으로 세션 복원
  initAuth: async () => {
    try {
      const res = await fetch('/api/auth/refresh', {
        method: 'POST',
        credentials: 'include',
      });

      if (!res.ok) {
        set({ accessToken: null, user: null, isReady: true });
        return;
      }

      const data = await res.json();
      const user = extractUserFromToken(data.accessToken);

      set({
        accessToken: data.accessToken,
        user,
        isReady: true,
      });
    } catch {
      set({ accessToken: null, user: null, isReady: true });
    }
  },
}));

export default useAuthStore;