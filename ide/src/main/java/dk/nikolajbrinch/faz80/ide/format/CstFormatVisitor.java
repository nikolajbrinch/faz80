package dk.nikolajbrinch.faz80.ide.format;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.cst.AlignmentNode;
import dk.nikolajbrinch.faz80.parser.cst.AssertionNode;
import dk.nikolajbrinch.faz80.parser.cst.CommentNode;
import dk.nikolajbrinch.faz80.parser.cst.ConstantNode;
import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.parser.cst.EndNode;
import dk.nikolajbrinch.faz80.parser.cst.GlobalNode;
import dk.nikolajbrinch.faz80.parser.cst.IncludeNode;
import dk.nikolajbrinch.faz80.parser.cst.InsertNode;
import dk.nikolajbrinch.faz80.parser.cst.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.MacroCallNode;
import dk.nikolajbrinch.faz80.parser.cst.NewlineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodesNode;
import dk.nikolajbrinch.faz80.parser.cst.OriginNode;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.VariableNode;
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
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentsNode;
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
import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CstFormatVisitor implements CstVisitor<String> {
  private final int indentSize;
  private final String indent;

  private AssignStatement lastLabel = null;

  public CstFormatVisitor(int indentSize) {
    this.indentSize = indentSize;
    this.indent = " ".repeat(indentSize);
  }

  public String format(ProgramNode programNode) {
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
  public String visitInstructionNode(InstructionNode node) {
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
  public String visitScopeNode(ScopeNode node) {
    return node.nodes().accept(this);
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
  public String visitLineNode(LineNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.label() != null) {
      String labelText = node.label().accept(this);

      builder.append(labelText);

      if (labelText.length() >= indentSize) {
        builder.append(node.newline().accept(this));
        builder.append(indent);
      } else {
        builder.append(indent.substring(labelText.length()));
      }
    } else {
      if (!(node.comment() != null
          && node.command() == null
          && node.comment().comment().start() == 1)) {
        builder.append(indent);
      }
    }

    if (node.command() != null) {
      builder.append(node.command().accept(this));
    }

    if (node.comment() != null) {
      builder.append(" ");
      builder.append(node.comment().accept(this));
      builder.append(" ");
    }

    builder.append(node.newline().accept(this));

    return builder.toString();
  }

  @Override
  public String visitProgramNode(ProgramNode node) {
    return node.nodes().accept(this);
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
    return node.startDirective().accept(this)
        + node.nodes().accept(this)
        + node.endDirective().accept(this);
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
  public String visitLocalEndNode(LocalEndNode node) {
    return node.token().text();
  }

  @Override
  public String visitLocalStartNode(LocalStartNode node) {
    return node.token().text();
  }

  @Override
  public String visitLocalNode(LocalNode node) {
    return node.startDirective().accept(this)
        + node.nodes().accept(this)
        + node.endDirective().accept(this);
  }

  @Override
  public String visitPhaseStartNode(PhaseStartNode node) {
    return String.format("%s %s", node.token().text(), node.expression().accept(this));
  }

  @Override
  public String visitPhaseNode(PhaseNode node) {
    return node.startDirective().accept(this)
        + node.nodes().accept(this)
        + node.endDirective().accept(this);
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
    builder.append(
        node.parameters().stream()
            .map(parameter -> parameter.accept(this))
            .collect(Collectors.joining(", ")));

    return builder.toString();
  }

  @Override
  public String visitMacroNode(MacroNode node) {
    return node.startDirective().accept(this)
        + node.nodes().accept(this)
        + node.endDirective().accept(this);
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
    StringBuilder builder = new StringBuilder();

    builder.append(node.ifDirective().accept(this));
    builder.append(node.thenBranch().accept(this));

    if (node.elseDirective() != null) {
      builder.append(node.elseDirective().accept(this));
    }

    if (node.elseBranch() != null) {
      builder.append(node.elseBranch().accept(this));
    }

    if (node.endIfDirective() != null) {
      builder.append(node.endIfDirective().accept(this));
    }

    return builder.toString();
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
  public String visitNodesNode(NodesNode node) {
    return node.map(line -> line.accept(this)).collect(Collectors.joining());
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
}
