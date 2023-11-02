package dk.nikolajbrinch.assembler.parser.expressions;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);
}
