package dk.nikolajbrinch.faz80.parser.cst.conditional;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record EndIfNode(AssemblerToken token) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.ENDIF;
  }

  @Override
  public  <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitEndIfNode(this);
  }

}
