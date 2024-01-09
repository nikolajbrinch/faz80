package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record TextStatement(List<MacroToken> tokens) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitTextStatement(this);
  }

  @Override
  public Line line() {
    return tokens.isEmpty() ? null : tokens.get(0).line();
  }

  public List<Line> lines() {
    return tokens.stream().map(MacroToken::line).toList();
  }
}
