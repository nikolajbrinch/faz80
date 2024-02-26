package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroSymbolNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParametersNode;
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

public class NodePrinter implements NodeVisitor<String> {

  public String print(Node cstNode) {
    return cstNode.accept(this);
  }

  public String print(ProgramNode programNode) {
    return programNode.accept(this);
  }

  @Override
  public String visitBinaryExpressionNode(BinaryExpressionNode node) {
    return String.format(
        "%s %s %s", node.left().accept(this), node.operator().text(), node.right().accept(this));
  }

  @Override
  public String visitUnaryExpressionNode(UnaryExpressionNode node) {
    return node.operator().text() + node.expression().accept(this);
  }

  @Override
  public String visitGroupingExpressionNode(GroupingExpressionNode node) {
    return node.groupStart().text() + node.expression().accept(this) + node.groupEnd().text();
  }

  @Override
  public String visitLiteralNumberExpressionNode(LiteralNumberExpressionNode node) {
    return node.numberLiteral().text();
  }

  @Override
  public String visitLiteralStringExpressionNode(LiteralStringExpressionNode node) {
    return StringUtil.escape(node.stringLiteral().text());
  }

  @Override
  public String visitIdentifierExpressionNode(IdentifierExpressionNode node) {
    return node.identifier().text();
  }

  @Override
  public String visitAddressReferenceExpressionNode(AddressReferenceExpressionNode node) {
    return node.addressReference().text();
  }

