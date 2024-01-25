package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;
import java.util.List;

public record DataLongStatement(List<Expression> values) implements ValuesStatement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitDataLongStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() {
    return values.isEmpty() ? null : values.get(0).sourceInfo();
  }
  @Override
  public Line line() {
    return values.isEmpty() ? null : values.get(0).line();
  }

  public List<Line> lines() {
    return values.stream().map(Expression::line).toList();
  }
}
