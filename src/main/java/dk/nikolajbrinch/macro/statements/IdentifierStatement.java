package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.parser.Line;

public record IdentifierStatement(MacroToken identifier) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIdentifierStatement(this);
  }

  @Override
  public Line line() {
    return identifier.line();
  }
}
