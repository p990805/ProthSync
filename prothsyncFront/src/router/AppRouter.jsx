import { Routes, Route } from 'react-router-dom';
import LandingPage from '../pages/LandingPage';
import FeaturesPage from '../pages/FeaturesPage';
import UsersPage from '../pages/UsersPage';
import CasesPage from '../pages/CasesPage';
import LoginPage from '../pages/LoginPage';
import SignupPage from '../pages/SignupPage';
import FeedPage from '../pages/FeedPage';

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/features" element={<FeaturesPage />} />
      <Route path="/users" element={<UsersPage />} />
      <Route path="/cases" element={<CasesPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/feed" element={<FeedPage />} />
    </Routes>
  );
}