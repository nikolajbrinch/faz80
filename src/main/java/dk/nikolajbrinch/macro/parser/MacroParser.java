package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.macro.scanner.MacroTokenType;
import dk.nikolajbrinch.macro.statements.TextStatement;
import dk.nikolajbrinch.macro.statements.IncludeStatement;
import dk.nikolajbrinch.macro.statements.MacroCallStatement;
import dk.nikolajbrinch.macro.statements.MacroStatement;
import dk.nikolajbrinch.macro.statements.Statement;
import dk.nikolajbrinch.parser.BaseParser;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.Scanner;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MacroParser extends BaseParser<Statement, MacroTokenType, MacroToken> {

  private static final Logger logger = LoggerFactory.getLogger();

  private final Set<String> macros = new HashSet<>();

  public MacroParser() {
    super(new MacroTokenProducer(), false);
  }

  protected static void reportError(MacroToken token, String message) {
    if (token.type() == MacroTokenType.EOF) {
      report("Error in line " + token.line().number() + ", " + token.start() + ": at end", message);
    } else {
      report(
          "Error in line "
              + token.line().number()
              + ", "
              + token.start()
              + ": at '"
              + token.text()
              + "'",
          message);
    }
  }

  protected static void report(String location, String message) {
    logger.error(message);
    logger.error(location);
  }

  public List<Statement> parse(File file) throws IOException {
    newFile(file);

    List<Statement> statements = new ArrayList<>();

    while (!isEof()) {
      Statement declaration = declaration();

      if (declaration != null) {
        statements.add(declaration);
      }
    }

    return statements;
  }

  private Statement declaration() {
    try {
      return switch (peek().type()) {
        case MACRO -> macro();
        case IDENTIFIER -> identifier();
        case INCLUDE -> include();
        default -> text();
      };

    } catch (ParseException e) {
      synchronize();

      return null;
    }
  }

  private Statement macro() {
    nextToken();
    skipText();
    MacroToken name = consume(MacroTokenType.IDENTIFIER, "Expect identifer after macro");

    macros.add(name.text());

    List<MacroToken> body = new ArrayList<>();

    List<Parameter> parameters = parseParams();

    List<MacroToken> lineEnd = new ArrayList<>();
    while (!match(MacroTokenType.NEWLINE)) {
      lineEnd.add(nextToken());
    }
    lineEnd.add(nextToken());

    while (true) {
      MacroToken token = nextToken();

      if (isEof()) {
        break;
      }

      if (token.type() == MacroTokenType.ENDMACRO) {
        break;
      }

      body.add(token);
    }

    return new MacroStatement(name, parameters, lineEnd, body);
  }

  private List<Parameter> parseParams() {
    List<Parameter> parameters = new ArrayList<>();

    while (!isCommentOrNewline()) {
      parameters.add(parseParam());

      while (checkType(MacroTokenType.COMMA)) {
        nextToken();
        parameters.add(parseParam());
      }
    }

    return parameters;
  }

  private Parameter parseParam() {
    skipText();
    MacroToken name = consume(MacroTokenType.IDENTIFIER, "Expect identifier for macro parameter");

    List<MacroToken> tokens = new ArrayList<>();

    skipText();
    if (checkType(MacroTokenType.EQUAL)) {
      nextToken();
      while (!match(MacroTokenType.COMMA) && !isCommentOrNewline()) {
        tokens.add(nextToken());
      }
    }

    return new Parameter(name, tokens);
  }

  private Statement identifier() {
    if (macros.contains(peek().text())) {
      return macroCall();
    }

    return new TextStatement(List.of(nextToken()));
  }

  private Statement macroCall() {
    MacroToken name = nextToken();
    List<Argument> arguments = parseArgs();

    return new MacroCallStatement(name, arguments);
  }

  private List<Argument> parseArgs() {
    List<Argument> arguments = new ArrayList<>();

    skipBlankText();

    MacroToken start = null;
    MacroTokenType end = null;
    if (match(MacroTokenType.LEFT_PAREN)) {
      start = nextToken();
      end = MacroTokenType.RIGHT_PAREN;
    }

    while (!isCommentOrNewline() && !isEof()) {
      if (end != null && match(end)) {
        break;
      }

      arguments.add(parseArg(end));

      while (checkType(MacroTokenType.COMMA)) {
        nextToken();
        arguments.add(parseArg(end));
      }
    }

    if (start != null) {
      consume(end, "Expecting " + end.name() + " at line " + start.line().number());
    }

    return arguments;
  }

  private Argument parseArg(MacroTokenType end) {
    skipBlankText();

    Argument argument = null;

    if (match(Grouping.startTypes())) {
      MacroToken localStart = nextToken();
      List<MacroToken> tokens = new ArrayList<>();

      while (!isCommentOrNewline()) {
        MacroTokenType localEnd = Grouping.findByStartType(localStart.type()).end();
        if (match(localEnd) && !(peek(2).type() == localEnd)) {
          nextToken();
          break;
        }

        tokens.add(nextToken());
      }

      argument = new Argument(tokens);
    } else {
      List<MacroToken> tokens = new ArrayList<>();
      while (!match(MacroTokenType.COMMA) && !isCommentOrNewline() && !isEof()) {
        if (end != null && match(end)) {
          break;
        }

        tokens.add(nextToken());
      }

      argument = new Argument(tokens);
    }

    return argument;
  }

  private Statement include() {
    MacroToken include = nextToken();
    List<MacroToken> space = skipBlankText();
    MacroToken filename = nextToken();

    return new IncludeStatement(include, space, filename);
  }

  private Statement text() {
    List<MacroToken> tokens = new ArrayList<>();

    while (!match(MacroTokenType.MACRO, MacroTokenType.IDENTIFIER) && !isEof()) {
      tokens.add(nextToken());
    }

    return new TextStatement(tokens);
  }

  private void synchronize() {
    while (!isEol()) {
      nextToken();
    }

    expectEol("Expect newline");
  }

  private void expectEol(String message) {
    consume(MacroTokenType.NEWLINE, message);
  }

  protected MacroToken consume(MacroTokenType type, String message) {
    if (checkType(type)) {
      return nextToken();
    }

    throw error(peek(), message);
  }

  protected RuntimeException error(MacroToken token, String message) {
    reportError(token, message);

    return new ParseException(message);
  }

  @Override
  protected boolean isType(MacroToken token, MacroTokenType type) {
    return token.type() == type;
  }

  @Override
  protected MacroTokenType getEofType() {
    return MacroTokenType.EOF;
  }

  @Override
  protected MacroTokenType getCommentType() {
    return MacroTokenType.COMMENT;
  }

  protected boolean isEol() {
    return isEof() || peek().type() == MacroTokenType.NEWLINE;
  }

  protected boolean isEol(int position) {
    return isEof(position) || peek(position).type() == MacroTokenType.NEWLINE;
  }

  private List<MacroToken> skipText() {
    List<MacroToken> text = new ArrayList<>();

    while (checkType(MacroTokenType.TEXT)) {
      text.add(nextToken());
    }

    return text;
  }

  private List<MacroToken> skipBlankText() {
    List<MacroToken> text = new ArrayList<>();

    while (checkType(MacroTokenType.TEXT) && peek().text().isBlank()) {
      text.add(nextToken());
    }
    return text;
  }

  private boolean isCommentOrNewline() {
    return match(getCommentType(), MacroTokenType.NEWLINE);
  }
}
