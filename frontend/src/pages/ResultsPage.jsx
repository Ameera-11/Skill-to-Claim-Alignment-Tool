import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'

const FLAG_CONFIG = {
  STRONG:          { color: 'border-green-400 bg-green-50',   badge: 'bg-green-100 text-green-800',   icon: '✅', label: 'Strong' },
  VAGUE:           { color: 'border-amber-400 bg-amber-50',   badge: 'bg-amber-100 text-amber-800',   icon: '⚠️', label: 'Too Vague' },
  INFLATED:        { color: 'border-red-400 bg-red-50',       badge: 'bg-red-100 text-red-800',       icon: '🔴', label: 'Possibly Inflated' },
  UNVERIFIABLE:    { color: 'border-purple-400 bg-purple-50', badge: 'bg-purple-100 text-purple-800', icon: '❓', label: 'Unverifiable' },
  GAP:             { color: 'border-gray-400 bg-gray-50',     badge: 'bg-gray-100 text-gray-700',     icon: '📅', label: 'Timeline Gap' },
  GITHUB_VERIFIED: { color: 'border-teal-400 bg-teal-50',    badge: 'bg-teal-100 text-teal-800',     icon: '🐙', label: 'GitHub Verified' },
}

export default function ResultsPage() {
  const navigate = useNavigate()
  const [data, setData]       = useState(null)
  const [filter, setFilter]   = useState('ALL')
  const [expanded, setExpanded] = useState({})

  useEffect(() => {
    const saved = sessionStorage.getItem('analysisResult')
    if (!saved) { navigate('/analyze'); return }
    setData(JSON.parse(saved))
  }, [navigate])

  if (!data) return null

  const result   = data.resumeAnalysis || data   // support both old and new format
  const github   = data.githubProfile
  const hasGH    = data.hasGitHub
  const ghCheck  = data.githubCrossCheckSummary || result.githubCrossCheck

  const score        = result.overallScore
  const isRecruiter  = result.mode === 'recruiter'
  const scoreColor   = score >= 80 ? 'text-green-600' : score >= 60 ? 'text-amber-500' : 'text-red-500'
  const scoreBarColor = score >= 80 ? 'bg-green-500' : score >= 60 ? 'bg-amber-400' : 'bg-red-500'

  const filteredFlags = filter === 'ALL' ? result.flags : result.flags.filter(f => f.flagType === filter)
  const counts = {}
  result.flags?.forEach(f => { counts[f.flagType] = (counts[f.flagType] || 0) + 1 })

  return (
    <div className="min-h-screen bg-gray-50">
      <div className={`py-8 text-white text-center ${isRecruiter ? 'bg-purple-700' : 'bg-blue-700'}`}>
        <h1 className="text-2xl font-bold">
          {isRecruiter ? '🏢 Recruiter Analysis Report' : '📊 Your Resume Integrity Report'}
        </h1>
        <p className="text-blue-100 text-sm mt-1">{result.flags?.length || 0} claims analyzed{hasGH ? ' · GitHub verified' : ''}</p>
      </div>

      <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">

        {/* SCORE CARD */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          <div className="flex flex-col md:flex-row items-center gap-8">
            <div className="flex-shrink-0 text-center">
              <div className={`text-7xl font-bold ${scoreColor}`}>{score}</div>
              <div className="text-gray-500 text-sm mt-1">out of 100</div>
              <div className={`mt-2 font-semibold text-lg ${scoreColor}`}>{result.scoreLabel}</div>
            </div>
            <div className="flex-1 w-full">
              <div className="flex justify-between text-xs text-gray-400 mb-1"><span>High Risk</span><span>Excellent</span></div>
              <div className="w-full bg-gray-200 rounded-full h-4 mb-4">
                <div className={`h-4 rounded-full score-fill ${scoreBarColor}`} style={{ width: `${score}%` }} />
              </div>
              <p className="text-gray-700 text-sm leading-relaxed">{result.summary}</p>
            </div>
          </div>

          {/* STAT PILLS */}
          <div className="grid grid-cols-3 sm:grid-cols-6 gap-3 mt-6">
            {Object.entries(FLAG_CONFIG).map(([type, cfg]) => (
              counts[type] ? (
                <div key={type} className="text-center bg-gray-50 rounded-xl p-3 cursor-pointer hover:bg-gray-100"
                  onClick={() => setFilter(type)}>
                  <div className="text-xl">{cfg.icon}</div>
                  <div className="text-2xl font-bold text-gray-800">{counts[type]}</div>
                  <div className="text-xs text-gray-500">{cfg.label}</div>
                </div>
              ) : null
            ))}
          </div>
        </div>

        {/* GITHUB PROFILE CARD */}
        {hasGH && github && (
          <div className="bg-white rounded-2xl shadow-sm border border-teal-200 p-6">
            <div className="flex items-center gap-2 mb-4">
              <span className="text-2xl">🐙</span>
              <h2 className="text-lg font-semibold text-gray-800">GitHub Verification Report</h2>
              <span className="ml-auto text-xs bg-teal-100 text-teal-700 px-3 py-1 rounded-full font-medium">Real Data</span>
            </div>

            {/* GitHub Stats */}
            <div className="flex items-center gap-4 mb-4">
              <img src={github.avatarUrl} alt="avatar" className="w-14 h-14 rounded-full border-2 border-teal-200" />
              <div>
                <div className="font-bold text-gray-800 text-lg">{github.name}</div>
                <a href={github.profileUrl} target="_blank" rel="noreferrer"
                  className="text-blue-600 text-sm hover:underline">@{github.username} ↗</a>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4 mb-4">
              {[
                { label: 'Public Repos', val: github.publicRepos },
                { label: 'Followers', val: github.followers },
                { label: 'Languages Used', val: github.topLanguages?.length || 0 }
              ].map(s => (
                <div key={s.label} className="bg-teal-50 rounded-xl p-3 text-center">
                  <div className="text-2xl font-bold text-teal-700">{s.val}</div>
                  <div className="text-xs text-gray-500">{s.label}</div>
                </div>
              ))}
            </div>

            {/* Languages */}
            {github.topLanguages?.length > 0 && (
              <div className="mb-4">
                <div className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">Verified Languages from GitHub</div>
                <div className="flex flex-wrap gap-2">
                  {github.topLanguages.slice(0, 10).map(lang => (
                    <span key={lang} className="bg-teal-100 text-teal-800 text-sm px-3 py-1 rounded-full font-medium">
                      {lang} ×{github.languageStats?.[lang]}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {/* Cross-check AI summary */}
            {ghCheck && (
              <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
                <div className="text-xs font-semibold text-amber-700 uppercase tracking-wide mb-1">🤖 AI Cross-Check Finding</div>
                <p className="text-sm text-amber-900">{ghCheck}</p>
              </div>
            )}

            {/* Top Repos */}
            {github.repositories?.length > 0 && (
              <div className="mt-4">
                <div className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">Recent Original Repositories</div>
                <div className="space-y-2 max-h-48 overflow-y-auto">
                  {github.repositories.slice(0, 8).map(repo => (
                    <a key={repo.name} href={repo.url} target="_blank" rel="noreferrer"
                      className="flex items-center justify-between bg-gray-50 hover:bg-gray-100 rounded-lg px-3 py-2 transition">
                      <div>
                        <span className="text-sm font-medium text-blue-600">{repo.name}</span>
                        {repo.language && (
                          <span className="ml-2 text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded-full">{repo.language}</span>
                        )}
                        {repo.description !== 'No description' && (
                          <div className="text-xs text-gray-500 mt-0.5 truncate max-w-xs">{repo.description}</div>
                        )}
                      </div>
                      {repo.stars > 0 && <span className="text-xs text-yellow-600">★ {repo.stars}</span>}
                    </a>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* FILTER BUTTONS */}
        <div className="flex flex-wrap gap-2">
          <button onClick={() => setFilter('ALL')}
            className={`px-4 py-2 rounded-full text-sm font-medium border transition ${
              filter === 'ALL' ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-600 border-gray-200'
            }`}>
            All ({result.flags?.length || 0})
          </button>
          {Object.entries(FLAG_CONFIG).map(([type, cfg]) => (
            counts[type] ? (
              <button key={type} onClick={() => setFilter(type)}
                className={`px-4 py-2 rounded-full text-sm font-medium border transition ${
                  filter === type ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-600 border-gray-200'
                }`}>
                {cfg.icon} {cfg.label} ({counts[type]})
              </button>
            ) : null
          ))}
        </div>

        {/* CLAIM CARDS */}
        <div className="space-y-4">
          {filteredFlags?.map((flag, i) => {
            const cfg = FLAG_CONFIG[flag.flagType] || FLAG_CONFIG.VAGUE
            const isOpen = expanded[i]
            return (
              <div key={i} onClick={() => setExpanded(p => ({ ...p, [i]: !p[i] }))}
                className={`bg-white rounded-xl border-l-4 border border-gray-100 p-5 cursor-pointer hover:shadow-sm transition ${cfg.color}`}>
                <div className="flex items-start justify-between gap-3">
                  <div className="flex-1">
                    <span className={`inline-block text-xs font-semibold px-2 py-1 rounded-full mb-2 ${cfg.badge}`}>
                      {cfg.icon} {cfg.label}
                    </span>
                    <p className="text-gray-800 font-medium text-sm leading-relaxed">"{flag.originalClaim}"</p>
                  </div>
                  <div className="flex-shrink-0 text-right">
                    <div className="text-lg font-bold text-gray-700">{flag.claimScore}</div>
                    <div className="text-xs text-gray-400">score</div>
                    <div className="text-gray-400 text-xs mt-1">{isOpen ? '▲' : '▼'}</div>
                  </div>
                </div>
                {isOpen && (
                  <div className="mt-4 pt-4 border-t border-gray-200 space-y-3">
                    <div>
                      <div className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-1">Why flagged</div>
                      <p className="text-sm text-gray-700">{flag.explanation}</p>
                    </div>
                    {flag.suggestion && flag.suggestion !== 'No changes needed' && (
                      <div>
                        <div className="text-xs font-semibold text-blue-600 uppercase tracking-wide mb-1">💡 Suggestion</div>
                        <p className="text-sm text-blue-800 bg-blue-50 rounded-lg px-3 py-2">{flag.suggestion}</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )
          })}
        </div>

        {/* ACTION BUTTONS */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button onClick={() => navigate('/analyze')}
            className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-xl font-semibold transition">
            🔄 Analyze Another Resume
          </button>
          <button onClick={() => window.print()}
            className="flex-1 bg-white border border-gray-300 hover:bg-gray-50 text-gray-700 py-3 rounded-xl font-semibold transition">
            🖨️ Print / Save as PDF
          </button>
          <button onClick={() => navigate('/')}
            className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-600 py-3 rounded-xl font-semibold transition">
            ← Home
          </button>
        </div>
      </div>
    </div>
  )
}