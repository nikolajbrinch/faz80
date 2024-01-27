package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.ParseError;
import dk.nikolajbrinch.parser.TaskResult;
import java.util.List;

public record AssemblerParseResult(BlockStatement block, List<ParseError> errors)
    implements TaskResult<ParseError> {}
