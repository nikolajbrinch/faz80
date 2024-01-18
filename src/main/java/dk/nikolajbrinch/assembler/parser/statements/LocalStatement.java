package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;

public record LocalStatement(BlockStatement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitLocalStatement(this);
  }

  @Override
  public Line line() {
    return block.line();
  }
}
