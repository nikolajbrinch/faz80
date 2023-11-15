package dk.nikolajbrinch.assembler.ast.statements;

import java.util.List;

public record BlockStatement(List<Statement> statements) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitBlockStatement(this);
  }
}
