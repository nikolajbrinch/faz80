package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentsNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record MacroCallNode(AssemblerToken name, ArgumentsNode arguments) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_CALL;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitMacroCallNode(this);
  }
}
