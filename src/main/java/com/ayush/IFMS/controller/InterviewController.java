package com.ayush.IFMS.controller;

import com.ayush.IFMS.dto.InterviewDTO;
import com.ayush.IFMS.dto.InterviewerRequestDTO;
import com.ayush.IFMS.model.InterviewStatus;
import com.ayush.IFMS.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<InterviewDTO>> getAllInterviews() {
        List<InterviewDTO> interviews = interviewService.getAllInterviews();
        return ResponseEntity.ok(interviews);
    }

    @GetMapping
    public List<InterviewDTO> getInterviews(
            @RequestParam(required = false) Long interviewer,
            @RequestParam(required = false) InterviewStatus status
    ) {
        if (interviewer != null && status != null) {
            return interviewService.getInterviewsByInterviewerAndStatus(interviewer, status);
        } else if (status != null) {
            return interviewService.getInterviewsByStatus(status);
        } else if (interviewer != null) {
            return interviewService.getInterviewsByInterviewer(interviewer);
        } else {
            return interviewService.getAllInterviews();
        }
    }
    //Get interview by interviewer id
    @PostMapping("/by-interviewer")
    public List<InterviewDTO> getInterviewsByInterviewerPost(@RequestBody InterviewerRequestDTO request) {
        return interviewService.getInterviewsByInterviewer(request.getInterviewerId());
    }

    //Get interview by ID
    @GetMapping("/{id}")
    public InterviewDTO getInterviewById(@PathVariable Long id) {
        return interviewService.getInterviewById(id);
    }

    //Get interviews for calendar view (time-based range)
    @GetMapping("/calendar")
    public List<InterviewDTO> getInterviewsBetween(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return interviewService.getInterviewsBetween(start, end);
    }

    //Schedule a new interview
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleInterview(@RequestBody InterviewDTO interviewDTO) {
        try {
            interviewService.scheduleInterview(interviewDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Interview scheduled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to schedule interview.");
        }
    }

}
