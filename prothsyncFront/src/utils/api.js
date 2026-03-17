import useAuthStore from '../store/useAuthStore';

const BASE_URL = '/api';

// refresh 중복 호출 방지
let refreshPromise = null;

async function refreshAccessToken() {
  // 이미 refresh 진행 중이면 같은 Promise 공유
  if (refreshPromise) return refreshPromise;

  refreshPromise = (async () => {
    try {
      const res = await fetch(`${BASE_URL}/auth/refresh`, {
        method: 'POST',
        credentials: 'include',
      });

      if (!res.ok) return null;

      const data = await res.json();
      return data.accessToken;
    } catch {
      return null;
    } finally {
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

export async function apiFetch(path, options = {}) {
  const { accessToken } = useAuthStore.getState();

  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  // 401 → refresh 시도 후 원래 요청 재시도
  if (res.status === 401) {
    const newToken = await refreshAccessToken();

    if (newToken) {
      // refresh 성공 → store 업데이트 + 원래 요청 재시도
      useAuthStore.getState().setAuth(
        newToken,
        useAuthStore.getState().user
      );

      const retryRes = await fetch(`${BASE_URL}${path}`, {
        ...options,
        headers: {
          ...headers,
          'Authorization': `Bearer ${newToken}`,
        },
        credentials: 'include',
      });

      if (!retryRes.ok) {
        const data = await retryRes.json().catch(() => ({}));
        throw new Error(data.message || '요청에 실패했습니다.');
      }

      if (retryRes.status === 204) return null;
      return retryRes.json();
    }

    // refresh 실패 → 로그아웃 처리
    useAuthStore.getState().clearAuth();
    window.location.href = '/login';
    throw new Error('인증이 만료되었습니다.');
  }

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.message || '요청에 실패했습니다.');
  }

  // 204 No Content
  if (res.status === 204) return null;

  return res.json();
}