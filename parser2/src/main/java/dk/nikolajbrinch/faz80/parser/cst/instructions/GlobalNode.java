package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record GlobalNode(AssemblerToken token, AssemblerToken identifier)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.GLOBAL;
  }

}