  @Override
  public String visitOpcodeNode(OpcodeNode node) {
    return String.format(
        "%s %s",
        node.mnemonic().text(),
        node.operands().stream()
            .map(operand -> operand.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitEndNode(EndNode node) {
    return node.token().text();
  }

  @Override
  public String visitBlockNode(BlockNode node) {
    return Stream.of(node.start(), node.body(), node.end())
        .filter(Objects::nonNull)
        .map(child -> child.accept(this))
        .collect(Collectors.joining());
  }

  @Override
  public String visitScopeNode(ScopeNode node) {
    return visitBlockNode(node);
  }

  @Override
  public String visitMacroCallNode(MacroCallNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.arguments() != null) {
      builder.append(" ");
      builder.append(node.arguments().accept(this));
    }

    return builder.toString();
  }

  @Override
  public String visitRegisterOperandNode(RegisterOperandNode node) {
    StringBuilder builder = new StringBuilder(node.register().text());

    if (node.operator() != null) {
      builder.append(node.operator().text());
      builder.append(node.displacement().accept(this));
    }

    return builder.toString();
  }

  @Override
  public String visitConditionOperandNode(ConditionOperandNode node) {
    return node.condition().text();
  }

  @Override
  public String visitExpressionOperandNode(ExpressionOperandNode node) {
    return node.expression().accept(this);
  }

  @Override
  public String visitSingleLineNode(BasicLineNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.label() != null) {
      builder.append(node.label().accept(this));
      builder.append(" ");
    }

    if (node.instruction() != null) {
      builder.append(node.instruction().accept(this));
      builder.append(" ");
    }

    if (node.comment() != null) {
      builder.append(node.comment().accept(this));
      builder.append(" ");
    }

    builder.append(node.newline().accept(this));

    return builder.toString();
  }

  @Override
  public String visitProgramNode(ProgramNode node) {
    return node.lines().accept(this);
  }

  @Override
  public String visitAddressReferenceNode(AddressReferenceExpressionNode node) {
    return node.addressReference().text();
  }

  @Override
  public String visitGroupingOperandNode(GroupingOperandNode node) {
    return String.format(
        "%s%s%s", node.groupStart().text(), node.operand().accept(this), node.groupEnd().text());
  }

  @Override
  public String visitVariableNode(VariableNode node) {
    return String.format("%s %s", node.operator().text(), node.expression().accept(this));
  }

  @Override
  public String visitConstantNode(ConstantNode node) {
    return String.format("%s %s", node.operator().text(), node.expression().accept(this));
  }

  @Override
  public String visitDataNode(DataNode node) {
    return String.format(
        "%s %s",
        node.token().text(),
        node.expressions().stream()
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitOriginNode(OriginNode node) {
    return String.format(
        "%s %s",
        node.token().text(),
        Stream.of(node.location(), node.fillByte())
            .filter(Objects::nonNull)
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitAlignmentNode(AlignmentNode node) {
    return String.format(
        "%s %s",
        node.token().text(),
        Stream.of(node.alignment(), node.fillByte())
            .filter(Objects::nonNull)
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitRepeatStartNode(RepeatStartNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitRepeatNode(RepeatNode node) {
    return visitBlockNode(node);
  }

  @Override
  public String visitRepeatEndNode(RepeatEndNode node) {
    return node.token().text();
  }

  @Override
  public String visitGlobalNode(GlobalNode node) {
    return String.format("%s %s", node.token().text(), node.identifier().text());
  }

  @Override
  public String visitAssertionNode(AssertionNode node) {
    return String.format("%s %S", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitLocalStartNode(LocalStartNode node) {
    return node.token().text();
  }

  @Override
  public String visitLocalNode(LocalNode node) {
    return visitScopeNode(node);
  }

  @Override
  public String visitLocalEndNode(LocalEndNode node) {
    return node.token().text();
  }

  @Override
  public String visitPhaseStartNode(PhaseStartNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitPhaseNode(PhaseNode node) {
    return visitBlockNode(node);
  }

  @Override
  public String visitPhaseEndNode(PhaseEndNode node) {
    return node.token().text();
  }

  @Override
  public String visitMacroStartNode(MacroStartNode node) {
    StringBuilder builder = new StringBuilder(node.token().text());

    if (node.name() != null) {
      builder.append(" ");
      builder.append(node.name().text());
    }

    builder.append(" ");
    builder.append(node.parameters().accept(this));

    return builder.toString();
  }

  @Override
  public String visitMacroNode(MacroNode node) {
    return visitBlockNode(node);
  }

  @Override
  public String visitMacroSymbolNode(MacroSymbolNode node) {
    return visitBlockNode(node);
  }

  @Override
  public String visitMacroEndNode(MacroEndNode node) {
    return node.token().text();
  }

  @Override
  public String visitParameterNode(ParameterNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.defaultValue() != null) {
      builder.append("=");
      builder.append(node.defaultValue().accept(this));
    }

    return builder.toString();
  }

  @Override
  public String visitArgumentsNode(ArgumentsNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(
        node.arguments().stream()
            .map(argument -> argument.accept(this))
            .collect(Collectors.joining(", ")));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }

  @Override
  public String visitArgumentNode(ArgumentNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.boundsStart() != null) {
      builder.append(node.boundsStart().text());
    }

    if (!node.tokens().isEmpty()) {
      List<AssemblerToken> tokens = new ArrayList<>(node.tokens());
      AssemblerToken prevToken = tokens.removeFirst();
      builder.append(prevToken.text());

      for (AssemblerToken token : tokens) {
        builder.append(" ".repeat(token.start() - (prevToken.end() + 1)));
        builder.append(token.text());
        prevToken = token;
      }
    }

    if (node.boundsEnd() != null) {
      builder.append(node.boundsEnd().text());
    }

    return builder.toString();
  }

  @Override
  public String visitIfNode(IfNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitEndIfNode(EndIfNode node) {
    return node.token().text();
  }

  @Override
  public String visitConditionalNode(ConditionalNode node) {
    return Stream.of(
            node.ifLine(), node.thenLines(), node.elseLine(), node.elseLines(), node.elseIfLine())
        .filter(Objects::nonNull)
        .map(child -> child.accept(this))
        .collect(Collectors.joining());
  }

  @Override
  public String visitElseIfNode(ElseIfNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitElseNode(ElseNode node) {
    return node.token().text();
  }

  @Override
  public String visitInsertNode(InsertNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitIncludeNode(IncludeNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitCommentNode(CommentNode node) {
    return node.comment().text();
  }

  @Override
  public String visitNewlineNode(NewlineNode node) {
    return node.newline().text();
  }

  @Override
  public String visitLabelNode(LabelNode node) {
    return node.label().text();
  }

  @Override
  public String visitLinesNode(LinesNode node) {
    return node.lines().stream().map(line -> line.accept(this)).collect(Collectors.joining());
  }

  @Override
  public String visitEmptyNode(EmptyNode node) {
    return "";
  }

  @Override
  public String visitSpaceNode(SpaceNode node) {
    return String.format(
        "%s %s",
        node.token().text(),
        Stream.of(node.count(), node.value())
            .filter(Objects::nonNull)
            .map(value -> value.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitTextNode(TextNode node) {
    return node.text().text();
  }

  @Override
  public String visitParametersNode(ParametersNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(
        node.parameters().stream()
            .map(parameter -> parameter.accept(this))
            .collect(Collectors.joining(", ")));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }
}
