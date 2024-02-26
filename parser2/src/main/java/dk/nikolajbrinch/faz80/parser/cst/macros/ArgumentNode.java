package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record ArgumentNode(AssemblerToken boundsStart, List<AssemblerToken> tokens, AssemblerToken boundsEnd) implements Node {

  @Override
  public NodeType type() {
    return NodeType.ARGUMENT;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitArgumentNode(this);
  }

}
