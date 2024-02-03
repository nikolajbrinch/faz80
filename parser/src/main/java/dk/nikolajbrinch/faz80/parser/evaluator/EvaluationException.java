package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;

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
