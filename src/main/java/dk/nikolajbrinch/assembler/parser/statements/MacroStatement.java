package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.ExpressionVisitor;
import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.scanner.Token;
import java.util.List;

public record MacroStatement(Token name, List<Parameter> parameters, Statement block) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitMacroStatement(this);
  }

}
