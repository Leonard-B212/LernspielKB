@RestController
@RequestMapping("/game/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @PostMapping("/run")
    public ResponseEntity<InterpreterResult> run(@RequestBody List<CodeBlock> program) {
        InterpreterResult result = interpreterService.run(program);
        return ResponseEntity.ok(result);
    }
}
