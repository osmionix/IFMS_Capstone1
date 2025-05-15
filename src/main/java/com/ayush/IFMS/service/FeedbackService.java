package com.ayush.IFMS.service;

import com.ayush.IFMS.dto.FeedbackDTO;
import com.ayush.IFMS.dto.SkillEvaluationDTO;
import com.ayush.IFMS.model.*;
import com.ayush.IFMS.repository.FeedbackRepository;
import com.ayush.IFMS.repository.InterviewRepository;
import com.ayush.IFMS.repository.CandidateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, InterviewRepository interviewRepository, CandidateRepository candidateRepository) {
        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
    }

    public FeedbackDTO saveFeedback(FeedbackDTO dto) {
        Feedback feedback = new Feedback();

        // Load the Interview by ID and set it
        Interview interview = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found with ID: " + dto.getInterviewId()));
        feedback.setInterview(interview);

        // Set the interviewer directly from the Interview object
        User interviewer = interview.getInterviewer();  // The interviewer is already set in the interview entity
        feedback.setInterviewer(interviewer);  // Set interviewer to feedback directly

        // Ensure Candidate is populated
        Candidate candidate = candidateRepository.findByEmailAndJobRole(dto.getCandidateEmail(), dto.getJobRole())
                .orElseThrow(() -> new RuntimeException("Candidate not found with email: " + dto.getCandidateEmail()));
        feedback.setCandidate(candidate);  // Set the candidate for feedback

        // Set other fields
        feedback.setCandidateName(dto.getCandidateName());
        feedback.setCandidateEmail(dto.getCandidateEmail());
        feedback.setInterviewDate(LocalDateTime.now()); 
        feedback.setJobRole(dto.getJobRole());
        feedback.setInterviewerEmail(interviewer.getEmail());
        feedback.setRound(dto.getRound());
        feedback.setDecision(dto.getDecision());
        feedback.setFinalComments(dto.getOverallComments());

        // Set skill evaluations
        List<SkillEvaluation> evaluations = dto.getSkillEvaluations().stream().map(s -> {
            SkillEvaluation se = new SkillEvaluation();
            se.setSkillName(s.getSkillName());
            se.setRating(s.getRating());
            se.setTopics(s.getTopics());
            se.setComments(s.getComments());
            se.setFeedback(feedback);
            return se;
        }).collect(Collectors.toList());

        feedback.setSkillEvaluations(evaluations);

        // Save and return
        Feedback saved = feedbackRepository.save(feedback);

        interview.setStatus(InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        dto.setInterviewId(saved.getId());  // this is feedback ID
        return dto;
    }

    public Feedback getFeedbackByInterviewId(Long interviewId) {
        return feedbackRepository.findByInterviewId(interviewId);
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setInterviewId(feedback.getInterview().getId());
        dto.setCandidateName(feedback.getCandidateName());
        dto.setCandidateEmail(feedback.getCandidateEmail());
        dto.setInterviewDate(feedback.getInterviewDate());
        dto.setJobRole(feedback.getJobRole());
        dto.setInterviewerEmail(feedback.getInterviewerEmail());
        dto.setRound(feedback.getRound());
        dto.setDecision(feedback.getDecision());
        dto.setOverallComments(feedback.getFinalComments());

        // Convert SkillEvaluations to DTOs
        if (feedback.getSkillEvaluations() != null) {
            List<SkillEvaluationDTO> skillDTOs = feedback.getSkillEvaluations().stream()
                    .map(se -> {
                        SkillEvaluationDTO seDTO = new SkillEvaluationDTO();
                        seDTO.setSkillName(se.getSkillName());
                        seDTO.setRating(se.getRating());
                        seDTO.setTopics(se.getTopics());
                        seDTO.setComments(se.getComments());
                        return seDTO;
                    })
                    .collect(Collectors.toList());
            dto.setSkillEvaluations(skillDTOs);
        }

        return dto;
    }

    public Long getFeedbackIdByCandidateDetails(String candidateName, String jobRole, InterviewRound round) {
        Feedback feedback = feedbackRepository.findByCandidateAndJobRoleAndRound(candidateName, jobRole, round);
        if (feedback == null) {
            throw new RuntimeException("Feedback not found for candidate: " + candidateName);
        }
        return feedback.getId();
    }
    public FeedbackDTO getFeedbackDetails(Long feedbackId) {
        // Find the feedback by ID or throw exception if not found
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));

        // Use the convertToDTO method to transform the entity
        FeedbackDTO feedbackDTO = convertToDTO(feedback);

        // Ensure the feedback ID is set
        feedbackDTO.setId(feedbackId);

        return feedbackDTO;
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

}
