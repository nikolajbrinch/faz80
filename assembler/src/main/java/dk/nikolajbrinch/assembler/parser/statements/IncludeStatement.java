package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record IncludeStatement(AssemblerToken string) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIncludeStatement(this);
  }
  @Override
  public SourceInfo sourceInfo() { return string.sourceInfo(); }

  @Override
  public Line line() {
    return string.line();
  }
}
