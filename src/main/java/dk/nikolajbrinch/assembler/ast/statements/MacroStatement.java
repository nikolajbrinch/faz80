package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import java.util.List;

public record MacroStatement(AssemblerToken name, List<Parameter> parameters, Statement block)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroStatement(this);
  }
}
