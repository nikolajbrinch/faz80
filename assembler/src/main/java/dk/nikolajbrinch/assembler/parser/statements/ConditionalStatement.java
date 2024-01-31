package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.SourceInfo;

public record ConditionalStatement(Expression condition, Statement thenBranch, Statement elseBranch)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitConditionalStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return condition.sourceInfo(); }

  @Override
  public Line line() {
    return condition.line();
  }
}
