package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatNode;
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
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroSymbolNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParametersNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;

public interface NodeProcessorAdapter<R> extends NodeProcessor<R> {

  @Override
  default R processBinaryExpressionNode(BinaryExpressionNode node) {
    return null;
  }

  @Override
  default R processUnaryExpressionNode(UnaryExpressionNode node) {
    return null;
  }

  @Override
  default R processGroupingExpressionNode(GroupingExpressionNode node) {
    return null;
  }

  @Override
  default R processLiteralNumberExpressionNode(LiteralNumberExpressionNode node) {
    return null;
  }

  @Override
  default R processLiteralStringExpressionNode(LiteralStringExpressionNode node) {
    return null;
  }

  @Override
  default R processIdentifierExpressionNode(IdentifierExpressionNode node) {
    return null;
  }

  @Override
  default R processAddressReferenceExpressionNode(AddressReferenceExpressionNode node) {
    return null;
  }

  @Override
  default R processOpcodeNode(OpcodeNode node) {
    return null;
  }

  @Override
  default R processEndNode(EndNode node) {
    return null;
  }

  @Override
  default R processBlockNode(BlockNode<? extends Node> node) {
    return null;
  }

  @Override
  default R processScopeNode(ScopeNode<? extends Node> node) {
    return null;
  }

  @Override
  default R processMacroCallNode(MacroCallNode node) {
    return null;
  }

  @Override
  default R processRegisterOperandNode(RegisterOperandNode node) {
    return null;
  }

  @Override
  default R processConditionOperandNode(ConditionOperandNode node) {
    return null;
  }

  @Override
  default R processExpressionOperandNode(ExpressionOperandNode node) {
    return null;
  }

  @Override
  default R processBasicLineNode(BasicLineNode node) {
    return null;
  }

  @Override
  default R processProgramNode(ProgramNode node) {
    return null;
  }

  @Override
  default R processGroupingOperandNode(GroupingOperandNode node) {
    return null;
  }

  @Override
  default R processVariableNode(VariableNode node) {
    return null;
  }

  @Override
  default R processConstantNode(ConstantNode node) {
    return null;
  }

  @Override
  default R processDataNode(DataNode node) {
    return null;
  }

  @Override
  default R processOriginNode(OriginNode node) {
    return null;
  }

  @Override
  default R processAlignmentNode(AlignmentNode node) {
    return null;
  }

  @Override
  default R processRepeatStartNode(RepeatStartNode node) {
    return null;
  }

  @Override
  default R processRepeatNode(RepeatNode node) {
    return null;
  }

  @Override
  default R processRepeatEndNode(RepeatEndNode node) {
    return null;
  }

  @Override
  default R processGlobalNode(GlobalNode node) {
    return null;
  }

  @Override
  default R processAssertionNode(AssertionNode node) {
    return null;
  }

  @Override
  default R processLocalStartNode(LocalStartNode node) {
    return null;
  }

  @Override
  default R processLocalNode(LocalNode node) {
    return null;
  }

  @Override
  default R processLocalEndNode(LocalEndNode node) {
    return null;
  }

  @Override
  default R processPhaseStartNode(PhaseStartNode node) {
    return null;
  }

  @Override
  default R processPhaseNode(PhaseNode node) {
    return null;
  }

  @Override
  default R processPhaseEndNode(PhaseEndNode node) {
    return null;
  }

  @Override
  default R processMacroStartNode(MacroStartNode node) {
    return null;
  }

  @Override
  default R processMacroNode(MacroNode node) {
    return null;
  }

  @Override
  default R processMacroSymbolNode(MacroSymbolNode node) {
    return null;
  }

  @Override
  default R processMacroEndNode(MacroEndNode node) {
    return null;
  }

  @Override
  default R processParameterNode(ParameterNode node) {
    return null;
  }

  @Override
  default R processArgumentsNode(ArgumentsNode node) {
    return null;
  }

  @Override
  default R processArgumentNode(ArgumentNode node) {
    return null;
  }

  @Override
  default R processIfNode(IfNode node) {
    return null;
  }

  @Override
  default R processEndIfNode(EndIfNode node) {
    return null;
  }

  @Override
  default R processConditionalNode(ConditionalNode node) {
    return null;
  }

  @Override
  default R processElseIfNode(ElseIfNode node) {
    return null;
  }

  @Override
  default R processElseNode(ElseNode node) {
    return null;
  }

  @Override
  default R processInsertNode(InsertNode node) {
    return null;
  }

  @Override
  default R processIncludeNode(IncludeNode node) {
    return null;
  }

  @Override
  default R processCommentNode(CommentNode node) {
    return null;
  }

  @Override
  default R processNewlineNode(NewlineNode node) {
    return null;
  }

  @Override
  default R processLabelNode(LabelNode node) {
    return null;
  }

  @Override
  default R processLinesNode(LinesNode node) {
    return null;
  }

  @Override
  default R processEmptyNode(EmptyNode node) {
    return null;
  }

  @Override
  default R processSectionNode(SectionNode node) {
    return null;
  }

  @Override
  default R processSpaceNode(SpaceNode node) {
    return null;
  }

  @Override
  default R processTextNode(TextNode node) {
    return null;
  }

  @Override
  default R processParametersNode(ParametersNode node) {
    return null;
  }
}
