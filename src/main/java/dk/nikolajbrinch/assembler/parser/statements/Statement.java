package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;

public interface Statement {
  <R> R accept(StatementVisitor<R> visitor);

  Line line();
}
