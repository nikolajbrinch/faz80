package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.scanner.Token;
import java.util.List;

public record MacroCallStatement(Token name, List<Statement> arguments) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroCallStatement(this);
  }
}
