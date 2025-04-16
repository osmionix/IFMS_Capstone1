package com.ayush.IFMS.repository;

import com.ayush.IFMS.model.Decision;
import com.ayush.IFMS.model.Interview;
import com.ayush.IFMS.model.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByInterviewTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Interview> findByStatus(InterviewStatus status);
    List<Interview> findByInterviewerId(Long interviewerId);
    List<Interview> findByCandidateId(Long candidateId);
    List<Interview> findByInterviewerIdAndStatus(Long interviewerId, InterviewStatus status);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.feedbacks WHERE i.id = :id")
    Optional<Interview> findByIdWithFeedbacks(@Param("id") Long id);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.feedbacks WHERE i.candidate.id = :candidateId")
    List<Interview> findByCandidateIdWithFeedbacks(@Param("candidateId") Long candidateId);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.feedbacks f WHERE f.decision = :decision")
    List<Interview> findByFeedbackDecision(@Param("decision") Decision decision);

    boolean existsByCandidateIdAndStatus(Long candidateId, InterviewStatus status);

    @Query("SELECT COUNT(i) > 0 FROM Interview i JOIN i.feedbacks f WHERE i.candidate.id = :candidateId AND f.decision = :decision")
    boolean existsByCandidateIdAndFeedbackDecision(@Param("candidateId") Long candidateId,
                                                   @Param("decision") Decision decision);
}