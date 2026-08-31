package de.lernspiel.game.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.service.InterpreterService;

@RestController
@RequestMapping("/game/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @PostMapping("/run")
    public ResponseEntity<ExecutionLog> run(@RequestBody ProgramRequest programRequest) {
        ExecutionLog output = interpreterService.run(programRequest);

        return ResponseEntity.ok(output);
    }
}