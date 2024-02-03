package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.parser.Parameter;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.util.List;

public record MacroStatement(
    AssemblerToken name, SymbolTable symbolTable, List<Parameter> parameters, BlockStatement block)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return name.sourceInfo(); }

  @Override
  public Line line() {
    return name.line();
  }
}
