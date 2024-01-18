package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.operands.Operand;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record InstructionStatement(AssemblerToken mnemonic, List<Operand> operands)
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
