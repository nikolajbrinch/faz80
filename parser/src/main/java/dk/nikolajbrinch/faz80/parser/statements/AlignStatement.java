package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record AlignStatement(AssemblerToken token, Expression alignment, Expression fillByte) implements Statement {

  @Override
  public SourceInfo sourceInfo() { return alignment.sourceInfo(); }

  @Override
  public Line line() {
    return alignment.line();
  }
}
