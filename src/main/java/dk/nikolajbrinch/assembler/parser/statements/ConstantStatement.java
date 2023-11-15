package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.Token;

public record ConstantStatement(Token identifier, Expression value) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitConstantStatement(this);
  }
}
