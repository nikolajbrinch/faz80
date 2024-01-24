package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.Assembled;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.Linked;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.parser.scanner.Directive;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.z80.Mnemonic;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.FileSource;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.ScannerSource;
import dk.nikolajbrinch.parser.StringSource;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.RichTextChange;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class TabController {

  @FXML private CodeEditor editor;

  private ScannerSource source = new StringSource("");

  public CodeEditor getEditor() {
    return editor;
  }

  private CompileResultProperty compileResultProperty = new CompileResultProperty();
  private Compiler compiler = new Compiler();

  public void initialize() {
    editor
        .plainTextChanges()
        .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
        .subscribe(this::handleTextChange);
    editor
        .richChanges()
        .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
        .subscribe(this::applySyntaxHighlighting);
  }

  private void handleTextChange(PlainTextChange change) {
    try {
      parse();
    } catch (IOException e) {
      /*
       * Ignore
       */
    }
  }

  public void setFile(File file) throws IOException {
    this.source = new FileSource(file);
    compiler.setDirectory(file.getParentFile().getCanonicalFile());
    editor.newText(file);
    parse();
  }

  public void parse() throws IOException {
    BlockStatement block;

    compiler.parse(editor.getText());
    compileResultProperty.setHasErrors(compiler.hasErrors());
    compileResultProperty.setErrors(compiler.getErrors());
    compileResultProperty.setParseResult(compiler.getParseResult());

    if (compiler.getParseResult() != null) {
      compileResultProperty.setAstTree(new AstTreeCreator().createTree(compiler.getParseResult()));
    }
  }

  public void compile() throws IOException {
    compiler.compile(editor.getText());

    compileResultProperty.setHasErrors(compiler.hasErrors());
    compileResultProperty.setErrors(compiler.getErrors());
    compileResultProperty.setParseResult(compiler.getParseResult());
    compileResultProperty.setAssembleResult(compiler.getAssembleResult());
    compileResultProperty.setLinkResult(compiler.getLinkResult());

    if (compiler.getParseResult() != null) {
      compileResultProperty.setAstTree(new AstTreeCreator().createTree(compiler.getParseResult()));
    }
  }

  private void applySyntaxHighlighting(
      RichTextChange<Collection<String>, String, Collection<String>> change) {
    String text = editor.getText();

    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    List<AssemblerToken> errorTokens =
        compileResultProperty.getErrors().stream()
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

    try (AssemblerScanner scanner =
        new AssemblerScanner(new StringSource(source.getSourceInfo(), text))) {

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

      StyleSpans<Collection<String>> styleSpans = spansBuilder.create();
      editor.setStyleSpans(0, styleSpans);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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
      case IDENTIFIER -> "identifier-class";
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
              PIPE_PIPE ->
          "operator-class";
      case LEFT_BRACE, LEFT_BRACKET, LEFT_PAREN, RIGHT_BRACE, RIGHT_BRACKET, RIGHT_PAREN ->
          "paren-class";
      case STRING, CHAR -> "string-class";
      case COMMENT -> "comment-class";
      case DIRECTIVE -> "error-class";
      default -> null;
    };
  }

  private boolean tokenInErrorTokens(AssemblerToken token, List<AssemblerToken> errorTokens) {
    return errorTokens.stream().anyMatch(errorToken -> matchTokens(token, errorToken));
  }

  private boolean matchTokens(AssemblerToken token, AssemblerToken errorToken) {
    return token.type() == errorToken.type() && token.position().equals(errorToken.position());
  }

  public boolean hasErrors() {
    return compileResultProperty.hasErrors();
  }

  public List<BaseError<?>> getErrors() {
    return compileResultProperty.getErrors();
  }

  public TreeItem<AstTreeValue> getAstTree() {
    return compileResultProperty.getAstTree();
  }

  public Property<TreeItem<AstTreeValue>> getAstTreeProperty() {
    return compileResultProperty.astTreeProperty();
  }

  public Assembled getAssembleResult() {
    return compileResultProperty.getAssembleResult();
  }

  public Linked getLinkResult() {
    return compileResultProperty.getLinkResult();
  }
}
