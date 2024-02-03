package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public interface Statement {
  <R> R accept(StatementVisitor<R> visitor);

  SourceInfo sourceInfo();

  Line line();
}
