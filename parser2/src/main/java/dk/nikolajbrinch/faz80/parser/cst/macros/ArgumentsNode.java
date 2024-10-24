package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record ArgumentsNode(
    AssemblerToken groupStart, List<ArgumentNode> arguments, AssemblerToken groupEnd)
    implements Node {

  @Override
  public NodeType type() {
    return NodeType.ARGUMENTS;
  }

}
