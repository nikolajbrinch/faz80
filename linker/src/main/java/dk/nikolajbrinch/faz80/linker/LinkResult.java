package dk.nikolajbrinch.faz80.linker;

import dk.nikolajbrinch.faz80.parser.base.TaskResult;
import java.util.List;

public record LinkResult(Linked linked, List<LinkMessage> messages) implements TaskResult<LinkMessage> {}
