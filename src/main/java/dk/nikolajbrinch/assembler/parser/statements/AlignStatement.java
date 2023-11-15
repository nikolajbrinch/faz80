package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;

public record AlignStatement(Expression alignment, Expression fillByte) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitAlignStatement(this);
  }
}
