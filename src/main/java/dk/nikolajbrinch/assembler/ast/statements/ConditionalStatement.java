package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.parser.Line;

public record ConditionalStatement(Expression condition, Statement thenBranch, Statement elseBranch)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitConditionalStatement(this);
  }

  @Override
  public Line line() {
    return condition.line();
  }
}
