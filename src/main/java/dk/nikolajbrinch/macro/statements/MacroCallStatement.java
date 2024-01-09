package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record MacroCallStatement(MacroToken name, List<dk.nikolajbrinch.macro.parser.Argument> arguments) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroCallStatement(this);
  }

  @Override
  public Line line() {
    return name.line();
  }
}
