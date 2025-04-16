package com.ayush.IFMS.controller;

import com.ayush.IFMS.dto.CandidateDTO;
import com.ayush.IFMS.service.CandidateService;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public List<CandidateDTO> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{id}")
    public CandidateDTO getCandidateById(@PathVariable Long id) {
        return candidateService.getCandidateById(id);
    }

    @PostMapping
    public CandidateDTO createCandidate(@RequestBody CandidateDTO candidateDTO) {
        return candidateService.createCandidate(candidateDTO);
    }
}