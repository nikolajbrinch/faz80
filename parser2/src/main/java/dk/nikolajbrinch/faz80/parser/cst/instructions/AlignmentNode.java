package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record AlignmentNode(AssemblerToken token, ExpressionNode alignment, ExpressionNode fillByte)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.ALIGNMENT;
  }

}
