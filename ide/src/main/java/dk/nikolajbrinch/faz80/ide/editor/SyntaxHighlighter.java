package dk.nikolajbrinch.faz80.ide.editor;

import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.parser.base.MessageType;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.faz80.scanner.Directive;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.parser.ParseMessage;
import dk.nikolajbrinch.scanner.SourceInfo;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class SyntaxHighlighter {

  private final Logger logger = LoggerFactory.getLogger();

  public StyleSpans<Collection<String>> createStyleSpans(
      String text, List<BaseMessage> messages, SourceInfo sourceInfo) {
    long currentTime = System.currentTimeMillis();
    List<AssemblerToken> errorTokens = collectErrorTokens(MessageType.ERROR, messages);
    List<AssemblerToken> warningTokens = collectErrorTokens(MessageType.WARNING, messages);

    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    try (AssemblerScanner scanner = new AssemblerScanner(new StringSource(sourceInfo, text))) {
      int lastPos = 0;

      for (AssemblerToken token : scanner) {
        if (token.type() == AssemblerTokenType.EOF) {
          break;
        }

        if (token.position().start() + 1 <= text.length()) {
          addEmpty(spansBuilder, token.position().start() - lastPos);
          String styleClass = getStyleClassForTokenType(token, errorTokens);
          String[] styleClasses = new String[] {styleClass};

          if (isTokenInTokens(token, warningTokens)) {
            styleClasses = new String[] {styleClass, "warning-class"};
          }

          addStyleClass(
              spansBuilder, token.position().end() - token.position().start() + 1, styleClasses);
          lastPos = token.position().end() + 1;
        }
      }
      addEmpty(spansBuilder, text.length() - lastPos);

      return spansBuilder.create();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      long endTime = System.currentTimeMillis();
      logger.debug("SyntaxHighlighter.createStyleSpans() took " + (endTime - currentTime) + " ms");
    }
  }

  private List<AssemblerToken> collectErrorTokens(MessageType type, List<BaseMessage> errors) {
    return errors.stream()
        .map(
            message ->
                switch (message) {
                  case ParseMessage parseMessage ->
                      parseMessage.type() == type ? parseMessage.token() : null;
                  default -> null;
                })
        .filter(Objects::nonNull)
        .toList();
  }

  private void addEmpty(StyleSpansBuilder<Collection<String>> spansBuilder, int length) {
    addStyleClass(spansBuilder, length, null);
  }

  private void addStyleClass(
      StyleSpansBuilder<Collection<String>> spansBuilder, int length, String... styleClasses) {
    if (length >= 0) {
      spansBuilder.add(
          styleClasses == null ? Collections.emptyList() : Arrays.asList(styleClasses), length);
    }
  }

  private String getStyleClassForTokenType(AssemblerToken token, List<AssemblerToken> errorTokens) {
    if (isTokenInTokens(token, errorTokens)) {
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
      case AssemblerTokenType.HEX_NUMBER,
              AssemblerTokenType.OCTAL_NUMBER,
              AssemblerTokenType.BINARY_NUMBER,
              AssemblerTokenType.DECIMAL_NUMBER ->
          "number-class";
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
      case AssemblerTokenType.LEFT_BRACE,
              AssemblerTokenType.LEFT_BRACKET,
              AssemblerTokenType.LEFT_PAREN,
              AssemblerTokenType.RIGHT_BRACE,
              AssemblerTokenType.RIGHT_BRACKET,
              AssemblerTokenType.RIGHT_PAREN ->
          "paren-class";
      case AssemblerTokenType.STRING, AssemblerTokenType.CHAR -> "string-class";
      case AssemblerTokenType.COMMENT -> "comment-class";
      case AssemblerTokenType.DIRECTIVE -> "error-class";
      default -> null;
    };
  }

  private boolean isTokenInTokens(AssemblerToken token, Collection<AssemblerToken> tokens) {
    return tokens.stream().anyMatch(t -> matchTokens(token, t));
  }

  private boolean matchTokens(AssemblerToken token1, AssemblerToken token2) {
    return token1.type() == token2.type() && token1.position().equals(token2.position());
  }
}
