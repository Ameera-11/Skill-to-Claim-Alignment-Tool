import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'

const API_BASE = 'https://skill-to-claim-alignment-tool.onrender.com/api'

export default function AnalyzePage() {
  const navigate    = useNavigate()
  const mode        = sessionStorage.getItem('mode') || 'jobseeker'
  const isRecruiter = mode === 'recruiter'

  const [tab, setTab]             = useState('text')
  const [resumeText, setText]     = useState('')
  const [file, setFile]           = useState(null)
  const [githubUsername, setGithub] = useState('')
  const [githubPreview, setPreview] = useState(null)
  const [githubLoading, setGhLoad]  = useState(false)
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState('')

  // Preview GitHub profile before full analysis
  const previewGitHub = async () => {
    if (!githubUsername.trim()) return
    setGhLoad(true)
    setPreview(null)
    try {
      const res = await axios.get(`${API_BASE}/github/${githubUsername.trim()}`)
      setPreview(res.data)
    } catch (err) {
      setError(err.response?.data?.error || 'GitHub user not found.')
    } finally {
      setGhLoad(false)
    }
  }

  const handleAnalyze = async () => {
    setError('')
    setLoading(true)
    try {
      let response
      if (tab === 'text') {
        response = await axios.post(`${API_BASE}/analyze/text`, {
          resumeText,
          mode,
          githubUsername: githubUsername.trim()
        })
      } else {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('mode', mode)
        formData.append('githubUsername', githubUsername.trim())
        response = await axios.post(`${API_BASE}/analyze/file`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
      }
      sessionStorage.setItem('analysisResult', JSON.stringify(response.data))
      navigate('/results')
    } catch (err) {
      setError(err.response?.data?.error || 'Something went wrong. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  const canSubmit = tab === 'text' ? resumeText.length > 50 : file !== null

  return (
    <div className="min-h-screen bg-gray-50">
      {/* HEADER */}
      <div className={`py-8 text-white text-center ${isRecruiter ? 'bg-purple-700' : 'bg-blue-700'}`}>
        <div className="text-3xl font-bold mb-1">
          {isRecruiter ? '🏢 Recruiter Mode' : '👤 Job Seeker Mode'}
        </div>
        <p className="text-blue-100 text-sm">
          {isRecruiter ? 'Scanning candidate resume for red flags' : 'Checking your resume integrity'}
        </p>
        <button onClick={() => navigate('/')} className="mt-3 text-blue-200 text-sm underline">
          ← Switch mode
        </button>
      </div>

      <div className="max-w-3xl mx-auto px-4 py-10 space-y-6">

        {/* RESUME INPUT CARD */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8">
          {/* TABS */}
          <div className="flex gap-2 mb-6 border-b border-gray-200">
            {['text', 'file'].map(t => (
              <button key={t} onClick={() => setTab(t)}
                className={`px-5 py-3 text-sm font-medium border-b-2 transition ${
                  tab === t ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}>
                {t === 'text' ? '📝 Paste Text' : '📂 Upload File'}
              </button>
            ))}
          </div>

          {/* TEXT TAB */}
          {tab === 'text' && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Paste resume content here</label>
              <textarea
                value={resumeText}
                onChange={(e) => setText(e.target.value)}
                rows={14}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none font-mono"
                placeholder="Paste your full resume here..."
              />
              <div className="mt-1 text-right text-xs text-gray-400">{resumeText.length} characters</div>
            </div>
          )}

          {/* FILE TAB */}
          {tab === 'file' && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">Upload resume file</label>
              <div
                className="border-2 border-dashed border-gray-300 rounded-xl p-12 text-center hover:border-blue-400 transition cursor-pointer"
                onClick={() => document.getElementById('fileInput').click()}
                onDragOver={(e) => e.preventDefault()}
                onDrop={(e) => { e.preventDefault(); setFile(e.dataTransfer.files[0]) }}
              >
                <div className="text-4xl mb-3">📄</div>
                {file ? (
                  <div>
                    <p className="font-medium text-gray-800">{file.name}</p>
                    <p className="text-sm text-gray-500 mt-1">{(file.size / 1024).toFixed(1)} KB</p>
                    <button onClick={(e) => { e.stopPropagation(); setFile(null) }}
                      className="mt-2 text-red-500 text-sm underline">Remove</button>
                  </div>
                ) : (
                  <div>
                    <p className="text-gray-600 font-medium">Click to upload or drag & drop</p>
                    <p className="text-gray-400 text-sm mt-1">PDF, DOCX, or TXT — supports LinkedIn PDF export too!</p>
                  </div>
                )}
              </div>
              <input id="fileInput" type="file" accept=".pdf,.docx,.txt" className="hidden"
                onChange={(e) => setFile(e.target.files[0])} />
            </div>
          )}
        </div>

        {/* GITHUB CARD */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xl">🐙</span>
            <h3 className="font-semibold text-gray-800">GitHub Verification <span className="text-xs font-normal text-green-600 bg-green-50 px-2 py-0.5 rounded-full ml-1">Optional · Free</span></h3>
          </div>
          <p className="text-sm text-gray-500 mb-4">
            Enter a GitHub username to cross-check resume skills against real code activity.
            AI will flag mismatches between claimed skills and actual GitHub usage.
          </p>

          <div className="flex gap-3">
            <div className="flex-1 relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">github.com/</span>
              <input
                type="text"
                value={githubUsername}
                onChange={(e) => { setGithub(e.target.value); setPreview(null) }}
                onKeyDown={(e) => e.key === 'Enter' && previewGitHub()}
                placeholder="username"
                className="w-full border border-gray-300 rounded-xl pl-24 pr-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-gray-400"
              />
            </div>
            <button
              onClick={previewGitHub}
              disabled={!githubUsername.trim() || githubLoading}
              className="px-5 py-3 bg-gray-800 text-white rounded-xl text-sm font-medium hover:bg-gray-700 disabled:bg-gray-300 transition"
            >
              {githubLoading ? '...' : 'Preview'}
            </button>
          </div>

          {/* GitHub Preview Card */}
          {githubPreview && (
            <div className="mt-4 bg-gray-50 rounded-xl p-4 border border-gray-200">
              <div className="flex items-center gap-3 mb-3">
                <img src={githubPreview.avatarUrl} alt="avatar"
                  className="w-12 h-12 rounded-full border border-gray-200" />
                <div>
                  <div className="font-semibold text-gray-800">{githubPreview.name}</div>
                  <div className="text-sm text-gray-500">@{githubPreview.username}</div>
                </div>
                <a href={githubPreview.profileUrl} target="_blank" rel="noreferrer"
                  className="ml-auto text-blue-600 text-xs underline">View Profile ↗</a>
              </div>
              <div className="grid grid-cols-3 gap-3 mb-3">
                {[
                  { label: 'Repos', val: githubPreview.publicRepos },
                  { label: 'Followers', val: githubPreview.followers },
                  { label: 'Languages', val: githubPreview.topLanguages?.length || 0 }
                ].map(s => (
                  <div key={s.label} className="text-center bg-white rounded-lg p-2 border border-gray-100">
                    <div className="font-bold text-gray-800">{s.val}</div>
                    <div className="text-xs text-gray-500">{s.label}</div>
                  </div>
                ))}
              </div>
              {githubPreview.topLanguages?.length > 0 && (
                <div className="flex flex-wrap gap-2">
                  {githubPreview.topLanguages.slice(0, 8).map(lang => (
                    <span key={lang} className="bg-blue-100 text-blue-700 text-xs px-2 py-1 rounded-full font-medium">
                      {lang}
                    </span>
                  ))}
                </div>
              )}
              <p className="text-xs text-green-600 mt-3 font-medium">
                ✅ GitHub data will be used to verify your resume claims
              </p>
            </div>
          )}
        </div>

        {/* ERROR */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl px-4 py-3 text-sm">
            ❌ {error}
          </div>
        )}

        {/* SUBMIT */}
        <button
          onClick={handleAnalyze}
          disabled={!canSubmit || loading}
          className={`w-full py-4 rounded-xl font-semibold text-white transition text-lg ${
            canSubmit && !loading
              ? isRecruiter ? 'bg-purple-600 hover:bg-purple-700' : 'bg-blue-600 hover:bg-blue-700'
              : 'bg-gray-300 cursor-not-allowed'
          }`}
        >
          {loading ? (
            <span className="flex items-center justify-center gap-3">
              <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24" fill="none">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              {githubUsername ? 'Fetching GitHub + Analyzing...' : 'Analyzing with AI...'}
            </span>
          ) : (
            `🔍 Analyze Resume${githubUsername ? ' + Verify GitHub' : ''}`
          )}
        </button>
      </div>
    </div>
  )
}