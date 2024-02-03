package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.scanner.Line;

public interface Operand {

  <R> R accept(OperandVisitor<R> visitor);

  Line line();
}
