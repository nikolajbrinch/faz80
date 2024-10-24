package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record PhaseEndNode(AssemblerToken token) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.PHASE_END;
  }

}
