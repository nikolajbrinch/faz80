package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
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
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodePrinter implements NodeProcessor<String> {

  public String print(Node node) {
    return process(node);
  }

  public String processBinaryExpressionNode(BinaryExpressionNode node) {
    return "%s %s %s"
        .formatted(process(node.left()), node.operator().text(), process(node.right()));
  }

  public String processUnaryExpressionNode(UnaryExpressionNode node) {
    return node.operator().text() + process(node.expression());
  }

  public String processGroupingExpressionNode(GroupingExpressionNode node) {
    return node.groupStart().text() + process(node.expression()) + node.groupEnd().text();
  }

  public String processLiteralNumberExpressionNode(LiteralNumberExpressionNode node) {
    return node.numberLiteral().text();
  }

  public String processLiteralStringExpressionNode(LiteralStringExpressionNode node) {
    return StringUtil.escape(node.stringLiteral().text());
  }

  public String processIdentifierExpressionNode(IdentifierExpressionNode node) {
    return node.identifier().text();
  }

  public String processAddressReferenceExpressionNode(AddressReferenceExpressionNode node) {
    return node.addressReference().text();
  }

  public String processOpcodeNode(OpcodeNode node) {
    return "%s %s"
        .formatted(node.mnemonic().text(), sepraratedValue(node.operands().stream(), ", "));
  }

  public String processEndNode(EndNode node) {
    return node.token().text();
  }

  public String processBlockNode(BlockNode<? extends Node> node) {
    return Stream.of(node.start(), node.body(), node.end())
        .filter(Objects::nonNull)
        .map(this::process)
        .collect(Collectors.joining());
  }

  public String processScopeNode(ScopeNode<? extends Node> node) {
    return processBlockNode(node);
  }

  public String processMacroCallNode(MacroCallNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.arguments() != null) {
      builder.append(" ");
      builder.append(process(node.arguments()));
    }

    return builder.toString();
  }

  public String processRegisterOperandNode(RegisterOperandNode node) {
    StringBuilder builder = new StringBuilder(node.register().text());

    if (node.operator() != null) {
      builder.append(node.operator().text());
      builder.append(process(node.displacement()));
    }

    return builder.toString();
  }

  public String processConditionOperandNode(ConditionOperandNode node) {
    return node.condition().text();
  }

  public String processExpressionOperandNode(ExpressionOperandNode node) {
    return process(node.expression());
  }

  public String processBasicLineNode(BasicLineNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.label() != null) {
      builder.append(process(node.label()));
      builder.append(" ");
    }

    if (node.instruction() != null) {
      builder.append(process(node.instruction()));
      builder.append(" ");
    }

    if (node.comment() != null) {
      builder.append(process(node.comment()));
      builder.append(" ");
    }

    builder.append(process(node.newline()));

    return builder.toString();
  }

  public String processProgramNode(ProgramNode node) {
    return process(node.lines());
  }

  public String processGroupingOperandNode(GroupingOperandNode node) {
    return "%s%s%s"
        .formatted(node.groupStart().text(), process(node.operand()), node.groupEnd().text());
  }

  public String processVariableNode(VariableNode node) {
    return "%s %s".formatted(node.operator().text(), process(node.expression()));
  }

  public String processConstantNode(ConstantNode node) {
    return "%s %s".formatted(node.operator().text(), process(node.expression()));
  }

  public String processDataNode(DataNode node) {
    return "%s %s"
        .formatted(node.token().text(), sepraratedValue(node.expressions().stream(), ", "));
  }

  public String processOriginNode(OriginNode node) {
    return "%s %s"
        .formatted(
            node.token().text(),
            sepraratedValue(Stream.of(node.location(), node.fillByte()), ", "));
  }

  public String processAlignmentNode(AlignmentNode node) {
    return "%s %s"
        .formatted(
            node.token().text(),
            sepraratedValue(Stream.of(node.alignment(), node.fillByte()), ", "));
  }

  public String processRepeatStartNode(RepeatStartNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processRepeatNode(RepeatNode node) {
    return processBlockNode(node);
  }

  public String processRepeatEndNode(RepeatEndNode node) {
    return node.token().text();
  }

  public String processGlobalNode(GlobalNode node) {
    return "%s %s".formatted(node.token().text(), node.identifier().text());
  }

  public String processAssertionNode(AssertionNode node) {
    return "%s %S".formatted(node.token().text(), process(node.expression()));
  }

  public String processLocalStartNode(LocalStartNode node) {
    return node.token().text();
  }

  public String processLocalNode(LocalNode node) {
    return processScopeNode(node);
  }

  public String processLocalEndNode(LocalEndNode node) {
    return node.token().text();
  }

  public String processPhaseStartNode(PhaseStartNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processPhaseNode(PhaseNode node) {
    return processBlockNode(node);
  }

  public String processPhaseEndNode(PhaseEndNode node) {
    return node.token().text();
  }

  public String processMacroStartNode(MacroStartNode node) {
    StringBuilder builder = new StringBuilder(node.token().text());

    if (node.name() != null) {
      builder.append(" ");
      builder.append(node.name().text());
    }

    builder.append(" ");
    builder.append(process(node.parameters()));

    return builder.toString();
  }

  public String processMacroNode(MacroNode node) {
    return processBlockNode(node);
  }

  public String processMacroSymbolNode(MacroSymbolNode node) {
    return processBlockNode(node);
  }

  public String processMacroEndNode(MacroEndNode node) {
    return node.token().text();
  }

  public String processParameterNode(ParameterNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.defaultValue() != null) {
      builder.append("=");
      builder.append(process(node.defaultValue()));
    }

    return builder.toString();
  }

  public String processArgumentsNode(ArgumentsNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(sepraratedValue(node.arguments().stream(), ", "));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }

  public String processArgumentNode(ArgumentNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.boundsStart() != null) {
      builder.append(node.boundsStart().text());
    }

    if (!node.tokens().isEmpty()) {
      List<AssemblerToken> tokens = new ArrayList<>(node.tokens());
      AssemblerToken prevToken = tokens.removeFirst();
      builder.append(prevToken.text());

      for (AssemblerToken token : tokens) {
        builder.append(" ".repeat(token.startColumn() - (prevToken.endColumn() + 1)));
        builder.append(token.text());
        prevToken = token;
      }
    }

    if (node.boundsEnd() != null) {
      builder.append(node.boundsEnd().text());
    }

    return builder.toString();
  }

  public String processIfNode(IfNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processEndIfNode(EndIfNode node) {
    return node.token().text();
  }

  public String processConditionalNode(ConditionalNode node) {
    return values(
        Stream.of(
            node.ifLine(), node.thenLines(), node.elseLine(), node.elseLines(), node.elseIfLine()));
  }

  public String processElseIfNode(ElseIfNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processElseNode(ElseNode node) {
    return node.token().text();
  }

  public String processInsertNode(InsertNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processIncludeNode(IncludeNode node) {
    return "%s %s".formatted(node.token().text(), process(node.expression()));
  }

  public String processCommentNode(CommentNode node) {
    return node.comment().text();
  }

  public String processNewlineNode(NewlineNode node) {
    return node.newline().text();
  }

  public String processLabelNode(LabelNode node) {
    return node.label().text();
  }

  public String processLinesNode(LinesNode node) {
    return values(node.lines().stream());
  }

  public String processEmptyNode(EmptyNode node) {
    return "";
  }

  @Override
  public String processSectionNode(SectionNode node) {
    return "%s %s".formatted(node.token().text(), node.name().text());
  }

  public String processSpaceNode(SpaceNode node) {
    return "%s %s"
        .formatted(
            node.token().text(), sepraratedValue(Stream.of(node.count(), node.value()), ", "));
  }

  public String processTextNode(TextNode node) {
    return node.text().text();
  }

  public String processParametersNode(ParametersNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(sepraratedValue(node.parameters().stream(), ", "));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }

  private <T extends Node> String sepraratedValue(Stream<T> stream, String separator) {
    return stream
        .filter(Objects::nonNull)
        .map(this::process)
        .collect(Collectors.joining(separator));
  }

  private <T extends Node> String values(Stream<T> stream) {
    return sepraratedValue(stream, "");
  }
}
