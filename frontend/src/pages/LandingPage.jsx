import { useNavigate } from 'react-router-dom'

export default function LandingPage() {
  const navigate = useNavigate()

  const startAsJobSeeker = () => {
    sessionStorage.setItem('mode', 'jobseeker')
    navigate('/analyze')
  }

  const startAsRecruiter = () => {
    sessionStorage.setItem('mode', 'recruiter')
    navigate('/analyze')
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800 text-white">

      {/* NAVBAR */}
      <nav className="flex justify-between items-center px-8 py-5 border-b border-slate-700">
        <div className="text-xl font-bold text-blue-400">
          🔍 ResumeCheck AI
        </div>
        <button
          onClick={startAsJobSeeker}
          className="bg-blue-600 hover:bg-blue-700 px-5 py-2 rounded-lg text-sm font-medium transition"
        >
          Check My Resume
        </button>
      </nav>

      {/* HERO */}
      <div className="max-w-4xl mx-auto px-6 py-24 text-center">
        <div className="inline-block bg-blue-600/20 text-blue-400 text-sm px-4 py-2 rounded-full mb-6 border border-blue-500/30">
          AI-Powered Resume Analysis
        </div>

        <h1 className="text-5xl font-bold mb-6 leading-tight">
          Is Your Resume{' '}
          <span className="text-blue-400">Honest Enough</span>{' '}
          to Get Hired?
        </h1>

        <p className="text-xl text-slate-300 mb-12 max-w-2xl mx-auto leading-relaxed">
          Our AI scans every claim in your resume — skills, achievements, dates —
          and gives you an integrity score with specific feedback before the recruiter does.
        </p>

        {/* TWO MODE BUTTONS */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center mb-16">
          <button
            onClick={startAsJobSeeker}
            className="bg-blue-600 hover:bg-blue-700 px-8 py-4 rounded-xl text-lg font-semibold transition flex items-center justify-center gap-2"
          >
            👤 I'm a Job Seeker
            <span className="text-sm font-normal text-blue-200">Check & improve my resume</span>
          </button>
          <button
            onClick={startAsRecruiter}
            className="bg-slate-700 hover:bg-slate-600 border border-slate-500 px-8 py-4 rounded-xl text-lg font-semibold transition flex items-center justify-center gap-2"
          >
            🏢 I'm a Recruiter
            <span className="text-sm font-normal text-slate-300">Scan a candidate's resume</span>
          </button>
        </div>

        {/* HOW IT WORKS */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-left">
          {[
            {
              icon: '📋',
              step: '1',
              title: 'Paste or Upload',
              desc: 'Paste your resume text or upload a PDF / DOCX file'
            },
            {
              icon: '🤖',
              step: '2',
              title: 'AI Analyzes',
              desc: 'Claude AI scans every claim for credibility, specificity and consistency'
            },
            {
              icon: '📊',
              step: '3',
              title: 'Get Your Score',
              desc: 'See your integrity score and exactly which claims need improvement'
            }
          ].map((item) => (
            <div key={item.step} className="bg-slate-800/60 border border-slate-700 rounded-xl p-6">
              <div className="text-3xl mb-3">{item.icon}</div>
              <div className="text-blue-400 text-sm font-medium mb-1">Step {item.step}</div>
              <h3 className="text-lg font-semibold mb-2">{item.title}</h3>
              <p className="text-slate-400 text-sm leading-relaxed">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>

      {/* FOOTER */}
      <div className="text-center py-8 text-slate-500 text-sm border-t border-slate-700">
        Built with Spring Boot + React + Claude AI
      </div>
    </div>
  )
}
