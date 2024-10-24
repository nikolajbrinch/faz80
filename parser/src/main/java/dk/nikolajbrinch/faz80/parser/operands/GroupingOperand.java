package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.scanner.Line;

public record GroupingOperand(Operand operand) implements Operand {

  @Override
  public Line line() {
    return operand.line();
  }
}
