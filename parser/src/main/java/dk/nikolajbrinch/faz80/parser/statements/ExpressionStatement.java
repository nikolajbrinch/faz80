package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record ExpressionStatement(Expression expression) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitExpressionStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return expression.sourceInfo(); }

  @Override
  public Line line() {
    return expression.line();
  }
}
