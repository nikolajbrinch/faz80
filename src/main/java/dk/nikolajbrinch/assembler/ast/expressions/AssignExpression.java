package dk.nikolajbrinch.assembler.ast.expressions;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record AssignExpression(AssemblerToken identifier, Expression expression)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAssignExpression(this);
  }

  @Override
  public Line line() {
    return identifier.line();
  }
}
