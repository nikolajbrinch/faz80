package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record LabelStatement(AssemblerToken identifier) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitLabelStatement(this);
  }
}
