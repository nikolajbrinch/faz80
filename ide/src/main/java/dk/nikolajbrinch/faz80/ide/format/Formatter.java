package dk.nikolajbrinch.faz80.ide.format;

import dk.nikolajbrinch.faz80.parser.cst.CstParser;
import dk.nikolajbrinch.faz80.parser.cst.CstParserConfiguration;
import dk.nikolajbrinch.faz80.parser.cst.ProgramNode;
import java.io.IOException;

public class Formatter {

  private final CstLabelLengthVisitor labelLengthVisitor = new CstLabelLengthVisitor();

  public String format(int tabStop, String text) throws IOException {
    ProgramNode programNode = parse(text);

    int maxIndent = 10;
    int labelLength = labelLengthVisitor.visitProgramNode(programNode);
    int indent = labelLength >= maxIndent ? maxIndent : labelLength / tabStop * tabStop + tabStop;

    return new CstFormatVisitor(indent).format(programNode);
  }

  private ProgramNode parse(String text) throws IOException {
    return newParser().parse(text);
  }

  private CstParser newParser() {
    return new CstParser(new CstParserConfiguration(false));
  }
}
