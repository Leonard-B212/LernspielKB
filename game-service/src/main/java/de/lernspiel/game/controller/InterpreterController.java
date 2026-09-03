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
import de.lernspiel.common.code.LogFile;
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

        createReadableLog(output);

        return ResponseEntity.ok(output);
    }

    public void createReadableLog(ExecutionLog output){
        List<String> readableLog = new ArrayList<>();
        for(LogFile log : output.getEntries()){
            switch(log.getLogType()){
                case PROGRAM_START:
                    readableLog.add("Programmdurchlauf gestartet");
                    break;
                case SIMPLE_VARIABLE_DECLARATION:
                    readableLog.add("Variable " + (String) log.getContents().get("variableName") + " wurde deklariert");
                    break;
                case VARIABLE_DECLARATION_ASSIGNMENT:
                    readableLog.add("Variable " + (String) log.getContents().get("variableName") + " wurde mit dem Wert " + log.getContents().get("variableValue") + " deklariert");
                    break;
                case VARIABLE_VALUE_ASSIGNMENT:
                    readableLog.add("Variable " + (String) log.getContents().get("variableName") + " wurde der Wert " + log.getContents().get("variableValue") + " zugewiesen");
                    break;
                case ERROR:
                    readableLog.add("Fehler: " + (String) log.getContents().get("errorMessage"));
                case PROGRAM_END:
                    readableLog.add("Programmdurchlauf beendet");
                default:
                    break;
            }
        }
        output.setReadableLog(readableLog);
    }
}