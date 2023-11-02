package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.assembler.scanner.Token;

public record EndStatement(Token end) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitEndStatement(this);
  }
}
