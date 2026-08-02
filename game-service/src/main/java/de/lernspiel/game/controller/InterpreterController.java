@RestController
@RequestMapping("/game/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @PostMapping("/run")
    public ResponseEntity<List<String>> run(@RequestBody ProgramRequest programRequest) {
        List<String> output = interpreterService.run(programRequest);
        return ResponseEntity.ok(output);
    }
}
