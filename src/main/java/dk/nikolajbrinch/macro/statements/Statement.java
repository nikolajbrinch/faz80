package dk.nikolajbrinch.macro.statements;

import dk.nikolajbrinch.parser.Line;

public interface Statement {

  <R> R accept(StatementVisitor<R> visitor);
  Line line();
}
