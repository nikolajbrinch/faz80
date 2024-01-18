package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.ParseError;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public class ParseResultProperty {

  private final SimpleBooleanProperty hasErrors = new SimpleBooleanProperty();

  private final SimpleObjectProperty<List<ParseError>> errors =
      new SimpleObjectProperty<>(List.of());

  private final SimpleObjectProperty<BlockStatement> result = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<TreeItem<AstTreeValue>> astTree =
      new SimpleObjectProperty<>(null);

  public ParseResultProperty() {
    this(false, List.of(), null, null);
  }

  public ParseResultProperty(
      boolean hasErrors,
      List<ParseError> errors,
      BlockStatement result,
      TreeItem<AstTreeValue> astTree) {
    setHasErrors(hasErrors);
    setErrors(errors);
    setResult(result);
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

  public List<ParseError> getErrors() {
    return errors.get();
  }

  public void setErrors(List<ParseError> value) {
    errors.set(value);
  }

  public SimpleObjectProperty<List<ParseError>> errorsProperty() {
    return errors;
  }

  public BlockStatement getResult() {
    return result.get();
  }

  public void setResult(BlockStatement value) {
    result.set(value);
  }

  public SimpleObjectProperty<BlockStatement> resultProperty() {
    return result;
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
