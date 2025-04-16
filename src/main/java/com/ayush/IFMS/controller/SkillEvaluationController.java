package com.ayush.IFMS.controller;

import com.ayush.IFMS.dto.SkillEvaluationDTO;
import com.ayush.IFMS.model.SkillEvaluation;
import com.ayush.IFMS.service.SkillEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillEvaluationController {

    private final SkillEvaluationService service;

    public SkillEvaluationController(SkillEvaluationService service) {
        this.service = service;
    }

    @GetMapping
    public List<SkillEvaluation> getAllSkills() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillEvaluation> getSkill(@PathVariable Long id) {
        SkillEvaluation skill = service.getById(id);
        if (skill != null) {
            return ResponseEntity.ok(skill);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SkillEvaluation> addSkill(@RequestBody SkillEvaluation skill) {
        return ResponseEntity.ok(service.save(skill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    //get skills by using feedback id
    @GetMapping("/by-feedback/{feedbackId}")
    public ResponseEntity<List<SkillEvaluationDTO>> getSkillsByFeedbackId(
            @PathVariable Long feedbackId) {
        List<SkillEvaluationDTO> skills = service.getSkillsByFeedbackId(feedbackId);
        return ResponseEntity.ok(skills);
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
