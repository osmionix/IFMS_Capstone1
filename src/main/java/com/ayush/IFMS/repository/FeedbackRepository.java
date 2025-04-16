package com.ayush.IFMS.repository;

import com.ayush.IFMS.model.Feedback;
import com.ayush.IFMS.model.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback findByInterviewId(Long interviewId);

    @Query("SELECT f FROM Feedback f WHERE f.candidateName = :candidateName AND f.jobRole = :jobRole AND f.round = :round")
    Feedback findByCandidateAndJobRoleAndRound(
            @Param("candidateName") String candidateName,
            @Param("jobRole") String jobRole,
            @Param("round") InterviewRound round);
}
