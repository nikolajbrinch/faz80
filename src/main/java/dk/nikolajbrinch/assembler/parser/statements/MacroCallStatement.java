package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;
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
