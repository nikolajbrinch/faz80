package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record ConditionalStatement(
    AssemblerToken ifToken,
    AssemblerToken elseToken,
    AssemblerToken endToken,
    Expression condition,
    Statement thenBranch,
    Statement elseBranch)
    implements Statement {


  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitConditionalStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() {
    return condition.sourceInfo();
  }

  @Override
  public Line line() {
    return condition.line();
  }
}
