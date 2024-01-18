package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.parser.Line;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);

  Line line();
}
