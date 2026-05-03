package com.resumechecker.model;

/**
 * FULL ANALYSIS RESULT
 * Combines resume analysis + GitHub profile into one response.
 * This is what gets sent to the React frontend.
 */
public class FullAnalysisResult {

    private ResumeAnalysis resumeAnalysis;
    private GitHubProfile  githubProfile;
    private boolean        hasGitHub;

    // GitHub cross-check findings (AI-generated)
    private String githubCrossCheckSummary;

    public FullAnalysisResult() {}

    public FullAnalysisResult(ResumeAnalysis resumeAnalysis,
                               GitHubProfile githubProfile,
                               boolean hasGitHub,
                               String githubCrossCheckSummary) {
        this.resumeAnalysis          = resumeAnalysis;
        this.githubProfile           = githubProfile;
        this.hasGitHub               = hasGitHub;
        this.githubCrossCheckSummary = githubCrossCheckSummary;
    }

    // Getters and Setters
    public ResumeAnalysis getResumeAnalysis()               { return resumeAnalysis; }
    public void setResumeAnalysis(ResumeAnalysis v)         { this.resumeAnalysis = v; }
    public GitHubProfile getGithubProfile()                 { return githubProfile; }
    public void setGithubProfile(GitHubProfile v)           { this.githubProfile = v; }
    public boolean isHasGitHub()                            { return hasGitHub; }
    public void setHasGitHub(boolean v)                     { this.hasGitHub = v; }
    public String getGithubCrossCheckSummary()              { return githubCrossCheckSummary; }
    public void setGithubCrossCheckSummary(String v)        { this.githubCrossCheckSummary = v; }
}