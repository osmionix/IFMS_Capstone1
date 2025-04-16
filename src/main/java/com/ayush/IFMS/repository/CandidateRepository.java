package com.ayush.IFMS.repository;

import com.ayush.IFMS.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByEmailAndJobRole(String email, String jobRole);
}