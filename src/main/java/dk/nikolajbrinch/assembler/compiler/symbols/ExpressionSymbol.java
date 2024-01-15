package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;

public record ExpressionSymbol(Expression value) implements Symbol<Expression> {}
