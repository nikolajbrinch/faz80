package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;

public record Parameter(AssemblerToken name, Expression defaultValue) {}
