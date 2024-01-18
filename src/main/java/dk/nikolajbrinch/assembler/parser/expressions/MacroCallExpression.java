package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.StatementVisitor;
import dk.nikolajbrinch.parser.Line;
import java.util.List;

public record MacroCallExpression(AssemblerToken name, List<Statement> arguments)
    implements Expression {

  @Override
  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitMacroCallExpression(this);
  }

  @Override
  public Line line() {
    return name.line();
  }
}
