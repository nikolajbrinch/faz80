package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.List;

public record MacroStartNode(
    AssemblerToken token,
    AssemblerToken name,
    AssemblerToken extraToken,
    List<ParameterNode> parameters)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_START;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitMacroStartNode(this);
  }
}
