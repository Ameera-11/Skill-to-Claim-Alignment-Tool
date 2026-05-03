package com.resumechecker.model;

import java.util.List;
import java.util.Map;

/**
 * MODEL - GitHubProfile
 * Holds all data fetched from the GitHub API for a user.
 * This gets sent to the frontend along with the resume analysis.
 */
public class GitHubProfile {

    private String username;
    private String name;
    private String bio;
    private String location;
    private String profileUrl;
    private String avatarUrl;
    private String createdAt;
    private int publicRepos;
    private int followers;
    private int following;
    private List<String> topLanguages;
    private Map<String, Integer> languageStats;   // language -> repo count
    private List<Repository> repositories;

    // -------------------------------------------------------
    // INNER CLASS: One GitHub repository
    // -------------------------------------------------------
    public static class Repository {
        private String name;
        private String description;
        private String language;
        private int stars;
        private int forks;
        private String updatedAt;
        private String url;
        private boolean isForked;

        public Repository() {}

        // Getters and Setters
        public String getName()             { return name; }
        public void setName(String v)       { this.name = v; }
        public String getDescription()      { return description; }
        public void setDescription(String v){ this.description = v; }
        public String getLanguage()         { return language; }
        public void setLanguage(String v)   { this.language = v; }
        public int getStars()               { return stars; }
        public void setStars(int v)         { this.stars = v; }
        public int getForks()               { return forks; }
        public void setForks(int v)         { this.forks = v; }
        public String getUpdatedAt()        { return updatedAt; }
        public void setUpdatedAt(String v)  { this.updatedAt = v; }
        public String getUrl()              { return url; }
        public void setUrl(String v)        { this.url = v; }
        public boolean getIsForked()        { return isForked; }
        public void setIsForked(boolean v)  { this.isForked = v; }
    }

    // Getters and Setters
    public String getUsername()                     { return username; }
    public void setUsername(String v)               { this.username = v; }
    public String getName()                         { return name; }
    public void setName(String v)                   { this.name = v; }
    public String getBio()                          { return bio; }
    public void setBio(String v)                    { this.bio = v; }
    public String getLocation()                     { return location; }
    public void setLocation(String v)               { this.location = v; }
    public String getProfileUrl()                   { return profileUrl; }
    public void setProfileUrl(String v)             { this.profileUrl = v; }
    public String getAvatarUrl()                    { return avatarUrl; }
    public void setAvatarUrl(String v)              { this.avatarUrl = v; }
    public String getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(String v)              { this.createdAt = v; }
    public int getPublicRepos()                     { return publicRepos; }
    public void setPublicRepos(int v)               { this.publicRepos = v; }
    public int getFollowers()                       { return followers; }
    public void setFollowers(int v)                 { this.followers = v; }
    public int getFollowing()                       { return following; }
    public void setFollowing(int v)                 { this.following = v; }
    public List<String> getTopLanguages()           { return topLanguages; }
    public void setTopLanguages(List<String> v)     { this.topLanguages = v; }
    public Map<String, Integer> getLanguageStats()  { return languageStats; }
    public void setLanguageStats(Map<String, Integer> v) { this.languageStats = v; }
    public List<Repository> getRepositories()       { return repositories; }
    public void setRepositories(List<Repository> v) { this.repositories = v; }
}