package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
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

  private final SimpleObjectProperty<BlockStatement> parseResult = new SimpleObjectProperty<>(null);

  private final SimpleListProperty<ByteSource> assembleResult =
      new SimpleListProperty<>(FXCollections.emptyObservableList());

  private final SimpleObjectProperty<TreeItem<AstTreeValue>> astTree =
      new SimpleObjectProperty<>(null);

  public CompileResultProperty() {
    this(false, List.of(), null, List.of(), null);
  }

  public CompileResultProperty(
      boolean hasErrors,
      List<BaseError<?>> errors,
      BlockStatement parseResult,
      List<ByteSource> assembleResult,
      TreeItem<AstTreeValue> astTree) {
    setHasErrors(hasErrors);
    setErrors(errors);
    setParseResult(parseResult);
    setAssembleResult(assembleResult);
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

  public BlockStatement getParseResult() {
    return parseResult.get();
  }

  public void setParseResult(BlockStatement value) {
    parseResult.set(value);
  }

  public SimpleObjectProperty<BlockStatement> parseResultProperty() {
    return parseResult;
  }

  public List<ByteSource> getAssembleResult() {
    return assembleResult.get();
  }

  public void setAssembleResult(List<ByteSource> value) {
    assembleResult.set(FXCollections.observableList(value));
  }

  public SimpleListProperty<ByteSource> parseAssembleProperty() {
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