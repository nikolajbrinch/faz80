package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record MacroStatement(
    AssemblerToken name, SymbolTable symbolTable, List<Parameter> parameters, BlockStatement block)
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
