package dk.nikolajbrinch.faz80.parser.cst.scopes;

import dk.nikolajbrinch.faz80.parser.cst.CommandNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record PhaseEndNode(AssemblerToken token) implements CommandNode {

  @Override
  public NodeType type() {
    return NodeType.PHASE_END;
  }

  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitPhaseEndNode(this);
  }
}
