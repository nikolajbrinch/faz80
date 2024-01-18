package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;

public record EmptyStatement(Line line) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }
}
