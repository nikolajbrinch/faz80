package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

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
