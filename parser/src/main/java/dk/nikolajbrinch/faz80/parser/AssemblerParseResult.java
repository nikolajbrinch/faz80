package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.ParseError;
import dk.nikolajbrinch.faz80.base.TaskResult;
import java.util.List;

public record AssemblerParseResult(BlockStatement block, List<ParseError> errors)
    implements TaskResult<ParseError> {}
