package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.base.TaskResult;
import java.util.List;

public record AssembleResult(Assembled assembled, List<AssembleError> errors) implements TaskResult<AssembleError> {

}
