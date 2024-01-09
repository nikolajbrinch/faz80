package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record LabelStatement(AssemblerToken identifier) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitLabelStatement(this);
  }

  @Override
  public Line line() {
    return identifier.line();
  }
}
