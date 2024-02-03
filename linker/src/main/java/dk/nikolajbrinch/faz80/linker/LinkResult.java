package dk.nikolajbrinch.faz80.linker;

import dk.nikolajbrinch.faz80.base.TaskResult;
import java.util.List;

public record LinkResult(Linked linked, List<LinkError> errors) implements TaskResult<LinkError> {}
