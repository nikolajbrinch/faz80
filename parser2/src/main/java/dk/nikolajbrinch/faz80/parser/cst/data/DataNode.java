package dk.nikolajbrinch.faz80.parser.cst.data;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record DataNode(DataType dataType, AssemblerToken token, List<ExpressionNode> expressions)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.DATA;
  }

}
