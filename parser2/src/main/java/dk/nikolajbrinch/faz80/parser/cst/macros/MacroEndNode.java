package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record MacroEndNode(AssemblerToken token) implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_END;
  }

}
