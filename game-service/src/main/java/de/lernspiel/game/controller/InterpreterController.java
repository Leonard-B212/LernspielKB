package de.lernspiel.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.service.InterpreterService;

@RestController
@RequestMapping("/game/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @PostMapping("/run")
    public ResponseEntity<List<String>> run(@RequestBody ProgramRequest programRequest) {
        List<String> output = interpreterService.run(programRequest);
        for(String s : output){
            System.out.println(s);
        }
        return ResponseEntity.ok(output);
    }
}
