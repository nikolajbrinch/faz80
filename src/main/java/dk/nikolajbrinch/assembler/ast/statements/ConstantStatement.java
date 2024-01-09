package dk.nikolajbrinch.assembler.ast.statements;

import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.Line;

public record ConstantStatement(AssemblerToken identifier, Expression value) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitConstantStatement(this);
  }

  @Override
  public Line line() {
    return identifier.line();
  }
}
