package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.parser.base.TaskResult;
import java.util.List;

public record AssembleResult(Assembled assembled, List<AssembleMessage> messages) implements TaskResult<AssembleMessage> {

}
