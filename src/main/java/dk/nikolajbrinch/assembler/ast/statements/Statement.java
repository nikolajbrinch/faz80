package dk.nikolajbrinch.assembler.ast.statements;

public interface Statement {

  <R> R accept(StatementVisitor<R> visitor);
}
