package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record RepeatStatement(AssemblerToken startToken, AssemblerToken endToken, Expression count, BlockStatement block) implements Statement {

  @Override
  public SourceInfo sourceInfo() { return count.sourceInfo(); }

  @Override
  public Line line() {
    return count.line();
  }
}
