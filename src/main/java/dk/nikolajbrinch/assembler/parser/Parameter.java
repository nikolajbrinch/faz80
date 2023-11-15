package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.Token;

public record Parameter(Token name, Expression defaultValue) {}
