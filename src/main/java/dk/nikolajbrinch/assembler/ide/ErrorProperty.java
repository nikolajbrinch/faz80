package dk.nikolajbrinch.assembler.ide;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ErrorProperty {
  private final SimpleIntegerProperty line = new SimpleIntegerProperty(-1);
  private final SimpleStringProperty type = new SimpleStringProperty("");
  private final SimpleStringProperty token = new SimpleStringProperty("");
  private final SimpleStringProperty description = new SimpleStringProperty("");

  public ErrorProperty() {
    this(-1, "", "", "");
  }

  public ErrorProperty(int line, String type, String token, String description) {
    setLine(line);
    setType(type);
    setToken(token);
    setDescription(description);
  }

  public int getLine() {
    return line.get();
  }

  public void setLine(int l) {
    line.set(l);
  }

  public SimpleIntegerProperty lineProperty() {
    return line;
  }

  public String getTtype() {
    return type.get();
  }

  public void setType(String t) {
    type.set(t);
  }

  public SimpleStringProperty typeProperty() {
    return type;
  }

  public String getToken() {
    return token.get();
  }

  public void setToken(String t) {
    token.set(t);
  }

  public SimpleStringProperty tokenProperty() {
    return token;
  }

  public String getDescription() {
    return description.get();
  }

  public void setDescription(String t) {
    description.set(t);
  }

  public SimpleStringProperty descriptionProperty() {
    return description;
  }
}
