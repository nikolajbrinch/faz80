package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.parser.statements.Statement;

public record StatementSymbol(Statement value) implements Symbol<Statement> {}
