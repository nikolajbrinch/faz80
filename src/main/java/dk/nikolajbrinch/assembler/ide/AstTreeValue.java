package dk.nikolajbrinch.assembler.ide;

public record AstTreeValue(int lineNumber, Object value) {

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
