package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record SpaceNode(AssemblerToken token, ExpressionNode count, ExpressionNode value)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.SPACE;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitSpaceNode(this);
  }
}
