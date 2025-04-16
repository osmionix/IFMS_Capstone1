package com.ayush.IFMS.controller;

import com.ayush.IFMS.dto.FeedbackDTO;
import com.ayush.IFMS.model.Feedback;
import com.ayush.IFMS.model.InterviewRound;
import com.ayush.IFMS.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            FeedbackDTO saved = feedbackService.saveFeedback(feedbackDTO);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbackDTOs = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbackDTOs);
    }

    //get feedback details using interview id
    @GetMapping("/{interviewId}")
    public ResponseEntity<Feedback> getFeedbackByInterviewId(@PathVariable Long interviewId) {
        Feedback feedback = feedbackService.getFeedbackByInterviewId(interviewId);
        if (feedback != null) {
            return ResponseEntity.ok(feedback);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/find-id")
    public ResponseEntity<Long> getFeedbackId(
            @RequestParam String candidateName,
            @RequestParam String jobRole,
            @RequestParam InterviewRound round) {

        Long feedbackId = feedbackService.getFeedbackIdByCandidateDetails(candidateName, jobRole, round);
        return ResponseEntity.ok(feedbackId);
    }

    @GetMapping("/details/{feedbackId}")
    public ResponseEntity<FeedbackDTO> getFeedbackDetails(@PathVariable Long feedbackId) {
        FeedbackDTO feedback = feedbackService.getFeedbackDetails(feedbackId);
        return ResponseEntity.ok(feedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
