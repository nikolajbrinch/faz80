package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record Parameter(AssemblerToken name, Expression defaultValue) {}
