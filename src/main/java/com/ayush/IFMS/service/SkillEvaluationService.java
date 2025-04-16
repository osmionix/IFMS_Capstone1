package com.ayush.IFMS.service;

import com.ayush.IFMS.dto.SkillEvaluationDTO;
import com.ayush.IFMS.model.SkillEvaluation;
import com.ayush.IFMS.repository.SkillEvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillEvaluationService {

    private final SkillEvaluationRepository repository;

    public SkillEvaluationService(SkillEvaluationRepository repository) {
        this.repository = repository;
    }

    public List<SkillEvaluation> getAll() {
        return repository.findAll();
    }

    public SkillEvaluation getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public SkillEvaluation save(SkillEvaluation evaluation) {
        return repository.save(evaluation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<SkillEvaluationDTO> getSkillsByFeedbackId(Long feedbackId) {
        List<SkillEvaluation> skills = repository.findByFeedbackId(feedbackId);
        return skills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SkillEvaluationDTO convertToDTO(SkillEvaluation skill) {
        SkillEvaluationDTO dto = new SkillEvaluationDTO();
        dto.setSkillName(skill.getSkillName());
        dto.setRating(skill.getRating());
        dto.setTopics(skill.getTopics());
        dto.setComments(skill.getComments());
        return dto;
    }
}
