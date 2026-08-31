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

import de.lernspiel.game.dto.ExecutionLog;
import de.lernspiel.game.dto.LogFile;
import de.lernspiel.game.dto.LogType;
import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.service.InterpreterService;
import de.lernspiel.game.service.LevelVerificationService;

@RestController
@RequestMapping("/game/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;
    @Autowired
    private LevelVerificationService levelVerificationService;

    @PostMapping("/run")
    public ResponseEntity<ExecutionLog> run(@RequestBody ProgramRequest programRequest) {
        ExecutionLog output = interpreterService.run(programRequest);
        ExecutionLog expectedOutput = new ExecutionLog(); //TODO

        if(levelVerificationService.verify(expectedOutput, output)){
            output.add(new LogFile(new HashMap<>(), LogType.LEVEL_SUCCESSFUL));
            return ResponseEntity.ok(output);
        } else {
            output.add(new LogFile(new HashMap<>(), LogType.LEVEL_UNSUCCESSFUL));
            return ResponseEntity.ok(output);
        }
        
    }
}