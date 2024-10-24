package dk.nikolajbrinch.faz80.formatter;

import dk.nikolajbrinch.faz80.parser.cst.Parser;
import dk.nikolajbrinch.faz80.parser.cst.ParserConfiguration;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import java.io.IOException;

public class Formatter {

  private int maxIndent = 10;

  private boolean fixedIndent = true;

  private int opcodeSize = 4;

  private int directiveSize = 6;

  private int instructionSize = 24;

  private final LabelLengthFinder labelLengthFinder = new LabelLengthFinder();

  public String format(int tabStop, String text) throws IOException {
    ProgramNode programNode = parse(text);

    int indent = fixedIndent ? maxIndent : calcIndent(programNode, tabStop);

    return new FormatProcessor(indent, opcodeSize, directiveSize, instructionSize)
        .format(programNode);
  }

  private int calcIndent(ProgramNode programNode, int tabStop) {
    int labelLength = labelLengthFinder.findLabelLength(programNode);
    return labelLength >= maxIndent ? maxIndent : labelLength / tabStop * tabStop + tabStop;
  }

  private ProgramNode parse(String text) throws IOException {
    return newParser().parse(text);
  }

  private Parser newParser() {
    return new Parser(new ParserConfiguration(false, false));
  }
}
