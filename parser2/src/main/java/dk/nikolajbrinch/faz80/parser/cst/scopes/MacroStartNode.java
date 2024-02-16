package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record MacroStartNode(
    AssemblerToken token,
    AssemblerToken name,
    AssemblerToken extraToken,
    List<ParameterNode> parameters)
    implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_START;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitMacroStartNode(this);
  }
}
