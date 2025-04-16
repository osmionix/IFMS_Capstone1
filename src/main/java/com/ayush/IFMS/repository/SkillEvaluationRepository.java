package com.ayush.IFMS.repository;

import com.ayush.IFMS.model.SkillEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillEvaluationRepository extends JpaRepository<SkillEvaluation, Long> {
    List<SkillEvaluation> findByFeedbackId(Long feedbackId);
}
