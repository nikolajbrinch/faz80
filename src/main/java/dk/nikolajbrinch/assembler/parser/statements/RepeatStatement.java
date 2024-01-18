package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record RepeatStatement(Expression count, Statement blockStatement) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitRepeatStatement(this);
  }

  @Override
  public Line line() {
    return count.line();
  }
}
