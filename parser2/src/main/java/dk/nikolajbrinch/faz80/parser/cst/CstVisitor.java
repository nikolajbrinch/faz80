package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.data.DataNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.AddressReferenceExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.BinaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.GroupingExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.IdentifierExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralNumberExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralStringExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.UnaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.EndIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.IfNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;

public interface CstVisitor<R> {

  R visitBinaryExpressionNode(BinaryExpressionNode node);

  R visitUnaryExpressionNode(UnaryExpressionNode node);

  R visitGroupingExpressionNode(GroupingExpressionNode node);

  R visitLiteralNumberExpressionNode(LiteralNumberExpressionNode node);

  R visitLiteralStringExpressionNode(LiteralStringExpressionNode node);

  R visitIdentifierExpressionNode(IdentifierExpressionNode node);

  R visitAddressReferenceExpressionNode(AddressReferenceExpressionNode node);

  R visitInstructionNode(InstructionNode node);

  R visitEndNode(EndNode node);

  R visitScopeNode(ScopeNode node);

  R visitMacroCallNode(MacroCallNode node);

  R visitRegisterOperandNode(RegisterOperandNode node);

  R visitConditionOperandNode(ConditionOperandNode node);

  R visitExpressionOperandNode(ExpressionOperandNode node);

  R visitLineNode(LineNode node);

  R visitProgramNode(ProgramNode node);

  R visitAddressReferenceNode(AddressReferenceExpressionNode node);

  R visitGroupingOperandNode(GroupingOperandNode node);

  R visitVariableNode(VariableNode node);

  R visitConstantNode(ConstantNode node);

  R visitInsertNode(InsertNode node);

  R visitIncludeNode(IncludeNode node);

  R visitDataNode(DataNode node);

  R visitOriginNode(OriginNode node);

  R visitAlignmentNode(AlignmentNode node);

  R visitRepeatStartNode(RepeatStartNode node);

  default R visitRepeatNode(RepeatNode node) {
    return visitScopeNode(node);
  }

  R visitRepeatEndNode(RepeatEndNode node);

  R visitGlobalNode(GlobalNode node);

  R visitAssertionNode(AssertionNode node);

  R visitLocalStartNode(LocalStartNode node);

  default R visitLocalNode(LocalNode node) {
    return visitScopeNode(node);
  }

  R visitLocalEndNode(LocalEndNode node);

  R visitPhaseStartNode(PhaseStartNode node);

  default R visitPhaseNode(PhaseNode node) {
    return visitScopeNode(node);
  }

  R visitPhaseEndNode(PhaseEndNode node);

  R visitMacroStartNode(MacroStartNode node);

  default R visitMacroNode(MacroNode node) {
    return visitScopeNode(node);
  }

  R visitMacroEndNode(MacroEndNode node);

  R visitParameterNode(ParameterNode node);

  R visitArgumentsNode(ArgumentsNode node);

  R visitArgumentNode(ArgumentNode node);

  R visitIfNode(IfNode node);

  R visitEndIfNode(EndIfNode node);

  R visitConditionalNode(ConditionalNode node);

  R visitElseIfNode(ElseIfNode node);

  R visitElseNode(ElseNode node);

  R visitNodesNode(NodesNode nodesNode);

  R visitCommentNode(CommentNode node);

  R visitNewlineNode(NewlineNode node);

  R visitLabelNode(LabelNode node);
}
