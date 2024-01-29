package dk.nikolajbrinch.assembler.ide;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ErrorProperty {
  private final SimpleStringProperty location = new SimpleStringProperty("");
  private final SimpleStringProperty type = new SimpleStringProperty("");
  private final SimpleIntegerProperty line = new SimpleIntegerProperty(-1);
  private final SimpleStringProperty task = new SimpleStringProperty("");
  private final SimpleStringProperty token = new SimpleStringProperty("");
  private final SimpleStringProperty description = new SimpleStringProperty("");

  public ErrorProperty() {
    this("", "", -1, "", "", "");
  }

  public ErrorProperty(String source, String type, int line, String task, String token, String description) {
    setLocation(source);
    setType(type);
    setLine(line);
    setType(task);
    setToken(token);
    setDescription(description);
  }

  public String setLocation() {
    return location.get();
  }

  public void setLocation(String t) {
    location.set(t);
  }

  public SimpleStringProperty locationProperty() {
    return location;
  }

  public String setType() {
    return type.get();
  }

  public void setType(String t) {
    type.set(t);
  }

  public SimpleStringProperty typeProperty() {
    return type;
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

  public String getTask() {
    return task.get();
  }

  public void setTask(String t) {
    task.set(t);
  }

  public SimpleStringProperty taskProperty() {
    return task;
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
