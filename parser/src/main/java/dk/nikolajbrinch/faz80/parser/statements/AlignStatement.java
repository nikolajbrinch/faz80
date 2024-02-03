package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record AlignStatement(Expression alignment, Expression fillByte) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitAlignStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return alignment.sourceInfo(); }

  @Override
  public Line line() {
    return alignment.line();
  }
}
