package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleError;
import dk.nikolajbrinch.assembler.compiler.Assembler;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import dk.nikolajbrinch.assembler.parser.Condition;
import dk.nikolajbrinch.assembler.parser.Register;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.parser.scanner.Directive;
import dk.nikolajbrinch.assembler.parser.scanner.Mnemonic;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.FileSource;
import dk.nikolajbrinch.parser.ParseError;
import dk.nikolajbrinch.parser.ScannerSource;
import dk.nikolajbrinch.parser.StringSource;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

  private ParseResultProperty parseResultProperty = new ParseResultProperty();
  private AssembleResultProperty assembleResultProperty = new AssembleResultProperty();

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
    editor.newText(file);
    parse();
  }

  public void parse() throws IOException {
    AssemblerParser parser = new AssemblerParser();
    parse(parser);
  }

  private void parse(AssemblerParser parser) throws IOException {
    BlockStatement block;

    block = parser.parse(editor.getText());
    parseResultProperty.setHasErrors(parser.hasErrors());
    parseResultProperty.setErrors(parser.getErrors());
    parseResultProperty.setResult(block);

    if (block != null) {
      parseResultProperty.setAstTree(new AstTreeCreator().createTree(block));
    }
  }

  public void assemble() throws IOException {
    if (parseResultProperty.getResult() == null) {
      parse();
    }

    if (parseResultProperty.getResult() != null && !parseResultProperty.hasErrors()) {
      Assembler assembler = new Assembler(new ExpressionEvaluator());
      assembler.assemble(parseResultProperty.getResult());

      assembleResultProperty.setHasErrors(assembler.hasErrors());
      assembleResultProperty.setErrors(assembler.getErrors());
      assembleResultProperty.setResult(assembler.getBytes());
    }
  }

  private void applySyntaxHighlighting(
      RichTextChange<Collection<String>, String, Collection<String>> change) {
    String text = editor.getText();

    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    List<AssemblerToken> errorTokens =
        parseResultProperty.getErrors().stream()
            .map(error -> error.exception().getToken())
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

  public boolean hasParseErrors() {
    return parseResultProperty.hasErrors();
  }

  public List<ParseError> getParseErrors() {
    return parseResultProperty.getErrors();
  }

  public TreeItem<AstTreeValue> getAstTree() {
    return parseResultProperty.getAstTree();
  }

  public Property<TreeItem<AstTreeValue>> getAstTreeProperty() {
    return parseResultProperty.astTreeProperty();
  }

  public boolean hasAssembleErrors() {
    return assembleResultProperty.hasErrors();
  }

  public List<AssembleError> getAssembleErrors() {
    return assembleResultProperty.getErrors();
  }

  public List<ByteSource> getAssembleResult() {
    return assembleResultProperty.getResult();
  }
}
