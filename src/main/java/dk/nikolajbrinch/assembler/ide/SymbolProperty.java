package dk.nikolajbrinch.assembler.ide;

import javafx.beans.property.SimpleStringProperty;

public class SymbolProperty {
  private final SimpleStringProperty key = new SimpleStringProperty("");
  private final SimpleStringProperty type = new SimpleStringProperty("");
  private final SimpleStringProperty value = new SimpleStringProperty("");

  public SymbolProperty() {
    this("", "", "");
  }

  public SymbolProperty(String key, String type, String value) {
    setKey(key);
    setType(type);
    setValue(value);
  }

  public String getKey() {
    return key.get();
  }

  public void setKey(String k) {
    key.set(k);
  }

  public SimpleStringProperty keyProperty() {
    return key;
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
