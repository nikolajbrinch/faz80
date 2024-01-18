package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleError;
import dk.nikolajbrinch.assembler.compiler.ByteSource;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.parser.ParseError;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public class AssembleResultProperty {

  private final SimpleBooleanProperty hasErrors = new SimpleBooleanProperty();

  private final SimpleObjectProperty<List<AssembleError>> errors =
      new SimpleObjectProperty<>(List.of());

  private final SimpleObjectProperty<List<ByteSource>> result =
      new SimpleObjectProperty<>(List.of());

  public AssembleResultProperty() {
    this(false, List.of(), null);
  }

  public AssembleResultProperty(
      boolean hasErrors, List<AssembleError> errors, List<ByteSource> result) {
    setHasErrors(hasErrors);
    setErrors(errors);
    setResult(result);
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

  public List<AssembleError> getErrors() {
    return errors.get();
  }

  public void setErrors(List<AssembleError> value) {
    errors.set(value);
  }

  public SimpleObjectProperty<List<AssembleError>> errorsProperty() {
    return errors;
  }

  public List<ByteSource> getResult() {
    return result.get();
  }

  public void setResult(List<ByteSource> value) {
    result.set(value);
  }

  public SimpleObjectProperty<List<ByteSource>> resultProperty() {
    return result;
  }
}
