package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record RepeatStartNode(AssemblerToken token, ExpressionNode expression)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.REPEAT_START;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitRepeatStartNode(this);
  }
}
