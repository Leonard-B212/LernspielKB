package de.lernspiel.level.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.level.dto.LevelVerificationRequest;
import de.lernspiel.level.service.LevelVerificationService;

@RestController
@RequestMapping("/api/levelVerification")
public class LevelVerificationController {
    @Autowired
    private LevelVerificationService levelVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody LevelVerificationRequest request) {
        return ResponseEntity.ok(levelVerificationService.verify(request.getExpectedExecutionLog(), request.getActualExecutionLog()));
    }
}
