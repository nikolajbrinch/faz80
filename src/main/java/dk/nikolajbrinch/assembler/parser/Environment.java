package dk.nikolajbrinch.assembler.parser;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Environment parent;

  private final Map<String, Object> map = new HashMap<>();

  public Environment() {
    this(null);
  }

  public Environment(Environment parent) {
    this.parent = parent;
  }

  public Object get(String name) {
    if (map.containsKey(name)) {
      return map.get(name);
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new IllegalStateException("Undefined variable: " + name);
  }

  public Object assign(String name, Object value) {
    if (map.containsKey(name)) {
      map.put(name, value);

      return value;
    }

    if (parent != null) {
      return parent.assign(name, value);
    }

    throw new IllegalStateException("Undefined variable: " + name);
  }

  public void define(String name, Object value) {
    map.put(name, value);
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
