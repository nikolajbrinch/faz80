package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatStartNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.EndIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.IfNode;
import dk.nikolajbrinch.faz80.parser.cst.data.DataNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.AddressReferenceExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.BinaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.GroupingExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.IdentifierExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralNumberExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralStringExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.UnaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AlignmentNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AssertionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.ConstantNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.EndNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.GlobalNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.IncludeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InsertNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.MacroCallNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OpcodeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OriginNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.VariableNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;

public interface NodeVisitorAdapter<R> extends NodeVisitor<R> {

  @Override
  default R visitBinaryExpressionNode(BinaryExpressionNode node) {
    return null;
  }

  @Override
  default R visitUnaryExpressionNode(UnaryExpressionNode node) {
    return null;
  }

  @Override
  default R visitGroupingExpressionNode(GroupingExpressionNode node) {
    return null;
  }

  @Override
  default R visitLiteralNumberExpressionNode(LiteralNumberExpressionNode node) {
    return null;
  }

  @Override
  default R visitLiteralStringExpressionNode(LiteralStringExpressionNode node) {
    return null;
  }

  @Override
  default R visitIdentifierExpressionNode(IdentifierExpressionNode node) {
    return null;
  }

  @Override
  default R visitAddressReferenceExpressionNode(AddressReferenceExpressionNode node) {
    return null;
  }

  @Override
  default R visitOpcodeNode(OpcodeNode node) {
    return null;
  }

  @Override
  default R visitEndNode(EndNode node) {
    return null;
  }

  @Override
  default R visitMacroCallNode(MacroCallNode node) {
    return null;
  }

  @Override
  default R visitRegisterOperandNode(RegisterOperandNode node) {
    return null;
  }

  @Override
  default R visitConditionOperandNode(ConditionOperandNode node) {
    return null;
  }

  @Override
  default R visitExpressionOperandNode(ExpressionOperandNode node) {
    return null;
  }

  @Override
  default R visitSingleLineNode(BasicLineNode node) {
    return null;
  }

  @Override
  default R visitProgramNode(ProgramNode node) {
    return null;
  }

  @Override
  default R visitAddressReferenceNode(AddressReferenceExpressionNode node) {
    return null;
  }

  @Override
  default R visitGroupingOperandNode(GroupingOperandNode node) {
    return null;
  }

  @Override
  default R visitVariableNode(VariableNode node) {
    return null;
  }

  @Override
  default R visitConstantNode(ConstantNode node) {
    return null;
  }

  @Override
  default R visitInsertNode(InsertNode node) {
    return null;
  }

  @Override
  default R visitIncludeNode(IncludeNode node) {
    return null;
  }

  @Override
  default R visitDataNode(DataNode node) {
    return null;
  }

  @Override
  default R visitOriginNode(OriginNode node) {
    return null;
  }

  @Override
  default R visitAlignmentNode(AlignmentNode node) {
    return null;
  }

  @Override
  default R visitRepeatStartNode(RepeatStartNode node) {
    return null;
  }

  @Override
  default R visitRepeatEndNode(RepeatEndNode node) {
    return null;
  }

  @Override
  default R visitGlobalNode(GlobalNode node) {
    return null;
  }

  @Override
  default R visitAssertionNode(AssertionNode node) {
    return null;
  }

  @Override
  default R visitLocalStartNode(LocalStartNode node) {
    return null;
  }

  @Override
  default R visitLocalEndNode(LocalEndNode node) {
    return null;
  }

  @Override
  default R visitPhaseStartNode(PhaseStartNode node) {
    return null;
  }

  @Override
  default R visitPhaseEndNode(PhaseEndNode node) {
    return null;
  }

  @Override
  default R visitMacroStartNode(MacroStartNode node) {
    return null;
  }

  @Override
  default R visitMacroEndNode(MacroEndNode node) {
    return null;
  }

  @Override
  default R visitParameterNode(ParameterNode node) {
    return null;
  }

  @Override
  default R visitArgumentsNode(ArgumentsNode node) {
    return null;
  }

  @Override
  default R visitArgumentNode(ArgumentNode node) {
    return null;
  }

  @Override
  default R visitIfNode(IfNode node) {
    return null;
  }

  @Override
  default R visitEndIfNode(EndIfNode node) {
    return null;
  }

  @Override
  default R visitConditionalNode(ConditionalNode node) {
    return null;
  }

  @Override
  default R visitElseIfNode(ElseIfNode node) {
    return null;
  }

  @Override
  default R visitElseNode(ElseNode node) {
    return null;
  }

  @Override
  default R visitBlockNode(BlockNode node) {
    return null;
  }

  @Override
  default R visitScopeNode(ScopeNode node) {
    return null;
  }

  @Override
  default R visitCommentNode(CommentNode node) {
    return null;
  }

  @Override
  default R visitNewlineNode(NewlineNode node) {
    return null;
  }

  @Override
  default R visitLabelNode(LabelNode node) {
    return null;
  }

  @Override
  default R visitLinesNode(LinesNode node) {
    return null;
  }

  @Override
  default R visitEmptyNode(EmptyNode node) {
    return null;
  }

  @Override
  default R visitSpaceNode(SpaceNode node) {
    return null;
  }
}
