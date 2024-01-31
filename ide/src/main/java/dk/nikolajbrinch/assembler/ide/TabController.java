package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleResult;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.ide.ast.AstTreeBuilder;
import dk.nikolajbrinch.assembler.ide.ast.AstTreeValue;
import dk.nikolajbrinch.assembler.linker.LinkResult;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.BaseException;
import dk.nikolajbrinch.parser.impl.FileSource;
import dk.nikolajbrinch.parser.ScannerSource;
import dk.nikolajbrinch.parser.impl.StringSource;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.model.PlainTextChange;

public class TabController {

  private final SyntaxHighlighter syntaxHighlighter = new SyntaxHighlighter();

  private final CompileResultProperty compileResultProperty = new CompileResultProperty();
  private final Compiler compiler = new Compiler();

  @FXML private CodeEditor editor;
  private ScannerSource source = new StringSource("");

  public CodeEditor getEditor() {
    return editor;
  }

  public SimpleObjectProperty<PlainTextChange> textChange = new SimpleObjectProperty<>(null);

  public void initialize() {
    editor
        .plainTextChanges()
        .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
        .subscribe(this::textChanged);
  }

  private void textChanged(PlainTextChange textChange) {
    try {
      parse();
      editor.setStyleSpans(
          0,
          syntaxHighlighter.createStyleSpans(
              editor.getText(), compileResultProperty.getErrors(), source.getSourceInfo()));
      this.textChange.set(textChange);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public ScannerSource getSource() {
    return source;
  }

  public void setFile(File file) throws IOException {
    this.source = new FileSource(file);
    compiler.setDirectory(file.getParentFile().getCanonicalFile());
    editor.newText(file);
  }

  void parse() throws IOException {
    compiler.parse(editor.getText());
    compileResultProperty.setHasErrors(compiler.hasErrors());
    compileResultProperty.setErrors(compiler.getErrors());
    compileResultProperty.setParseResult(compiler.getParseResult());

    if (compiler.getParseResult() != null) {
      compileResultProperty.setAstTree(
          new AstTreeBuilder().build(compiler.getParseResult().block()));
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
      compileResultProperty.setAstTree(
          new AstTreeBuilder().build(compiler.getParseResult().block()));
    }
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

  public Property<TreeItem<AstTreeValue>> astTreeProperty() {
    return compileResultProperty.astTreeProperty();
  }

  public ListProperty<BaseError<? extends BaseException>> errorsProperty() {
    return compileResultProperty.errorsProperty();
  }

  public AssembleResult getAssembleResult() {
    return compileResultProperty.getAssembleResult();
  }

  public LinkResult getLinkResult() {
    return compileResultProperty.getLinkResult();
  }

  public void highlightLine(int lineNumber) {
    if (lineNumber != -1) {
      editor.highlightLine(lineNumber);
    }
  }

  public SimpleObjectProperty<PlainTextChange> plainTextChange() {
    return textChange;
  }

  public void dispose() {}
}
