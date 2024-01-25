package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record GlobalStatement(AssemblerToken identifier) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitGlobalStatement(this);
  }
  @Override
  public SourceInfo sourceInfo() { return identifier.sourceInfo(); }
  @Override
  public Line line() {
    return identifier.line();
  }
}
