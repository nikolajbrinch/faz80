package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.operands.Operand;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record InstructionStatement(AssemblerToken mnemonic, Operand operand1, Operand operand2)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitInstructionStatement(this);
  }

  @Override
  public Line line() {
    return mnemonic.line();
  }
}
