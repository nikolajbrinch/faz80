package dk.nikolajbrinch.faz80.parser.cst.data;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record DataNode(DataType dataType, AssemblerToken token, List<ExpressionNode> expressions)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.DATA;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitDataNode(this);
  }
}
