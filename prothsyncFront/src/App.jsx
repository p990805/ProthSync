import { useEffect } from 'react';
import { BrowserRouter } from 'react-router-dom';
import useAuthStore from './store/useAuthStore';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ScrollToTop from './components/ScrollToTop';
import AppRouter from './router/AppRouter';

export default function App() {
  const isReady = useAuthStore((state) => state.isReady);
  const initAuth = useAuthStore((state) => state.initAuth);

  useEffect(() => {
    initAuth();
  }, [initAuth]);

  if (!isReady) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        fontSize: '1rem',
        color: '#888',
      }}>
        로딩 중...
      </div>
    );
  }

  return (
    <BrowserRouter>
      <ScrollToTop />
      <Navbar />
      <main>
        <AppRouter />
      </main>
      <Footer />
    </BrowserRouter>
  );
}