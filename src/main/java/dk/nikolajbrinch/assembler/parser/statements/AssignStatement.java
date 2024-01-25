package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

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
