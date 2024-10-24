package dk.nikolajbrinch.faz80.parser.cst.instructions;

import dk.nikolajbrinch.faz80.parser.cst.CommentNode;
import dk.nikolajbrinch.faz80.parser.cst.EmptyNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.NewlineNode;
import dk.nikolajbrinch.faz80.parser.cst.Node;
import dk.nikolajbrinch.faz80.parser.cst.SectionNode;
import dk.nikolajbrinch.faz80.parser.cst.SpaceNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatStartNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.EndIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.IfNode;
import dk.nikolajbrinch.faz80.parser.cst.data.DataNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;

public sealed interface InstructionNode extends Node
    permits CommentNode, EmptyNode, LabelNode, NewlineNode, SectionNode, SpaceNode, PhaseEndNode, PhaseStartNode, RepeatEndNode,
    RepeatStartNode, ElseIfNode, ElseNode, EndIfNode, IfNode, DataNode, AlignmentNode, AssertionNode, ConstantNode, EndNode, GlobalNode,
    IncludeNode, InsertNode, MacroCallNode, OpcodeNode, OriginNode, VariableNode, MacroEndNode, MacroStartNode, LocalEndNode,
    LocalStartNode {}
