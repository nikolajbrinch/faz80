package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.blocks.ArgumentsNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record MacroCallNode(AssemblerToken name, ArgumentsNode arguments) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_CALL;
  }

  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitMacroCallNode(this);
  }
}
