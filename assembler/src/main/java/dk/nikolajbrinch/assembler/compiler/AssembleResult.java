package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.parser.TaskResult;
import java.util.List;

public record AssembleResult(Assembled assembled, List<AssembleError> errors) implements TaskResult<AssembleError> {

}
