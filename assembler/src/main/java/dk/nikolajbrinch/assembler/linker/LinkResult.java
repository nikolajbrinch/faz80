package dk.nikolajbrinch.assembler.linker;

import dk.nikolajbrinch.parser.TaskResult;
import java.util.List;

public record LinkResult(Linked linked, List<LinkError> errors) implements TaskResult<LinkError> {}
