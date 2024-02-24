package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record RepeatStartNode(AssemblerToken token, ExpressionNode expression)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.REPEAT_START;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitRepeatStartNode(this);
  }
}
