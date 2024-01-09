package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record IncludeStatement(MacroToken include, List<MacroToken> space, MacroToken filename)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIncludeStatement(this);
  }

  @Override
  public Line line() {
    return include.line();
  }
}
