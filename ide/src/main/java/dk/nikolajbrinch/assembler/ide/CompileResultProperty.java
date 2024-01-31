package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleResult;
import dk.nikolajbrinch.assembler.ide.ast.AstTreeValue;
import dk.nikolajbrinch.assembler.linker.LinkResult;
import dk.nikolajbrinch.assembler.parser.AssemblerParseResult;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.BaseException;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

public class CompileResultProperty {

  private final SimpleBooleanProperty hasErrors = new SimpleBooleanProperty();

  private final SimpleListProperty<BaseError<?>> errors =
      new SimpleListProperty<>(FXCollections.emptyObservableList());

  private final SimpleObjectProperty<AssemblerParseResult> parseResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<AssembleResult> assembleResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<LinkResult> linkResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<TreeItem<AstTreeValue>> astTree =
      new SimpleObjectProperty<>(null);

  public CompileResultProperty() {
    this(false, List.of(), null, null, null, null);
  }

  public CompileResultProperty(
      boolean hasErrors,
      List<BaseError<?>> errors,
      AssemblerParseResult parseResult,
      AssembleResult assembleResult,
      LinkResult linkResult,
      TreeItem<AstTreeValue> astTree) {
    setHasErrors(hasErrors);
    setErrors(errors);
    setParseResult(parseResult);
    setAssembleResult(assembleResult);
    setLinkResult(linkResult);
    setAstTree(astTree);
  }

  public boolean hasErrors() {
    return hasErrors.get();
  }

  public void setHasErrors(boolean value) {
    hasErrors.set(value);
  }

  public SimpleBooleanProperty hasErrorsProperty() {
    return hasErrors;
  }

  public List<BaseError<? extends BaseException>> getErrors() {
    return errors.get();
  }

  public void setErrors(List<BaseError<? extends BaseException>> value) {
    errors.set(FXCollections.observableList(value));
  }

  public SimpleListProperty<BaseError<? extends BaseException>> errorsProperty() {
    return errors;
  }

  public AssemblerParseResult getParseResult() {
    return parseResult.get();
  }

  public void setParseResult(AssemblerParseResult value) {
    parseResult.set(value);
  }

  public SimpleObjectProperty<AssemblerParseResult> parseResultProperty() {
    return parseResult;
  }

  public AssembleResult getAssembleResult() {
    return assembleResult.get();
  }

  public void setAssembleResult(AssembleResult value) {
    assembleResult.set(value);
  }

  public SimpleObjectProperty<LinkResult> linkResultProperty() {
    return linkResult;
  }

  public LinkResult getLinkResult() {
    return linkResult.get();
  }

  public void setLinkResult(LinkResult value) {
    linkResult.set(value);
  }

  public SimpleObjectProperty<AssembleResult> assemblResulteProperty() {
    return assembleResult;
  }

  public TreeItem<AstTreeValue> getAstTree() {
    return astTree.get();
  }

  public void setAstTree(TreeItem<AstTreeValue> value) {
    astTree.set(value);
  }

  public SimpleObjectProperty<TreeItem<AstTreeValue>> astTreeProperty() {
    return astTree;
  }
}
