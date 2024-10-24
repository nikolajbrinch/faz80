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

public interface NodeProcessor<R> {

  default R process(Node cstNode) {
    return switch (cstNode) {
      case AddressReferenceExpressionNode node -> processAddressReferenceExpressionNode(node);
      case AlignmentNode node -> processAlignmentNode(node);
      case ArgumentNode node -> processArgumentNode(node);
      case ArgumentsNode node -> processArgumentsNode(node);
      case AssertionNode node -> processAssertionNode(node);
      case BasicLineNode node -> processBasicLineNode(node);
      case BinaryExpressionNode node -> processBinaryExpressionNode(node);
      case CommentNode node -> processCommentNode(node);
      case ConditionOperandNode node -> processConditionOperandNode(node);
      case ConditionalNode node -> processConditionalNode(node);
      case ConstantNode node -> processConstantNode(node);
      case DataNode node -> processDataNode(node);
      case ElseIfNode node -> processElseIfNode(node);
      case ElseNode node -> processElseNode(node);
      case EmptyNode node -> processEmptyNode(node);
      case EndNode node -> processEndNode(node);
      case EndIfNode node -> processEndIfNode(node);
      case ExpressionOperandNode node -> processExpressionOperandNode(node);
      case GlobalNode node -> processGlobalNode(node);
      case GroupingOperandNode node -> processGroupingOperandNode(node);
      case GroupingExpressionNode node -> processGroupingExpressionNode(node);
      case IdentifierExpressionNode node -> processIdentifierExpressionNode(node);
      case IfNode node -> processIfNode(node);
      case InsertNode node -> processInsertNode(node);
      case IncludeNode node -> processIncludeNode(node);
      case LabelNode node -> processLabelNode(node);
      case LinesNode node -> processLinesNode(node);
      case LiteralNumberExpressionNode node -> processLiteralNumberExpressionNode(node);
      case LiteralStringExpressionNode node -> processLiteralStringExpressionNode(node);
      case LocalEndNode node -> processLocalEndNode(node);
      case LocalNode node -> processLocalNode(node);
      case LocalStartNode node -> processLocalStartNode(node);
      case MacroCallNode node -> processMacroCallNode(node);
      case MacroEndNode node -> processMacroEndNode(node);
      case MacroNode node -> processMacroNode(node);
      case MacroStartNode node -> processMacroStartNode(node);
      case MacroSymbolNode node -> processMacroSymbolNode(node);
      case NewlineNode node -> processNewlineNode(node);
      case OpcodeNode node -> processOpcodeNode(node);
      case OriginNode node -> processOriginNode(node);
      case ParameterNode node -> processParameterNode(node);
      case ParametersNode node -> processParametersNode(node);
      case PhaseEndNode node -> processPhaseEndNode(node);
      case PhaseNode node -> processPhaseNode(node);
      case PhaseStartNode node -> processPhaseStartNode(node);
      case ProgramNode node -> processProgramNode(node);
      case RegisterOperandNode node -> processRegisterOperandNode(node);
      case RepeatEndNode node -> processRepeatEndNode(node);
      case RepeatNode node -> processRepeatNode(node);
      case RepeatStartNode node -> processRepeatStartNode(node);
      case SectionNode node -> processSectionNode(node);
      case SpaceNode node -> processSpaceNode(node);
      case TextNode node -> processTextNode(node);
      case UnaryExpressionNode node -> processUnaryExpressionNode(node);
      case VariableNode node -> processVariableNode(node);
    };
  }

  R processBinaryExpressionNode(BinaryExpressionNode node);

  R processUnaryExpressionNode(UnaryExpressionNode node);

  R processGroupingExpressionNode(GroupingExpressionNode node);

  R processLiteralNumberExpressionNode(LiteralNumberExpressionNode node);

  R processLiteralStringExpressionNode(LiteralStringExpressionNode node);

  R processIdentifierExpressionNode(IdentifierExpressionNode node);

  R processAddressReferenceExpressionNode(AddressReferenceExpressionNode node);

  R processOpcodeNode(OpcodeNode node);

  R processEndNode(EndNode node);

  R processBlockNode(BlockNode<? extends Node> node);

  R processScopeNode(ScopeNode<? extends Node> node);

  R processMacroCallNode(MacroCallNode node);

  R processRegisterOperandNode(RegisterOperandNode node);

  R processConditionOperandNode(ConditionOperandNode node);

  R processExpressionOperandNode(ExpressionOperandNode node);

  R processBasicLineNode(BasicLineNode node);

  R processProgramNode(ProgramNode node);

  R processGroupingOperandNode(GroupingOperandNode node);

  R processVariableNode(VariableNode node);

  R processConstantNode(ConstantNode node);

  R processDataNode(DataNode node);

  R processOriginNode(OriginNode node);

  R processAlignmentNode(AlignmentNode node);

  R processRepeatStartNode(RepeatStartNode node);

  R processRepeatNode(RepeatNode node);

  R processRepeatEndNode(RepeatEndNode node);

  R processGlobalNode(GlobalNode node);

  R processAssertionNode(AssertionNode node);

  R processLocalStartNode(LocalStartNode node);

  R processLocalNode(LocalNode node);

  R processLocalEndNode(LocalEndNode node);

  R processPhaseStartNode(PhaseStartNode node);

  R processPhaseNode(PhaseNode node);

  R processPhaseEndNode(PhaseEndNode node);

  R processMacroStartNode(MacroStartNode node);

  R processMacroNode(MacroNode node);

  R processMacroSymbolNode(MacroSymbolNode node);

  R processMacroEndNode(MacroEndNode node);

  R processParameterNode(ParameterNode node);

  R processArgumentsNode(ArgumentsNode node);

  R processArgumentNode(ArgumentNode node);

  R processIfNode(IfNode node);

  R processEndIfNode(EndIfNode node);

  R processConditionalNode(ConditionalNode node);

  R processElseIfNode(ElseIfNode node);

  R processElseNode(ElseNode node);

  R processInsertNode(InsertNode node);

  R processIncludeNode(IncludeNode node);

  R processCommentNode(CommentNode node);

  R processNewlineNode(NewlineNode node);

  R processLabelNode(LabelNode node);

  R processLinesNode(LinesNode node);

  R processEmptyNode(EmptyNode node);

  R processSectionNode(SectionNode node);

  R processSpaceNode(SpaceNode node);

  R processTextNode(TextNode node);

  R processParametersNode(ParametersNode node);
}
