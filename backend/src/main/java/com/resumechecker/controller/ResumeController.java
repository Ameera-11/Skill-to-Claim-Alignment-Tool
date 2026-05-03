package com.resumechecker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumechecker.model.FullAnalysisResult;
import com.resumechecker.model.GitHubProfile;
import com.resumechecker.model.ResumeAnalysis;
import com.resumechecker.service.ClaudeService;
import com.resumechecker.service.FileParserService;
import com.resumechecker.service.GitHubService;

/**
 * RESUME CONTROLLER
 * API endpoints - now supports optional GitHub username for cross-checking.
 *
 * POST /api/analyze/text  - analyze pasted text (+ optional GitHub username)
 * POST /api/analyze/file  - analyze uploaded PDF/DOCX (+ optional GitHub username)
 * GET  /api/github/{username} - fetch GitHub profile only
 * GET  /api/health         - health check
 */
@RestController
@RequestMapping("/api")
public class ResumeController {

    @Autowired private ClaudeService    claudeService;
    @Autowired private FileParserService fileParserService;
    @Autowired private GitHubService    gitHubService;

    // --------------------------------------------------------
    // ENDPOINT 1: Analyze pasted text + optional GitHub
    // --------------------------------------------------------
    @PostMapping("/analyze/text")
    public ResponseEntity<?> analyzeText(@RequestBody Map<String, String> body) {

        String resumeText      = body.get("resumeText");
        String mode            = body.getOrDefault("mode", "jobseeker");
        String githubUsername  = body.getOrDefault("githubUsername", "").trim();

        if (resumeText == null || resumeText.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Resume text cannot be empty."));
        }
        if (resumeText.length() < 50) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Resume text is too short. Please paste your full resume."));
        }

        try {
            return ResponseEntity.ok(performFullAnalysis(resumeText, mode, githubUsername));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Analysis failed: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------
    // ENDPOINT 2: Analyze uploaded file + optional GitHub
    // --------------------------------------------------------
    @PostMapping("/analyze/file")
    public ResponseEntity<?> analyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "jobseeker") String mode,
            @RequestParam(value = "githubUsername", defaultValue = "") String githubUsername) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file to upload."));
        }

        try {
            System.out.println("📂 Parsing file: " + file.getOriginalFilename());
            String resumeText = fileParserService.extractText(file);
            return ResponseEntity.ok(performFullAnalysis(resumeText, mode, githubUsername.trim()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "File processing failed: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------
    // ENDPOINT 3: Fetch GitHub profile only (for preview)
    // --------------------------------------------------------
    @GetMapping("/github/{username}")
    public ResponseEntity<?> getGitHubProfile(@PathVariable String username) {
        try {
            GitHubProfile profile = gitHubService.fetchProfile(username);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "GitHub fetch failed: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------
    // ENDPOINT 4: Health check
    // --------------------------------------------------------
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "Resume Checker is running!"));
    }

    // --------------------------------------------------------
    // PRIVATE: Core analysis logic used by both endpoints
    // --------------------------------------------------------
    private FullAnalysisResult performFullAnalysis(String resumeText, String mode,
                                                    String githubUsername) throws Exception {
        GitHubProfile githubProfile     = null;
        String        githubSummary     = null;
        boolean       hasGitHub         = false;

        // If GitHub username provided, fetch real data
        if (githubUsername != null && !githubUsername.isBlank()) {
            System.out.println("🐙 Fetching GitHub profile for: " + githubUsername);
            try {
                githubProfile = gitHubService.fetchProfile(githubUsername);
                githubSummary = gitHubService.buildGitHubSummary(githubProfile);
                hasGitHub     = true;
                System.out.println("✅ GitHub profile fetched: "
                        + githubProfile.getPublicRepos() + " repos, "
                        + githubProfile.getTopLanguages());
            } catch (Exception e) {
                // GitHub fetch failed - continue without it
                System.err.println("⚠️ GitHub fetch failed: " + e.getMessage());
            }
        }

        // Run AI analysis (with or without GitHub data)
        System.out.println("🤖 Analyzing resume in " + mode + " mode...");
        ResumeAnalysis analysis = claudeService.analyzeResumeWithGitHub(
                resumeText, mode, githubSummary);
        System.out.println("✅ Analysis done. Score: " + analysis.getOverallScore());

        return new FullAnalysisResult(
                analysis,
                githubProfile,
                hasGitHub,
                analysis.getGithubCrossCheck()
        );
    }
}