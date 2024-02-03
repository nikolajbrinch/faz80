package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.util.List;

public record InstructionStatement(AssemblerToken mnemonic, List<Operand> operands)
    implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitInstructionStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() { return mnemonic.sourceInfo(); }

  @Override
  public Line line() {
    return mnemonic.line();
  }
}
