package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public interface Statement {
  <R> R accept(StatementVisitor<R> visitor);

  SourceInfo sourceInfo();

  Line line();
}
