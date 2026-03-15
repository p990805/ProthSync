import Hero from '../components/Hero';
import StatsBar from '../components/StatsBar';
import Features from '../components/Features';
import UserTypes from '../components/UserTypes';
import CasesShowcase from '../components/CasesShowcase';
import CTA from '../components/CTA';

export default function LandingPage() {
  return (
    <>
      <Hero />
      <StatsBar />
      <Features />
      <UserTypes />
      <CasesShowcase />
      <CTA />
    </>
  );
}