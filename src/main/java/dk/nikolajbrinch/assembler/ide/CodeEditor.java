package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.scanner.Directive;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class CodeEditor extends CodeArea {

  public CodeEditor() {
    super();
    getStyleClass().add("code-editor");
    setLineHighlighterFill(Paint.valueOf("lightblue"));

    richChanges()
        .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
        .subscribe(change -> applySyntaxHighlighting());
    setParagraphGraphicFactory(LineNumberFactory.get(this));
    setParagraphGraphicFactory(
        lineNumber -> {
          Label lineNo = new Label(String.format("%05d ", lineNumber + 1));
          lineNo.getStyleClass().add("line-number");

          return lineNo;
        });
    setOnMouseClicked(event -> setLineHighlighterOn(false));
    textProperty().addListener((obs, oldText, newText) -> setLineHighlighterOn(false));
  }

  private void applySyntaxHighlighting() {
    String text = getText();

    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    AssemblerScanner scanner =
        new AssemblerScanner(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

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
            getStyleClassForTokenType(token));
        lastPos = token.position().end() + 1;
      }
    }
    addEmpty(spansBuilder, text.length() - lastPos);

    StyleSpans<Collection<String>> styleSpans = spansBuilder.create();
    setStyleSpans(0, styleSpans);
  }

  public void highlightLine(int lineNumber) {
    moveTo(position(lineNumber - 1, 0).toOffset());
    requestFollowCaret();
    int currentParagraph = getCurrentParagraph();
    int scrollToParagraph = Math.max(0, currentParagraph - 10);
    showParagraphAtTop(scrollToParagraph);
    setLineHighlighterOn(true);
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

  private String getStyleClassForTokenType(AssemblerToken token) {
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

    if (token.type() == AssemblerTokenType.IDENTIFIER) {
      return "identifier-class";
    }

    return switch (token.type()) {
      case HEX_NUMBER, OCTAL_NUMBER, BINARY_NUMBER, DECIMAL_NUMBER -> "number-class";
      case DOLLAR, DOLLAR_DOLLAR -> "dollar-class";
      case PLUS,
          MINUS,
          SLASH,
          STAR,
          AND,
          AND_AND,
          EQUAL,
          EQUAL_EQUAL,
          BANG,
          BANG_EQUAL,
          LESS,
          LESS_LESS,
          LESS_EQUAL,
          GREATER,
          GREATER_GREATER,
          GREATER_GREATER_GREATER,
          GREATER_EQUAL,
          CARET,
          CARET_CARET,
          TILDE,
          PIPE,
          PIPE_PIPE -> "operator-class";
      case LEFT_BRACE,
          LEFT_BRACKET,
          LEFT_PAREN,
          RIGHT_BRACE,
          RIGHT_BRACKET,
          RIGHT_PAREN -> "paren-class";
      case STRING, CHAR -> "string-class";
      case COMMENT -> "comment-class";
      default -> null;
    };
  }

  public void newText(String text) {
    replaceText(text);
    moveTo(0);
    requestFollowCaret();
  }
}
