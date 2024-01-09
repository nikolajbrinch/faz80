package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record WordStatement(List<Expression> values) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitWordStatement(this);
  }

  @Override
  public Line line() {
    return values.isEmpty() ? null : values.get(0).line();
  }

  public List<Line> lines() {
    return values.stream().map(Expression::line).toList();
  }
}
