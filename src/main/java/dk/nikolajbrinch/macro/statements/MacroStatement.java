package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.macro.parser.Parameter;
import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record MacroStatement(MacroToken name, List<Parameter> parameters, List<MacroToken> lineEnd, List<MacroToken> body)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroStatement(this);
  }

  @Override
  public Line line() {
    return name.line();
  }
}
