package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record IncludeNode(AssemblerToken token, ExpressionNode expression) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.INCLUDE;
  }

}
