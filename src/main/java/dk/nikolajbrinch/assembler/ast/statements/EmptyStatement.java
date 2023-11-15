package dk.nikolajbrinch.assembler.ast.statements;

public record EmptyStatement() implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEmptyStatement(this);
  }
}
