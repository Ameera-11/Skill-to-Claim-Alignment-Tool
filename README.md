# 🔍 AI Resume Integrity Checker

A full-stack web application that uses AI to analyze resumes for credibility, honesty, and interview readiness.

**Live Demo:** [your-vercel-url.vercel.app](https://your-vercel-url.vercel.app)

---

## Features

- **AI-powered analysis** — every claim scored individually using Claude API
- **Integrity score (0–100)** — overall resume credibility rating
- **Claim-level feedback** — flags: Strong, Vague, Inflated, Unverifiable, Timeline Gap
- **Two user modes** — Job Seeker (constructive) and Recruiter (strict)
- **File upload support** — PDF and DOCX parsing via Apache POI + PDFBox
- **Paste or upload** — accepts plain text or file upload

---

## Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Backend     | Java 17, Spring Boot 3.2          |
| AI          | Anthropic Claude API              |
| File Parsing| Apache POI (DOCX), PDFBox (PDF)   |
| HTTP Client | OkHttp                            |
| Frontend    | React 18, Vite                    |
| Styling     | Tailwind CSS                      |
| API Calls   | Axios                             |
| Deployment  | Render (backend), Vercel (frontend)|

---

## Project Structure

```
resume-checker/
├── backend/                          # Spring Boot API
│   ├── src/main/java/com/resumechecker/
│   │   ├── ResumeCheckerApplication.java   # Entry point
│   │   ├── controller/
│   │   │   └── ResumeController.java       # REST endpoints
│   │   ├── service/
│   │   │   ├── ClaudeService.java          # Claude API integration
│   │   │   └── FileParserService.java      # PDF/DOCX parsing
│   │   ├── model/
│   │   │   └── ResumeAnalysis.java         # Data model
│   │   └── config/
│   │       └── CorsConfig.java             # CORS config
│   └── src/main/resources/
│       └── application.properties          # Config
│
└── frontend/                         # React app
    └── src/
        ├── App.jsx                    # Routing
        ├── pages/
        │   ├── LandingPage.jsx        # Hero + mode selection
        │   ├── AnalyzePage.jsx        # Input + file upload
        │   └── ResultsPage.jsx        # Score + flagged claims
        └── index.css                  # Global styles
```

---

## How to Run Locally

### Backend (Spring Boot)

```bash
cd backend

# Add your API key to application.properties:
# claude.api.key=YOUR_KEY_HERE

mvn spring-boot:run
# Server runs at http://localhost:8080
```

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
# App runs at http://localhost:5173
```

---

## API Endpoints

| Method | Endpoint             | Description                    |
|--------|----------------------|--------------------------------|
| POST   | /api/analyze/text    | Analyze pasted resume text     |
| POST   | /api/analyze/file    | Analyze uploaded PDF/DOCX file |
| GET    | /api/health          | Health check                   |

### Sample Request

```json
POST /api/analyze/text
{
  "resumeText": "John Smith, Senior Developer...",
  "mode": "jobseeker"
}
```

### Sample Response

```json
{
  "overallScore": 72,
  "scoreLabel": "Good",
  "summary": "Resume shows solid experience but contains some vague claims...",
  "mode": "jobseeker",
  "flags": [
    {
      "originalClaim": "Led a team to increase revenue by 300%",
      "flagType": "INFLATED",
      "explanation": "300% revenue increase is an extraordinary claim without supporting context",
      "suggestion": "Specify your exact role, the timeframe, and add context like starting baseline",
      "claimScore": 35
    }
  ]
}
```

---

## Deployment

**Backend → Render.com (free)**
1. Push backend to GitHub
2. Create Web Service on Render, connect repo
3. Set environment variable: `CLAUDE_API_KEY=your_key`
4. Build command: `mvn clean package`
5. Start command: `java -jar target/resume-checker-1.0.0.jar`

**Frontend → Vercel (free)**
1. Update `API_BASE` in `AnalyzePage.jsx` to your Render URL
2. Push frontend to GitHub
3. Import repo in Vercel — auto-deploys

---

## Skills Demonstrated

- RESTful API design with Spring Boot
- LLM API integration (Claude/Anthropic)
- File parsing (PDF, DOCX) in Java
- React component architecture with hooks
- Role-based UI (job seeker vs recruiter modes)
- Full-stack deployment (Render + Vercel)
- CORS configuration for cross-origin APIs

---

*Built as a portfolio project demonstrating full-stack Java + React development*
