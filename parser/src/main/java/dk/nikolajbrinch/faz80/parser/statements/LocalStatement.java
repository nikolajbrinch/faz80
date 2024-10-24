package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record LocalStatement(AssemblerToken startToken, AssemblerToken endToken, BlockStatement block) implements Statement {

  @Override
  public SourceInfo sourceInfo() { return block.sourceInfo(); }

  @Override
  public Line line() {
    return block.line();
  }
}
