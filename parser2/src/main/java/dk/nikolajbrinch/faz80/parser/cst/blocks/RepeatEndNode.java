package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record RepeatEndNode(AssemblerToken token) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.REPEAT_END;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitRepeatEndNode(this);
  }
}
