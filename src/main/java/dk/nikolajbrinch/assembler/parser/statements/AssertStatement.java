package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record AssertStatement(Expression expression) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitAssertStatement(this);
  }
  @Override
  public SourceInfo sourceInfo() { return expression.sourceInfo(); }

  @Override
  public Line line() {
    return expression.line();
  }
}
