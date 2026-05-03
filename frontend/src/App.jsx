import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LandingPage   from './pages/LandingPage.jsx'
import AnalyzePage   from './pages/AnalyzePage.jsx'
import ResultsPage   from './pages/ResultsPage.jsx'

/**
 * APP - Main routing component
 * 3 pages:
 *   /          → LandingPage  (hero + how it works)
 *   /analyze   → AnalyzePage  (paste or upload resume)
 *   /results   → ResultsPage  (score + flagged claims)
 */
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"        element={<LandingPage />} />
        <Route path="/analyze" element={<AnalyzePage />} />
        <Route path="/results" element={<ResultsPage />} />
      </Routes>
    </BrowserRouter>
  )
}
