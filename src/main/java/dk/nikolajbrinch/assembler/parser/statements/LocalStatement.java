package dk.nikolajbrinch.assembler.parser.statements;

public record LocalStatement(Statement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitLocalStatement(this);
  }
}
