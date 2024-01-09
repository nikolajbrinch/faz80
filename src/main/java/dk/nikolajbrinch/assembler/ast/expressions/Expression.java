package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.parser.Line;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);

  Line line();
}
