package com.resumechecker.model;

import java.util.List;

/**
 * MODEL - ResumeAnalysis
 * Holds the full AI analysis result including optional GitHub cross-check.
 */
public class ResumeAnalysis {

    private int overallScore;
    private String scoreLabel;
    private String summary;
    private String mode;
    private String githubCrossCheck = ""; // AI summary of GitHub vs resume match
    private List<ClaimFlag> flags;

    public static class ClaimFlag {
        private String originalClaim;
        private String flagType;  // STRONG|VAGUE|INFLATED|UNVERIFIABLE|GAP|GITHUB_VERIFIED
        private String explanation;
        private String suggestion;
        private int claimScore;

        public ClaimFlag() {}

        public ClaimFlag(String originalClaim, String flagType,
                         String explanation, String suggestion, int claimScore) {
            this.originalClaim = originalClaim;
            this.flagType      = flagType;
            this.explanation   = explanation;
            this.suggestion    = suggestion;
            this.claimScore    = claimScore;
        }

        public String getCssClass() {
            return switch (flagType) {
                case "STRONG"          -> "flag-strong";
                case "VAGUE"           -> "flag-vague";
                case "INFLATED"        -> "flag-inflated";
                case "UNVERIFIABLE"    -> "flag-unverifiable";
                case "GAP"             -> "flag-gap";
                case "GITHUB_VERIFIED" -> "flag-verified";
                default                -> "flag-vague";
            };
        }

        public String getFlagLabel() {
            return switch (flagType) {
                case "STRONG"          -> "✅ Strong";
                case "VAGUE"           -> "⚠️ Too Vague";
                case "INFLATED"        -> "🔴 Possibly Inflated";
                case "UNVERIFIABLE"    -> "❓ Unverifiable";
                case "GAP"             -> "📅 Timeline Gap";
                case "GITHUB_VERIFIED" -> "🐙 GitHub Verified";
                default                -> "⚠️ Review Needed";
            };
        }

        public String getOriginalClaim()        { return originalClaim; }
        public void setOriginalClaim(String v)  { this.originalClaim = v; }
        public String getFlagType()             { return flagType; }
        public void setFlagType(String v)       { this.flagType = v; }
        public String getExplanation()          { return explanation; }
        public void setExplanation(String v)    { this.explanation = v; }
        public String getSuggestion()           { return suggestion; }
        public void setSuggestion(String v)     { this.suggestion = v; }
        public int getClaimScore()              { return claimScore; }
        public void setClaimScore(int v)        { this.claimScore = v; }
    }

    public long countByType(String type) {
        if (flags == null) return 0;
        return flags.stream().filter(f -> type.equals(f.getFlagType())).count();
    }

    public int getOverallScore()                { return overallScore; }
    public void setOverallScore(int v)          { this.overallScore = v; }
    public String getScoreLabel()               { return scoreLabel; }
    public void setScoreLabel(String v)         { this.scoreLabel = v; }
    public String getSummary()                  { return summary; }
    public void setSummary(String v)            { this.summary = v; }
    public String getMode()                     { return mode; }
    public void setMode(String v)               { this.mode = v; }
    public String getGithubCrossCheck()         { return githubCrossCheck; }
    public void setGithubCrossCheck(String v)   { this.githubCrossCheck = v; }
    public List<ClaimFlag> getFlags()           { return flags; }
    public void setFlags(List<ClaimFlag> v)     { this.flags = v; }
}