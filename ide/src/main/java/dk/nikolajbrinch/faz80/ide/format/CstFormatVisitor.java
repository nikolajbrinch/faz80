package dk.nikolajbrinch.faz80.ide.format;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AlignmentNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AssertionNode;
import dk.nikolajbrinch.faz80.parser.cst.CommentNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.ConstantNode;
import dk.nikolajbrinch.faz80.parser.cst.EmptyNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.EndNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.GlobalNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.IncludeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InsertNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.MacroCallNode;
import dk.nikolajbrinch.faz80.parser.cst.NewlineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OpcodeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OriginNode;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.SpaceNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.VariableNode;
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
import dk.nikolajbrinch.faz80.parser.cst.blocks.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ScopeNode;
import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CstFormatVisitor implements NodeVisitor<String> {
  private final String opcodeFormat;
  private final String directiveFormat;
  private final String lineFormat;
  private AssignStatement lastLabel = null;

  public CstFormatVisitor(int indentSize, int opcodeSize, int directiveSize, int instructionSize) {
    this.opcodeFormat = "%-" + opcodeSize + "s";
    this.directiveFormat = "%-" + directiveSize + "s";
    this.lineFormat = "%-" + indentSize + "s" + "%-" + instructionSize + "s%s%s";
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
  public String visitOpcodeNode(OpcodeNode node) {
    String string =
        String.format(
            "%s %s",
            opcode(node.mnemonic()),
            node.operands().stream()
                .map(operand -> operand.accept(this))
                .collect(Collectors.joining(", ")));

    return string;
  }

  @Override
  public String visitEndNode(EndNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitBlockNode(BlockNode node) {
    return node.body().accept(this);
  }

  @Override
  public String visitScopeNode(ScopeNode node) {
    return node.body().accept(this);
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
    //    StringBuilder builder = new StringBuilder();
    //
    //    if (node.label() != null) {
    //      String labelText = node.label().accept(this);
    //
    //      builder.append(labelText);
    //
    //      if (labelText.length() >= indentSize) {
    //        builder.append(node.newline().accept(this));
    //        builder.append(indent);
    //      } else {
    //        builder.append(indent.substring(labelText.length()));
    //      }
    //    } else {
    //      if (!(node.comment() != null
    //          && node.instruction().type() == NodeType.EMPTY
    //          && node.comment().comment().start() == 1)) {
    //        builder.append(indent);
    //      }
    //    }
    //
    //    if (node.instruction() != null) {
    //      builder.append(node.instruction().accept(this));
    //    }
    //
    //    if (node.comment() != null) {
    //      builder.append(" ");
    //      builder.append(node.comment().accept(this));
    //      builder.append(" ");
    //    }
    //
    //    builder.append(node.newline().accept(this));

    String labelText = node.label() == null ? "" : node.label().accept(this);
    ;
    String instructionText = node.instruction() == null ? "" : node.instruction().accept(this);
    String commentText = node.comment() == null ? "" : node.comment().accept(this);
    String newlineText = node.newline() == null ? "" : node.newline().accept(this);

    if (node.comment() != null && node.comment().comment().start() == 1) {
      return String.format("%s%s", commentText, newlineText);
    }

    return String.format(lineFormat, labelText, instructionText, commentText, newlineText);

    //    return builder.toString();
  }

  @Override
  public String visitProgramNode(ProgramNode node) {
    return node.node().accept(this);
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
    return String.format("%s %s", opcode(node.operator()), node.expression().accept(this));
  }

  @Override
  public String visitConstantNode(ConstantNode node) {
    return String.format("%s %s", opcode(node.operator()), node.expression().accept(this));
  }

  @Override
  public String visitDataNode(DataNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        node.expressions().stream()
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitOriginNode(OriginNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        Stream.of(node.location(), node.fillByte())
            .filter(Objects::nonNull)
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitAlignmentNode(AlignmentNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        Stream.of(node.alignment(), node.fillByte())
            .filter(Objects::nonNull)
            .map(expression -> expression.accept(this))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitRepeatStartNode(RepeatStartNode node) {
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitRepeatNode(RepeatNode node) {
    return node.startLine().accept(this) + node.body().accept(this) + node.endLine().accept(this);
  }

  @Override
  public String visitRepeatEndNode(RepeatEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitGlobalNode(GlobalNode node) {
    return String.format("%s %s", opcode(node.token()), node.identifier().text());
  }

  @Override
  public String visitAssertionNode(AssertionNode node) {
    return String.format("%s %S", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitLocalEndNode(LocalEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitLocalStartNode(LocalStartNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitLocalNode(LocalNode node) {
    return node.startLine().accept(this) + node.body().accept(this) + node.endLine().accept(this);
  }

  @Override
  public String visitPhaseStartNode(PhaseStartNode node) {
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitPhaseNode(PhaseNode node) {
    return node.startLine().accept(this) + node.body().accept(this) + node.endLine().accept(this);
  }

  @Override
  public String visitPhaseEndNode(PhaseEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitMacroStartNode(MacroStartNode node) {
    StringBuilder builder = new StringBuilder(opcode(node.token()));

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
    return node.startLine().accept(this) + node.body().accept(this) + node.endLine().accept(this);
  }

  @Override
  public String visitMacroEndNode(MacroEndNode node) {
    return opcode(node.token());
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
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitEndIfNode(EndIfNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitConditionalNode(ConditionalNode node) {
    StringBuilder builder = new StringBuilder();

    builder.append(node.ifLine().accept(this));
    builder.append(node.thenLines().accept(this));

    if (node.elseLine() != null) {
      builder.append(node.elseLine().accept(this));
    }

    if (node.elseLines() != null) {
      builder.append(node.elseLines().accept(this));
    }

    if (node.elseIfLine() != null) {
      builder.append(node.elseIfLine().accept(this));
    }

    return builder.toString();
  }

  @Override
  public String visitElseIfNode(ElseIfNode node) {
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitElseNode(ElseNode node) {
    return opcode(node.token());
  }

  @Override
  public String visitInsertNode(InsertNode node) {
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
  }

  @Override
  public String visitIncludeNode(IncludeNode node) {
    return String.format("%s %s", opcode(node.token()), node.expression().accept(this));
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
        opcode(node.token()),
        Stream.of(node.count(), node.value())
            .filter(Objects::nonNull)
            .map(value -> value.accept(this))
            .collect(Collectors.joining(", ")));
  }

  private String opcode(AssemblerToken instruction) {
    return String.format(opcodeFormat, instruction.text());
  }

  private String directive(AssemblerToken instruction) {
    return String.format(directiveFormat, instruction.text());
  }
}
