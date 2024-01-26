package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.Assembled;
import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.Linked;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.impl.FileSource;
import dk.nikolajbrinch.parser.ScannerSource;
import dk.nikolajbrinch.parser.impl.StringSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.EventStream;

public class TabController {

  private final SyntaxHighlighter syntaxHighlighter = new SyntaxHighlighter();

  private final CompileResultProperty compileResultProperty = new CompileResultProperty();
  private final Compiler compiler = new Compiler();

  @FXML private CodeEditor editor;
  private ScannerSource source = new StringSource("");

  public CodeEditor getEditor() {
    return editor;
  }

  public void initialize() {
    editor
        .richChanges()
        .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
        .subscribe(
            richTextChange ->
                editor.setStyleSpans(
                    0,
                    syntaxHighlighter.createStyleSpans(
                        editor.getText(),
                        compileResultProperty.getErrors(),
                        source.getSourceInfo())));
  }

  public ScannerSource getSource() {
    return source;
  }

  public void setFile(File file) throws IOException {
    this.source = new FileSource(file);
    compiler.setDirectory(file.getParentFile().getCanonicalFile());
    editor.newText(file);
  }

  public EventStream<PlainTextChange> textChanges() {
    return editor.plainTextChanges();
  }

  void parse() throws IOException {
    compiler.parse(editor.getText());
    compileResultProperty.setHasErrors(compiler.hasErrors());
    compileResultProperty.setErrors(compiler.getErrors());
    compileResultProperty.setParseResult(compiler.getParseResult());

    if (compiler.getParseResult() != null) {
      compileResultProperty.setAstTree(new AstTreeBuilder().build(compiler.getParseResult()));
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
      compileResultProperty.setAstTree(new AstTreeBuilder().build(compiler.getParseResult()));
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

  public Property<TreeItem<AstTreeValue>> getAstTreeProperty() {
    return compileResultProperty.astTreeProperty();
  }

  public Assembled getAssembleResult() {
    return compileResultProperty.getAssembleResult();
  }

  public Linked getLinkResult() {
    return compileResultProperty.getLinkResult();
  }

  public void highlightLine(int lineNumber) {
    if (lineNumber != -1) {
      editor.highlightLine(lineNumber);
    }
  }
}
