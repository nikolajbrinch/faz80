package dk.nikolajbrinch.assembler.ide.symbols;

import javafx.beans.property.SimpleStringProperty;

public class SymbolProperty {
  private final SimpleStringProperty name = new SimpleStringProperty("");
  private final SimpleStringProperty type = new SimpleStringProperty("");
  private final SimpleStringProperty values = new SimpleStringProperty("");

  public SymbolProperty() {
    this("", "", "");
  }

  public SymbolProperty(String name, String type, String values) {
    setName(name);
    setType(type);
    setValues(values);
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

  public String getValues() {
    return values.get();
  }

  public void setValues(String v) {
    values.set(v);
  }

  public SimpleStringProperty valuesProperty() {
    return values;
  }
}
