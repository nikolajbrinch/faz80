package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record LocalStatement(BlockStatement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitLocalStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return block.sourceInfo(); }

  @Override
  public Line line() {
    return block.line();
  }
}
