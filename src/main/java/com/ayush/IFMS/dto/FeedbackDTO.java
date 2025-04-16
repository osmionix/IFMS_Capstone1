package com.ayush.IFMS.dto;

import com.ayush.IFMS.model.Decision;
import com.ayush.IFMS.model.InterviewRound;
import java.time.LocalDateTime;
import java.util.List;

public class FeedbackDTO {
    private long id;
    private Long interviewId;
    private String candidateName;
    private String candidateEmail;
    private LocalDateTime interviewDate;
    private String jobRole;
    private String interviewerEmail;
    private InterviewRound round;
    private Decision decision;
    private String overallComments;

    private List<SkillEvaluationDTO> skillEvaluations;

    // Getters and setters

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public LocalDateTime getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDateTime interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getInterviewerEmail() {
        return interviewerEmail;
    }

    public void setInterviewerEmail(String interviewerEmail) {
        this.interviewerEmail = interviewerEmail;
    }

    public InterviewRound getRound() {
        return round;
    }

    public void setRound(InterviewRound round) {
        this.round = round;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public String getOverallComments() {
        return overallComments;
    }

    public void setOverallComments(String overallComments) {
        this.overallComments = overallComments;
    }

    public List<SkillEvaluationDTO> getSkillEvaluations() {
        return skillEvaluations;
    }

    public void setSkillEvaluations(List<SkillEvaluationDTO> skillEvaluations) {
        this.skillEvaluations = skillEvaluations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
