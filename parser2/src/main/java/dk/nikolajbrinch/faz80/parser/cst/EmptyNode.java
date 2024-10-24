package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;

public record EmptyNode() implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.EMPTY;
  }

}
