package dk.nikolajbrinch.assembler.ide;

import javafx.beans.property.SimpleStringProperty;

public class SymbolProperty {
  private final SimpleStringProperty name = new SimpleStringProperty("");
  private final SimpleStringProperty type = new SimpleStringProperty("");
  private final SimpleStringProperty value = new SimpleStringProperty("");

  public SymbolProperty() {
    this("", "", "");
  }

  public SymbolProperty(String name, String type, String value) {
    setName(name);
    setType(type);
    setValue(value);
  }

  public String getName() {
    return name.get();
  }

  public void setName(String k) {
    name.set(k);
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public String getType() {
    return type.get();
  }

  public void setType(String t) {
    type.set(t);
  }

  public SimpleStringProperty typeProperty() {
    return type;
  }

  public String getValue() {
    return value.get();
  }

  public void setValue(String v) {
    value.set(v);
  }

  public SimpleStringProperty valueProperty() {
    return value;
  }
}
