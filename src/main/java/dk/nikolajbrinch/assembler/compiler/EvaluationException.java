package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;

public class EvaluationException extends RuntimeException {

  private final Expression expression;

  public EvaluationException(Expression expression, String message) {
    super(message);
    this.expression = expression;
  }

  public EvaluationException(Expression expression, String message, Throwable cause) {
    super(message, cause);
    this.expression = expression;
  }

  public Expression getExpression() {
    return expression;
  }
}
