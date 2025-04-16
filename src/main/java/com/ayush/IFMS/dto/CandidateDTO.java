package com.ayush.IFMS.dto;

public class CandidateDTO {
    private Long id;
    private String name;
    private String email;
    private String jobRole;

    // No-arg constructor
    public CandidateDTO() {}

    // All-args constructor
    public CandidateDTO(Long id, String name, String email, String jobRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.jobRole = jobRole;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
}