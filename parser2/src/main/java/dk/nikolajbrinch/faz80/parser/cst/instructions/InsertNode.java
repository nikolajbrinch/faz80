package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record InsertNode(AssemblerToken token, ExpressionNode expression) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.INSERT;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitInsertNode(this);
  }
}
