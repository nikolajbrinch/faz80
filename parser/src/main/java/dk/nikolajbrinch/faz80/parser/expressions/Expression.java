package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);

  SourceInfo sourceInfo();

  Line line();
}
