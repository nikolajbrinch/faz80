package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record AssignStatement(AssemblerToken identifier, SymbolType type, Expression initializer)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitAssignStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return identifier.sourceInfo(); }

  @Override
  public Line line() {
    return identifier.line();
  }
}
