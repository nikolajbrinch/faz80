package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record VariableStatement(AssemblerToken identifier, Expression intializer)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitVariableStatement(this);
  }
}
