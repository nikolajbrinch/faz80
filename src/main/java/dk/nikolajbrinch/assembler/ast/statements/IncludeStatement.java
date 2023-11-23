package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.scanner.AssemblerToken;

public record IncludeStatement(AssemblerToken string) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitIncludeStatement(this);
  }
}
