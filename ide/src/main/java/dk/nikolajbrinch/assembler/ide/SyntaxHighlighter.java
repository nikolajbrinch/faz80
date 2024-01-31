package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.parser.scanner.Directive;
import dk.nikolajbrinch.assembler.z80.Mnemonic;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.BaseException;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.SourceInfo;
import dk.nikolajbrinch.parser.impl.StringSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class SyntaxHighlighter {

  public StyleSpans<Collection<String>> createStyleSpans(
      String text, List<BaseError<? extends BaseException>> errors, SourceInfo sourceInfo) {
    List<AssemblerToken> errorTokens = collectErrorTokens(errors);

    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    try (AssemblerScanner scanner = new AssemblerScanner(new StringSource(sourceInfo, text))) {
      int lastPos = 0;

      for (AssemblerToken token : scanner) {
        if (token.type() == AssemblerTokenType.EOF) {
          break;
        }

        if (token.position().start() + 1 <= text.length()) {
          addEmpty(spansBuilder, token.position().start() - lastPos);
          addStyleClass(
              spansBuilder,
              token.position().end() - token.position().start() + 1,
              getStyleClassForTokenType(token, errorTokens));
          lastPos = token.position().end() + 1;
        }
      }
      addEmpty(spansBuilder, text.length() - lastPos);

      return spansBuilder.create();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private List<AssemblerToken> collectErrorTokens(List<BaseError<? extends BaseException>> errors) {
    return errors.stream()
        .map(
            error -> {
              Exception exception = error.exception();

              if (exception instanceof ParseException parseException) {
                return parseException.getToken();
              }

              return null;
            })
        .filter(Objects::nonNull)
        .toList();
  }

  private void addEmpty(StyleSpansBuilder<Collection<String>> spansBuilder, int length) {
    addStyleClass(spansBuilder, length, null);
  }

  private void addStyleClass(
      StyleSpansBuilder<Collection<String>> spansBuilder, int length, String styleClass) {
    if (length >= 0) {
      spansBuilder.add(
          styleClass == null ? Collections.emptyList() : Collections.singleton(styleClass), length);
    }
  }

  private String getStyleClassForTokenType(AssemblerToken token, List<AssemblerToken> errorTokens) {
    if (tokenInErrorTokens(token, errorTokens)) {
      return "error-class";
    }

    if (Mnemonic.find(token.text()) != null) {
      return "mnemonic-class";
    }

    if (Directive.find(token.text()) != null) {
      return "directive-class";
    }

    if (Register.find(token.text()) != null) {
      return "register-class";
    }

    if (Condition.find(token.text()) != null) {
      return "register-class";
    }

    return switch (token.type()) {
      case AssemblerTokenType.IDENTIFIER -> "identifier-class";
      case AssemblerTokenType.HEX_NUMBER, AssemblerTokenType.OCTAL_NUMBER, AssemblerTokenType.BINARY_NUMBER, AssemblerTokenType.DECIMAL_NUMBER -> "number-class";
      case AssemblerTokenType.DOLLAR, AssemblerTokenType.DOLLAR_DOLLAR -> "dollar-class";
      case AssemblerTokenType.PLUS,
              AssemblerTokenType.MINUS,
              AssemblerTokenType.SLASH,
              AssemblerTokenType.STAR,
              AssemblerTokenType.AND,
              AssemblerTokenType.AND_AND,
              AssemblerTokenType.EQUAL,
              AssemblerTokenType.EQUAL_EQUAL,
              AssemblerTokenType.BANG,
              AssemblerTokenType.BANG_EQUAL,
              AssemblerTokenType.LESS,
              AssemblerTokenType.LESS_LESS,
              AssemblerTokenType.LESS_EQUAL,
              AssemblerTokenType.GREATER,
              AssemblerTokenType.GREATER_GREATER,
              AssemblerTokenType.GREATER_GREATER_GREATER,
              AssemblerTokenType.GREATER_EQUAL,
              AssemblerTokenType.CARET,
              AssemblerTokenType.CARET_CARET,
              AssemblerTokenType.TILDE,
              AssemblerTokenType.PIPE,
              AssemblerTokenType.PIPE_PIPE ->
          "operator-class";
      case AssemblerTokenType.LEFT_BRACE, AssemblerTokenType.LEFT_BRACKET, AssemblerTokenType.LEFT_PAREN, AssemblerTokenType.RIGHT_BRACE, AssemblerTokenType.RIGHT_BRACKET, AssemblerTokenType.RIGHT_PAREN ->
          "paren-class";
      case AssemblerTokenType.STRING, AssemblerTokenType.CHAR -> "string-class";
      case AssemblerTokenType.COMMENT -> "comment-class";
      case AssemblerTokenType.DIRECTIVE -> "error-class";
      default -> null;
    };
  }

  private boolean tokenInErrorTokens(AssemblerToken token, List<AssemblerToken> errorTokens) {
    return errorTokens.stream().anyMatch(errorToken -> matchTokens(token, errorToken));
  }

  private boolean matchTokens(AssemblerToken token, AssemblerToken errorToken) {
    return token.type() == errorToken.type() && token.position().equals(errorToken.position());
  }
}
