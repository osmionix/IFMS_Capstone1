package com.ayush.IFMS.dto;

import com.ayush.IFMS.model.Rating;

public class SkillEvaluationDTO {
    private String skillName;
    private Rating rating;
    private String topics;
    private String comments;

    // Getters and setters

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
