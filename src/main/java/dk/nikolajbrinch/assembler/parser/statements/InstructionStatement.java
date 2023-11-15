package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.Token;

public record InstructionStatement(Token mnemonic, Expression operand1, Expression operand2)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitInstructionStatement(this);
  }
}
