package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.parser.Line;

public interface Statement {
  <R> R accept(StatementVisitor<R> visitor);

  Line line();
}
