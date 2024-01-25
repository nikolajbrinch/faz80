package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);

  SourceInfo sourceInfo();

  Line line();
}
