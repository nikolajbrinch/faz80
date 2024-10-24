package dk.nikolajbrinch.faz80.formatter;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.cst.BasicLineNode;
import dk.nikolajbrinch.faz80.parser.cst.CommentNode;
import dk.nikolajbrinch.faz80.parser.cst.EmptyNode;
import dk.nikolajbrinch.faz80.parser.cst.LabelNode;
import dk.nikolajbrinch.faz80.parser.cst.LinesNode;
import dk.nikolajbrinch.faz80.parser.cst.NewlineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeProcessor;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import dk.nikolajbrinch.faz80.parser.cst.SectionNode;
import dk.nikolajbrinch.faz80.parser.cst.SpaceNode;
import dk.nikolajbrinch.faz80.parser.cst.TextNode;
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

public class FormatProcessor implements NodeProcessor<String> {

  private final String opcodeFormat;
  private final String directiveFormat;
  private final String lineFormat2;
  private final String lineFormat3;
  private final String lineFormat4;

  public FormatProcessor(int indentSize, int opcodeSize, int directiveSize, int instructionSize) {
    this.opcodeFormat = "%-" + opcodeSize + "s";
    this.directiveFormat = "%-" + directiveSize + "s";
    this.lineFormat2 = "%s%s";
    this.lineFormat3 = "%-" + indentSize + "s" + "%-" + instructionSize + "s%s";
    this.lineFormat4 = "%-" + indentSize + "s" + "%-" + instructionSize + "s%s%s";
  }

  public String format(ProgramNode programNode) {
    return process(programNode);
  }

  @Override
  public String processBinaryExpressionNode(BinaryExpressionNode node) {
    return String.format(
        "%s %s %s", process(node.left()), node.operator().text(), process(node.right()));
  }

  @Override
  public String processUnaryExpressionNode(UnaryExpressionNode node) {
    return node.operator().text() + process(node.expression());
  }

  @Override
  public String processGroupingExpressionNode(GroupingExpressionNode node) {
    return node.groupStart().text() + process(node.expression()) + node.groupEnd().text();
  }

  @Override
  public String processLiteralNumberExpressionNode(LiteralNumberExpressionNode node) {
    return node.numberLiteral().text();
  }

  @Override
  public String processLiteralStringExpressionNode(LiteralStringExpressionNode node) {
    return StringUtil.escape(node.stringLiteral().text());
  }

  @Override
  public String processIdentifierExpressionNode(IdentifierExpressionNode node) {
    return node.identifier().text();
  }

  @Override
  public String processAddressReferenceExpressionNode(AddressReferenceExpressionNode node) {
    return node.addressReference().text();
  }

  @Override
  public String processOpcodeNode(OpcodeNode node) {
    String string =
        String.format(
            "%s %s",
            opcode(node.mnemonic()),
            node.operands().stream()
                .map(operand -> process(operand))
                .collect(Collectors.joining(", ")));

    return string;
  }

  @Override
  public String processEndNode(EndNode node) {
    return opcode(node.token());
  }

  @Override
  public String processBlockNode(BlockNode node) {
    return process(node.body());
  }

  @Override
  public String processScopeNode(ScopeNode node) {
    return process(node.body());
  }

  @Override
  public String processMacroCallNode(MacroCallNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.arguments() != null) {
      builder.append(" ");
      builder.append(process(node.arguments()));
    }

