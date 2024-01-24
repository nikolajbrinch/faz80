package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import java.util.ArrayList;
import java.util.List;

public class Assembled {

  private boolean originSet = false;
  private long origin = 0;
  private List<ByteSource> bytes = new ArrayList<>();

  public long getOrigin() {
    return origin;
  }

  public List<ByteSource> getBytes() {
    return bytes;
  }

  public void add(ByteSource byteSource) {
    bytes.add(byteSource);
  }

  public void setOrigin(NumberValue origin) {
    if (!originSet) {
      this.origin = origin.value();
      originSet = true;
    }
  }
}
