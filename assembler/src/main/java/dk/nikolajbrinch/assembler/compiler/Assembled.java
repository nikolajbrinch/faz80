package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.util.ArrayList;
import java.util.List;

public class Assembled {

  private boolean originSet = false;
  private long origin = 0;
  private List<AssembledLine> lines = new ArrayList<>();

  public long getOrigin() {
    return origin;
  }

  public List<AssembledLine> getLines() {
    return lines;
  }

  public void add(Statement statement, ByteSource byteSource) {
    lines.add(new AssembledLine(statement, byteSource));
  }

  public void setOrigin(NumberValue origin) {
    if (!originSet) {
      this.origin = origin.value();
      originSet = true;
    }
  }
}
