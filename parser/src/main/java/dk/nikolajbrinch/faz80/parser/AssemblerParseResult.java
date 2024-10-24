package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.base.TaskResult;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.ParseMessage;
import java.util.List;

public record AssemblerParseResult(BlockStatement block, List<ParseMessage> messages)
    implements TaskResult<ParseMessage> {}
