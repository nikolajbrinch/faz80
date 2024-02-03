package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record Parameter(AssemblerToken name, Expression defaultValue) {}