    return builder.toString();
  }

  @Override
  public String processRegisterOperandNode(RegisterOperandNode node) {
    StringBuilder builder = new StringBuilder(node.register().text());

    if (node.operator() != null) {
      builder.append(node.operator().text());
      builder.append(process(node.displacement()));
    }

    return builder.toString();
  }

  @Override
  public String processConditionOperandNode(ConditionOperandNode node) {
    return node.condition().text();
  }

  @Override
  public String processExpressionOperandNode(ExpressionOperandNode node) {
    return process(node.expression());
  }

  @Override
  public String processBasicLineNode(BasicLineNode node) {
    String labelText = node.label() == null ? "" : process(node.label());

    String instructionText = node.instruction() == null ? "" : process(node.instruction());
    String commentText = node.comment() == null ? "" : process(node.comment());
    String newlineText = node.newline() == null ? "" : process(node.newline());

    if (node.comment() != null) {
      if (node.comment().comment().startColumn() == 1) {
        return String.format(lineFormat2, commentText, newlineText);
      } else if (node.comment().comment().startColumn() > 1 && instructionText.isEmpty()) {
        return String.format(lineFormat3, labelText, commentText, newlineText);
      }
    }

    return String.format(lineFormat4, labelText, instructionText, commentText, newlineText);
  }

  @Override
  public String processProgramNode(ProgramNode node) {
    return process(node.lines());
  }

  @Override
  public String processGroupingOperandNode(GroupingOperandNode node) {
    return String.format(
        "%s%s%s", node.groupStart().text(), process(node.operand()), node.groupEnd().text());
  }

  @Override
  public String processVariableNode(VariableNode node) {
    return String.format("%s %s", opcode(node.operator()), process(node.expression()));
  }

  @Override
  public String processConstantNode(ConstantNode node) {
    return String.format("%s %s", opcode(node.operator()), process(node.expression()));
  }

  @Override
  public String processDataNode(DataNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        node.expressions().stream().map(this::process).collect(Collectors.joining(", ")));
  }

  @Override
  public String processOriginNode(OriginNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        Stream.of(node.location(), node.fillByte())
            .filter(Objects::nonNull)
            .map(this::process)
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String processAlignmentNode(AlignmentNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        Stream.of(node.alignment(), node.fillByte())
            .filter(Objects::nonNull)
            .map(this::process)
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String processRepeatStartNode(RepeatStartNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processRepeatNode(RepeatNode node) {
    return process(node.start()) + process(node.body()) + process(node.end());
  }

  @Override
  public String processRepeatEndNode(RepeatEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String processGlobalNode(GlobalNode node) {
    return String.format("%s %s", opcode(node.token()), node.identifier().text());
  }

  @Override
  public String processAssertionNode(AssertionNode node) {
    return String.format("%s %S", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processLocalEndNode(LocalEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String processLocalStartNode(LocalStartNode node) {
    return opcode(node.token());
  }

  @Override
  public String processLocalNode(LocalNode node) {
    return process(node.start()) + process(node.body()) + process(node.end());
  }

  @Override
  public String processPhaseStartNode(PhaseStartNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processPhaseNode(PhaseNode node) {
    return process(node.start()) + process(node.body()) + process(node.end());
  }

  @Override
  public String processPhaseEndNode(PhaseEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String processMacroStartNode(MacroStartNode node) {
    StringBuilder builder = new StringBuilder(opcode(node.token()));

    if (node.name() != null) {
      builder.append(" ");
      builder.append(node.name().text());
    }

    builder.append(" ");
    builder.append(process(node.parameters()));

    return builder.toString();
  }

  @Override
  public String processMacroNode(MacroNode node) {
    return process(node.start()) + process(node.body()) + process(node.end());
  }

  @Override
  public String processMacroEndNode(MacroEndNode node) {
    return opcode(node.token());
  }

  @Override
  public String processParameterNode(ParameterNode node) {
    StringBuilder builder = new StringBuilder(node.name().text());

    if (node.defaultValue() != null) {
      builder.append("=");
      builder.append(process(node.defaultValue()));
    }

    return builder.toString();
  }

  @Override
  public String processArgumentsNode(ArgumentsNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(node.arguments().stream().map(this::process).collect(Collectors.joining(", ")));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }

  @Override
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

  @Override
  public String processIfNode(IfNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processEndIfNode(EndIfNode node) {
    return opcode(node.token());
  }

  @Override
  public String processConditionalNode(ConditionalNode node) {
    StringBuilder builder = new StringBuilder();

    builder.append(process(node.ifLine()));
    builder.append(process(node.thenLines()));

    if (node.elseLine() != null) {
      builder.append(process(node.elseLine()));
    }

    if (node.elseLines() != null) {
      builder.append(process(node.elseLines()));
    }

    if (node.elseIfLine() != null) {
      builder.append(process(node.elseIfLine()));
    }

    return builder.toString();
  }

  @Override
  public String processElseIfNode(ElseIfNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processElseNode(ElseNode node) {
    return opcode(node.token());
  }

  @Override
  public String processInsertNode(InsertNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processIncludeNode(IncludeNode node) {
    return String.format("%s %s", opcode(node.token()), process(node.expression()));
  }

  @Override
  public String processCommentNode(CommentNode node) {
    return node.comment().text();
  }

  @Override
  public String processNewlineNode(NewlineNode node) {
    return node.newline().text();
  }

  @Override
  public String processLabelNode(LabelNode node) {
    return node.label().text();
  }

  @Override
  public String processLinesNode(LinesNode node) {
    return node.lines().stream().map(this::process).collect(Collectors.joining());
  }

  @Override
  public String processEmptyNode(EmptyNode node) {
    return "";
  }

  @Override
  public String processSectionNode(SectionNode node) {
    return String.format("%s %s", node.token().text(), node.name().text());
  }

  @Override
  public String processSpaceNode(SpaceNode node) {
    return String.format(
        "%s %s",
        opcode(node.token()),
        Stream.of(node.count(), node.value())
            .filter(Objects::nonNull)
            .map(this::process)
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String processTextNode(TextNode node) {
    return node.text().text();
  }

  @Override
  public String processParametersNode(ParametersNode node) {
    StringBuilder builder = new StringBuilder();

    if (node.groupStart() != null) {
      builder.append(node.groupStart().text());
    }

    builder.append(node.parameters().stream().map(this::process).collect(Collectors.joining(", ")));

    if (node.groupEnd() != null) {
      builder.append(node.groupEnd().text());
    }

    return builder.toString();
  }

  @Override
  public String processMacroSymbolNode(MacroSymbolNode node) {
    return process(node.start()) + process(node.body()) + process(node.end());
  }

  private String opcode(AssemblerToken instruction) {
    return String.format(opcodeFormat, instruction.text());
  }

  private String directive(AssemblerToken instruction) {
    return String.format(directiveFormat, instruction.text());
  }
}
