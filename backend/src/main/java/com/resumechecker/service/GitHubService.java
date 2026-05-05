package com.resumechecker.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumechecker.model.GitHubProfile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * GITHUB SERVICE
 * Fetches real GitHub data using the FREE public GitHub API.
 * No API key needed for public profiles!
 *
 * What we fetch:
 * - User profile (name, bio, followers, public repos)
 * - All public repositories (name, language, stars, description)
 * - Top programming languages used
 * - Recent activity summary
 */


@Service
public class GitHubService {

    @jakarta.annotation.PostConstruct
    public void init() {
        this.githubToken = System.getenv("GITHUB_TOKEN");
        if (this.githubToken == null) this.githubToken = "";
}

    private String githubToken = "";

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                // GitHub API requires a User-Agent header
                Request request = chain.request().newBuilder()
                        .addHeader("User-Agent", "ResumeChecker-App")
                        .addHeader("Accept", "application/vnd.github.v3+json")
                        .addHeader("Authorization", "Bearer " + githubToken)
                        .build();
                return chain.proceed(request);
            })
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches complete GitHub profile for a username
     *
     * @param username - GitHub username (e.g. "torvalds")
     * @return GitHubProfile with all fetched data
     */
    public GitHubProfile fetchProfile(String username) throws Exception {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("GitHub username cannot be empty.");
        }

        // Clean username (remove @ if user typed it)
        username = username.trim().replace("@", "");

        GitHubProfile profile = new GitHubProfile();
        profile.setUsername(username);

        // Step 1: Fetch basic user profile
        fetchUserInfo(username, profile);

        // Step 2: Fetch repositories
        fetchRepositories(username, profile);

        // Step 3: Calculate language stats
        calculateLanguageStats(profile);

        return profile;
    }

    /**
     * Fetches basic user info: name, bio, followers, etc.
     */
    private void fetchUserInfo(String username, GitHubProfile profile) throws Exception {
        String url = "https://api.github.com/users/" + username;

        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                throw new IllegalArgumentException(
                    "GitHub user '" + username + "' not found. Please check the username."
                );
            }
            if (!response.isSuccessful()) {
                throw new RuntimeException("GitHub API error: " + response.code());
            }

            JsonNode user = objectMapper.readTree(response.body().string());

            profile.setName(user.path("name").asText(username));
            profile.setBio(user.path("bio").asText(""));
            profile.setPublicRepos(user.path("public_repos").asInt(0));
            profile.setFollowers(user.path("followers").asInt(0));
            profile.setFollowing(user.path("following").asInt(0));
            profile.setLocation(user.path("location").asText(""));
            profile.setProfileUrl("https://github.com/" + username);
            profile.setAvatarUrl(user.path("avatar_url").asText(""));
            profile.setCreatedAt(user.path("created_at").asText(""));
        }
    }

    /**
     * Fetches all public repositories (up to 100)
     */
    private void fetchRepositories(String username, GitHubProfile profile) throws Exception {
        String url = "https://api.github.com/users/" + username
                + "/repos?sort=updated&per_page=100&type=owner";

        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) return;

            JsonNode repos = objectMapper.readTree(response.body().string());
            List<GitHubProfile.Repository> repoList = new ArrayList<>();

            if (repos.isArray()) {
                for (JsonNode repo : repos) {
                    // Skip forked repos (not original work)
                    if (repo.path("fork").asBoolean(false)) continue;

                    GitHubProfile.Repository r = new GitHubProfile.Repository();
                    r.setName(repo.path("name").asText());
                    r.setDescription(repo.path("description").asText("No description"));
                    r.setLanguage(repo.path("language").asText(""));
                    r.setStars(repo.path("stargazers_count").asInt(0));
                    r.setForks(repo.path("forks_count").asInt(0));
                    r.setUpdatedAt(repo.path("updated_at").asText(""));
                    r.setUrl(repo.path("html_url").asText(""));
                    r.setIsForked(false);
                    repoList.add(r);
                }
            }

            profile.setRepositories(repoList);
        }
    }

    /**
     * Calculates which languages are used most across all repos
     */
    private void calculateLanguageStats(GitHubProfile profile) {
        Map<String, Integer> langCount = new LinkedHashMap<>();

        if (profile.getRepositories() != null) {
            for (GitHubProfile.Repository repo : profile.getRepositories()) {
                String lang = repo.getLanguage();
                if (lang != null && !lang.isBlank()) {
                    langCount.merge(lang, 1, Integer::sum);
                }
            }
        }

        // Sort by count descending
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(langCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        // Top 10 languages
        Map<String, Integer> topLangs = new LinkedHashMap<>();
        sorted.stream().limit(10).forEach(e -> topLangs.put(e.getKey(), e.getValue()));

        profile.setLanguageStats(topLangs);
        profile.setTopLanguages(new ArrayList<>(topLangs.keySet()));
    }

    /**
     * Builds a text summary of GitHub profile for the AI prompt
     */
    public String buildGitHubSummary(GitHubProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GITHUB PROFILE (Verified Real Data) ===\n");
        sb.append("Username: ").append(profile.getUsername()).append("\n");
        sb.append("Name: ").append(profile.getName()).append("\n");
        sb.append("Public Repositories: ").append(profile.getPublicRepos()).append("\n");
        sb.append("Followers: ").append(profile.getFollowers()).append("\n");
        sb.append("Account created: ").append(profile.getCreatedAt()).append("\n");

        if (!profile.getTopLanguages().isEmpty()) {
            sb.append("Top Languages (by repo count): ")
              .append(String.join(", ", profile.getTopLanguages()))
              .append("\n");
        }

        sb.append("\nOriginal Repositories:\n");
        if (profile.getRepositories() != null) {
            profile.getRepositories().stream().limit(15).forEach(repo -> {
                sb.append("  - ").append(repo.getName());
                if (!repo.getLanguage().isBlank()) {
                    sb.append(" [").append(repo.getLanguage()).append("]");
                }
                if (repo.getStars() > 0) {
                    sb.append(" ★").append(repo.getStars());
                }
                if (!repo.getDescription().equals("No description")) {
                    sb.append(": ").append(repo.getDescription());
                }
                sb.append("\n");
            });
        }
        sb.append("===========================================\n");
        return sb.toString();
    }
}