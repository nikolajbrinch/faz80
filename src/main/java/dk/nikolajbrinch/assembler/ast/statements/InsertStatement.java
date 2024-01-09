package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record InsertStatement(AssemblerToken string) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitInsertStatement(this);
  }

  @Override
  public Line line() {
    return string.line();
  }
}
