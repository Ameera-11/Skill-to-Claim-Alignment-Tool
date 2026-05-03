package com.resumechecker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumechecker.model.ResumeAnalysis;
import com.resumechecker.model.ResumeAnalysis.ClaimFlag;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * GROQ AI SERVICE
 * Analyzes resume text — optionally cross-checked against real GitHub data.
 */
@Service
public class ClaudeService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    /**
     * Analyze resume text only (no GitHub)
     */
    public ResumeAnalysis analyzeResume(String resumeText, String mode) throws Exception {
        return analyzeResumeWithGitHub(resumeText, mode, null);
    }

    /**
     * Analyze resume text AND cross-check with GitHub data
     *
     * @param resumeText    - resume content
     * @param mode          - "jobseeker" or "recruiter"
     * @param githubSummary - GitHub profile summary (null if no GitHub provided)
     */
    public ResumeAnalysis analyzeResumeWithGitHub(String resumeText, String mode,
                                                   String githubSummary) throws Exception {
        String prompt = buildPrompt(resumeText, mode, githubSummary);

        String requestBody = "{"
            + "\"model\": \"llama-3.1-8b-instant\","
            + "\"messages\": [{\"role\": \"user\", \"content\": "
            + objectMapper.writeValueAsString(prompt) + "}],"
            + "\"temperature\": 0.3,"
            + "\"max_tokens\": 2000"
            + "}";

        Request request = new Request.Builder()
                .url(GROQ_URL)
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Groq API error: " + response.code() + " - " + responseBody);
            }

            JsonNode root = objectMapper.readTree(responseBody);
            String aiText = root.path("choices").get(0)
                    .path("message").path("content").asText();

            int jsonStart = aiText.indexOf("{");
            int jsonEnd   = aiText.lastIndexOf("}") + 1;
            if (jsonStart == -1 || jsonEnd == 0) {
                throw new RuntimeException("No JSON in AI response: " + aiText);
            }

            JsonNode analysisJson = objectMapper.readTree(aiText.substring(jsonStart, jsonEnd));
            return parseAnalysis(analysisJson, mode);
        }
    }

    private String buildPrompt(String resumeText, String mode, String githubSummary) {
        String modeInstruction = mode.equals("recruiter")
                ? "You are a strict RECRUITER reviewing a candidate resume. Be critical and flag anything suspicious."
                : "You are helping a JOB SEEKER improve their resume. Be constructive and specific.";

        String githubSection = "";
        if (githubSummary != null && !githubSummary.isBlank()) {
            githubSection = "\n\nIMPORTANT - You also have access to the candidate's REAL GitHub profile data below. "
                + "Use this to cross-check the skills and technologies claimed in the resume. "
                + "If the resume claims expertise in a language but GitHub shows little/no usage, flag it as INFLATED. "
                + "If GitHub confirms the skills, mark those claims as STRONG.\n\n"
                + githubSummary;
        }

        return modeInstruction + githubSection + "\n\n"
            + "Analyze this resume for integrity and credibility.\n"
            + "For each key claim use flag types: STRONG, VAGUE, INFLATED, UNVERIFIABLE, GAP\n"
            + (githubSummary != null ? "Also add GITHUB_VERIFIED for claims confirmed by GitHub data.\n" : "")
            + "\nRESPOND ONLY WITH VALID JSON, NO BACKTICKS, NO OTHER TEXT:\n"
            + "{\n"
            + "  \"overallScore\": 75,\n"
            + "  \"scoreLabel\": \"Good\",\n"
            + "  \"summary\": \"2-3 sentence summary of resume integrity\",\n"
            + "  \"githubCrossCheck\": \"1-2 sentences about how GitHub data matches or contradicts the resume (only if GitHub data provided, else empty string)\",\n"
            + "  \"flags\": [\n"
            + "    {\n"
            + "      \"originalClaim\": \"exact text from resume\",\n"
            + "      \"flagType\": \"STRONG\",\n"
            + "      \"explanation\": \"reason for this rating\",\n"
            + "      \"suggestion\": \"improvement or No changes needed\",\n"
            + "      \"claimScore\": 90\n"
            + "    }\n"
            + "  ]\n"
            + "}\n\n"
            + "Score labels: 80-100=Excellent, 60-79=Good, 40-59=Needs Work, 0-39=High Risk\n\n"
            + "RESUME:\n---\n" + resumeText + "\n---";
    }

    private ResumeAnalysis parseAnalysis(JsonNode json, String mode) {
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setOverallScore(json.path("overallScore").asInt(50));
        analysis.setScoreLabel(json.path("scoreLabel").asText("Unknown"));
        analysis.setSummary(json.path("summary").asText("Analysis complete."));
        analysis.setMode(mode);
        analysis.setGithubCrossCheck(json.path("githubCrossCheck").asText(""));

        List<ClaimFlag> flags = new ArrayList<>();
        JsonNode flagsNode = json.path("flags");
        if (flagsNode.isArray()) {
            for (JsonNode f : flagsNode) {
                flags.add(new ClaimFlag(
                    f.path("originalClaim").asText(),
                    f.path("flagType").asText("VAGUE"),
                    f.path("explanation").asText(),
                    f.path("suggestion").asText(),
                    f.path("claimScore").asInt(50)
                ));
            }
        }
        analysis.setFlags(flags);
        return analysis;
    }
}