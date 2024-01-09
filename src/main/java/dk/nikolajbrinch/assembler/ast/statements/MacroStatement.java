package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record MacroStatement(AssemblerToken name, List<Parameter> parameters, BlockStatement block)
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
