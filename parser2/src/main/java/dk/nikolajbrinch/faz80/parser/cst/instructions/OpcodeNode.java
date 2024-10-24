package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record OpcodeNode(AssemblerToken mnemonic, List<OperandNode> operands)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.OPCODE;
  }

}
