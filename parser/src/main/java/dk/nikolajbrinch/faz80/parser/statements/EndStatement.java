package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record EndStatement(AssemblerToken token) implements Statement {

  @Override
  public SourceInfo sourceInfo() {
    return token.sourceInfo();
  }

  @Override
  public Line line() {
    return token.line();
  }
}
