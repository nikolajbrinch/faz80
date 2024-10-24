package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BodyNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParametersNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;

public sealed interface Node
    permits ArgumentNode,
        ArgumentsNode,
        BodyNode,
        ExpressionNode,
        InstructionNode,
        LineNode,
        OperandNode,
        ParameterNode,
        ParametersNode,
        ProgramNode,
        TextNode {

  NodeType type();
}
