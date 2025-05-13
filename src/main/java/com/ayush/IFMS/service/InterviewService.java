package com.ayush.IFMS.service;

import com.ayush.IFMS.dto.InterviewDTO;
import com.ayush.IFMS.dto.CandidateDTO;
import com.ayush.IFMS.model.*;
import com.ayush.IFMS.repository.CandidateRepository;
import com.ayush.IFMS.repository.InterviewRepository;
import com.ayush.IFMS.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public InterviewService(InterviewRepository interviewRepository,
                            CandidateRepository candidateRepository,
                            UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    public List<InterviewDTO> getInterviewsBetween(LocalDateTime start, LocalDateTime end) {
        return interviewRepository.findByInterviewTimeBetween(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InterviewDTO getInterviewById(Long id) {
        return interviewRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
    }

    public List<InterviewDTO> getAllInterviews() {
        List<Interview> interviews = interviewRepository.findAll();
        return interviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InterviewDTO scheduleInterview(InterviewDTO interviewDTO) {
        Interview interview = new Interview();

        CandidateDTO dto = interviewDTO.getCandidate();

        // Check if candidate exists with the same email and job role
        Candidate candidate = candidateRepository.findByEmailAndJobRole(dto.getEmail(), dto.getJobRole())
                .orElseGet(() -> {
                    // If the candidate doesn't exist, create a new one
                    Candidate newCandidate = new Candidate();
                    newCandidate.setName(dto.getName());
                    newCandidate.setEmail(dto.getEmail());
                    newCandidate.setJobRole(dto.getJobRole());
                    return candidateRepository.save(newCandidate); // Save the new candidate
                });

        User interviewer = userRepository.findById(interviewDTO.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        interview.setCandidate(candidate);
        interview.setInterviewer(interviewer);
        interview.setInterviewTime(interviewDTO.getInterviewTime());
        interview.setRound(interviewDTO.getRound());
        interview.setStatus(InterviewStatus.SCHEDULED);

        Interview savedInterview = interviewRepository.save(interview);
        return convertToDTO(savedInterview);
    }


    private InterviewDTO convertToDTO(Interview interview) {
        InterviewDTO dto = new InterviewDTO();
        dto.setId(interview.getId());
        dto.setCandidate(new CandidateDTO(
                interview.getCandidate().getId(),
                interview.getCandidate().getName(),
                interview.getCandidate().getEmail(),
                interview.getCandidate().getJobRole()
        ));
        dto.setInterviewerId(interview.getInterviewer().getId());
        dto.setInterviewerName(interview.getInterviewer().getName());
        dto.setInterviewTime(interview.getInterviewTime());
        dto.setRound(InterviewRound.valueOf(interview.getRound().name()));
        dto.setStatus(InterviewStatus.valueOf(interview.getStatus().name()));
        return dto;
    }

    public List<InterviewDTO> getInterviewsByInterviewerAndStatus(Long interviewerId, InterviewStatus status) {
        return interviewRepository.findByInterviewerIdAndStatus(interviewerId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InterviewDTO> getInterviewsByStatus(InterviewStatus status) {
        return interviewRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InterviewDTO> getInterviewsByInterviewer(Long interviewerId) {
        return interviewRepository.findByInterviewerId(interviewerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
