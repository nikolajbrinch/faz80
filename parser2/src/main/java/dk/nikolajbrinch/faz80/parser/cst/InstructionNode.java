package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record InstructionNode(AssemblerToken mnemonic, List<OperandNode> operands)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.INSTRUCTION;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitInstructionNode(this);
  }
}
