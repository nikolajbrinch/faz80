package dk.nikolajbrinch.faz80.parser.cst.macros;

import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record MacroStartNode(
    AssemblerToken token, AssemblerToken name, AssemblerToken extraToken, ParametersNode parameters)
    implements InstructionNode {

  @Override
  public NodeType type() {
    return NodeType.MACRO_START;
  }

}
