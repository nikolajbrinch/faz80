package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record LabelNode(AssemblerToken label) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.LABEL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitLabelNode(this);
  }
}
