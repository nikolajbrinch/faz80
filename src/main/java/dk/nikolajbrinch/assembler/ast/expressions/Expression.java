package dk.nikolajbrinch.assembler.ast.expressions;

public interface Expression {

  <R> R accept(ExpressionVisitor<R> visitor);
}
