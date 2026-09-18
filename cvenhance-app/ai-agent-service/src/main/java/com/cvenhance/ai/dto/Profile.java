package com.cvenhance.ai.dto;

import java.util.List;

public record Profile(
        String name,
        String location,
        String phone,
        String email,
        String linkedin,
        String linkedinUrl,
        String github,
        String githubUrl,
        String summary,
        List<Education> education,
        List<Skill> skills,
        List<Experience> experience,
        List<Project> projects,
        List<String> achievements
) {
    public record Education(String school, String degree, String location, String period) { }
    public record Skill(String label, String value) { }
    public record Experience(String company, String role, String location, String period, List<String> bullets) { }
    public record Project(String name, String tech, String githubUrl, String liveUrl, List<String> bullets) { }
}
