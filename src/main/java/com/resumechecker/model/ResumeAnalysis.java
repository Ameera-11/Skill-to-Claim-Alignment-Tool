package com.resumechecker.model;

import java.util.List;

/**
 * Holds the full analysis result for a resume.
 * This is what gets sent to the HTML template to display results.
 */
public class ResumeAnalysis {

    // Overall integrity score (0 to 100)
    private int overallScore;

    // Label like "Excellent", "Good", "Needs Work", "High Risk"
    private String scoreLabel;

    // Summary paragraph from AI
    private String summary;

    // List of individual claim findings
    private List<ClaimFlag> flags;

    // Mode: "jobseeker" or "recruiter"
    private String mode;

    // Original resume text (for display)
    private String resumeText;

    // --- Inner class for each flagged claim ---
    public static class ClaimFlag {
        // The original text extracted from the resume
        private String originalClaim;

        // "STRONG", "VAGUE", "INFLATED", "UNVERIFIABLE", "GAP"
        private String flagType;

        // Explanation of why it's flagged
        private String explanation;

        // AI suggestion for improvement
        private String suggestion;

        // Score for this specific claim (0-100)
        private int claimScore;

        public ClaimFlag() {}

        public ClaimFlag(String originalClaim, String flagType, 
                         String explanation, String suggestion, int claimScore) {
            this.originalClaim = originalClaim;
            this.flagType = flagType;
            this.explanation = explanation;
            this.suggestion = suggestion;
            this.claimScore = claimScore;
        }

        // CSS class based on flag type (used in HTML template)
        public String getCssClass() {
            return switch (flagType) {
                case "STRONG"       -> "flag-strong";
                case "VAGUE"        -> "flag-vague";
                case "INFLATED"     -> "flag-inflated";
                case "UNVERIFIABLE" -> "flag-unverifiable";
                case "GAP"          -> "flag-gap";
                default             -> "flag-vague";
            };
        }

        // Human-readable label for the badge
        public String getFlagLabel() {
            return switch (flagType) {
                case "STRONG"       -> "Strong";
                case "VAGUE"        -> "Too Vague";
                case "INFLATED"     -> "Possibly Inflated";
                case "UNVERIFIABLE" -> "Unverifiable";
                case "GAP"          -> "Timeline Gap";
                default             -> "Review Needed";
            };
        }

        // Getters and Setters
        public String getOriginalClaim()  { return originalClaim; }
        public void setOriginalClaim(String v) { this.originalClaim = v; }

        public String getFlagType()       { return flagType; }
        public void setFlagType(String v) { this.flagType = v; }

        public String getExplanation()    { return explanation; }
        public void setExplanation(String v) { this.explanation = v; }

        public String getSuggestion()     { return suggestion; }
        public void setSuggestion(String v) { this.suggestion = v; }

        public int getClaimScore()        { return claimScore; }
        public void setClaimScore(int v)  { this.claimScore = v; }
    }

    // Getters and Setters for ResumeAnalysis
    public int getOverallScore()          { return overallScore; }
    public void setOverallScore(int v)    { this.overallScore = v; }

    public String getScoreLabel()         { return scoreLabel; }
    public void setScoreLabel(String v)   { this.scoreLabel = v; }

    public String getSummary()            { return summary; }
    public void setSummary(String v)      { this.summary = v; }

    public List<ClaimFlag> getFlags()     { return flags; }
    public void setFlags(List<ClaimFlag> v) { this.flags = v; }

    public String getMode()               { return mode; }
    public void setMode(String v)         { this.mode = v; }

    public String getResumeText()         { return resumeText; }
    public void setResumeText(String v)   { this.resumeText = v; }

    // Helper: count flags by type
    public long countByType(String type) {
        if (flags == null) return 0;
        return flags.stream().filter(f -> f.getFlagType().equals(type)).count();
    }

    // Helper: score color for display
    public String getScoreColor() {
        if (overallScore >= 80) return "#22c55e";  // green
        if (overallScore >= 60) return "#f59e0b";  // amber
        return "#ef4444";                            // red
    }
}
