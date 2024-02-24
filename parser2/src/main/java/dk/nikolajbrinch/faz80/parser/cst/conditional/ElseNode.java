package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ElseNode(AssemblerToken token) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.ELSE;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitElseNode(this);
  }
}
