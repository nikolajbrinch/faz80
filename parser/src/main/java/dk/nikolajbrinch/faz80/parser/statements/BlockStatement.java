package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.util.List;

public record BlockStatement(SymbolTable symbols, List<Statement> statements) implements Statement {

  @Override
  public SourceInfo sourceInfo() {
    return statements.isEmpty()
        ? null
        : statements.get(0) == null ? null : statements.get(0).sourceInfo();
  }

  @Override
  public Line line() {
    return statements.isEmpty()
        ? null
        : statements.get(0) == null ? null : statements.get(0).line();
  }

  public List<Line> lines() {
    return statements.stream().map(Statement::line).toList();
  }
}
