package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record InstructionStatement(
    AssemblerToken mnemonic, Expression operand1, Expression operand2) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitInstructionStatement(this);
  }
}
