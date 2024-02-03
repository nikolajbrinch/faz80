package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.scanner.Line;

public record GroupingOperand(Operand operand) implements Operand {

  @Override
  public <R> R accept(OperandVisitor<R> visitor) {
    return visitor.visitGroupingOperand(this);
  }

  @Override
  public Line line() {
    return operand.line();
  }
}
