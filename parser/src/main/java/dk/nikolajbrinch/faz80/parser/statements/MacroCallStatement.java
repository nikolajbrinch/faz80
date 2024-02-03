package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.util.List;

public record MacroCallStatement(AssemblerToken name, List<Statement> arguments)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroCallStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return name.sourceInfo(); }

  @Override
  public Line line() {
    return name.line();
  }
}
