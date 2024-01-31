package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record EmptyStatement(SourceInfo sourceInfo, Line line) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }
}
