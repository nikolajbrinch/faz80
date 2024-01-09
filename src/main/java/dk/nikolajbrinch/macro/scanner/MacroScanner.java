package dk.nikolajbrinch.macro.scanner;

import dk.nikolajbrinch.parser.BaseScanner;
import dk.nikolajbrinch.parser.Char;
import dk.nikolajbrinch.parser.Line;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MacroScanner extends BaseScanner<MacroTokenType, MacroToken> {

  private List<MacroToken> tokenStack = new ArrayList<>();

  private List<Char> textChars = new ArrayList<>();

  public MacroScanner(InputStream inputStream) {
    super(inputStream);
  }

  @Override
  protected MacroToken createEofToken(Line line, int position) {
    return new MacroToken(MacroTokenType.EOF, line, position, position, "");
  }

  @Override
  protected MacroToken createToken() throws IOException {
    if (!tokenStack.isEmpty()) {
      return tokenStack.removeFirst();
    }

    Char ch = nextChar();

    MacroToken token = null;

    while (token == null) {
      textChars.add(ch);

      token =
          switch (ch.character()) {
            case '\n' -> createCharsToken(MacroTokenType.NEWLINE, ch);
            case '\r' -> createCarriageReturnToken(ch);
            case ';' -> createCommentToken(ch);
            case '(' -> createCharsToken(MacroTokenType.LEFT_PAREN, ch);
            case ')' -> createCharsToken(MacroTokenType.RIGHT_PAREN, ch);
            case '[' -> createCharsToken(MacroTokenType.LEFT_BRACKET, ch);
            case ']' -> createCharsToken(MacroTokenType.RIGHT_BRACKET, ch);
            case '{' -> createCharsToken(MacroTokenType.LEFT_BRACE, ch);
            case '}' -> createCharsToken(MacroTokenType.RIGHT_BRACE, ch);
            case '<' -> createCharsToken(MacroTokenType.LESS, ch);
            case '>' -> createCharsToken(MacroTokenType.GREATER, ch);
            case ',' -> createCharsToken(MacroTokenType.COMMA, ch);
            case '=' -> createCharsToken(MacroTokenType.EQUAL, ch);
            case '"', '\'' -> createTextToken(ch);
            default -> {
              yield isIdentifierStart(ch.character()) ? createIdentifierToken(ch) : null;
            }
          };
      if (token != null) {
        return createTextToken(token);
      } else {
        if (!getCharReader().hasNext()) {
          return createTextToken(
              createEofToken(getCharReader().getLine(), getCharReader().getPosition()));
        }

        ch = nextChar();
      }
    }

    return token;
  }

  private MacroToken createCarriageReturnToken(Char ch) throws IOException {
    if (checkNextChar('\n')) {
      return createCharsToken(MacroTokenType.NEWLINE, ch, nextChar());
    }

    return createCharsToken(MacroTokenType.NEWLINE, ch);
  }

  private MacroToken createCommentToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last = ch;

    while (checkNextChar(nextChar -> nextChar != '\n')) {
      last = appendChar(builder);
    }

    return new MacroToken(
        MacroTokenType.COMMENT, ch.line(), ch.position(), last.position(), builder.toString());
  }

  private MacroToken createTextToken(MacroToken token) {
    MacroToken blockToken = createTextToken();

    if (blockToken != null) {
      tokenStack.addFirst(token);
      return blockToken;
    }

    return token;
  }

  private MacroToken createTextToken() {
    MacroToken token = null;

    textChars.removeLast();

    if (!textChars.isEmpty()) {
      Char last = textChars.getLast();
      Char first = textChars.getFirst();

      char[] characters = new char[textChars.size()];
      int i = 0;
      for (Char ch : textChars) {
        characters[i] = ch.character();
        i++;
      }

      textChars.clear();

      token =
          createToken(
              MacroTokenType.TEXT,
              first.line(),
              first.position(),
              last.position(),
              new String(characters));
    }

    return token;
  }

  @Override
  protected MacroToken createToken(
      MacroTokenType tokenType, Line line, int start, int end, String text) {
    return new MacroToken(tokenType, line, start, end, text);
  }

  @Override
  protected boolean isEofToken(MacroToken token) {
    return token.type() == MacroTokenType.EOF;
  }

  private MacroToken createIdentifierToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last = ch;

    while (checkNextChar(
        nextChar -> Character.isLetterOrDigit(nextChar) || nextChar == ':' || nextChar == '_')) {
      last = appendChar(builder);

      if (last.character() == ':') {
        if (checkNextChar(':')) {
          last = appendChar(builder);
        }

        break;
      }
    }

    MacroTokenType tokenType = MacroTokenType.IDENTIFIER;

    String text = builder.toString();

    if (text.equalsIgnoreCase("AF")) {
      if (checkNextChar('\'')) {
        last = appendChar(builder);
        text = builder.toString();
      }
    }

    Directive directive = Directive.find(text);

    if (directive != null) {
      tokenType = MacroTokenType.valueOf(directive.name());
    }

    return new MacroToken(tokenType, ch.line(), ch.position(), last.position(), text);
  }

  private boolean isIdentifierStart(Character character) {
    return Character.isAlphabetic(character)
        || character == '_'
        || character == '.'
        || character == '@';
  }

  private MacroToken createTextToken(Char ch) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(ch.toString());

    Char last;

    while (true) {
      Char nextChar = peekChar();

      if (nextChar != null) {
        if (nextChar.character() == '\\') {
          appendChar(builder);
          last = appendChar(builder);
        } else if (nextChar.character() == ch.character()) {
          last = appendChar(builder);
          break;
        } else {
          last = appendChar(builder);
        }
      }
    }

    MacroTokenType type = MacroTokenType.STRING;

    return new MacroToken(type, ch.line(), ch.position(), last.position(), builder.toString());
  }
}
