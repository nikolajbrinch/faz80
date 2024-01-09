package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.parser.Line;

public record EmptyStatement(Line line) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }

}
