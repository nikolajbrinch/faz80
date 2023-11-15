package dk.nikolajbrinch.assembler.parser.statements;

public interface Statement {

  <R> R accept(StatementVisitor<R> visitor);
}
