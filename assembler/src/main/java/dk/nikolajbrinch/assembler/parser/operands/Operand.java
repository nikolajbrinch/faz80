package dk.nikolajbrinch.assembler.parser.operands;

import dk.nikolajbrinch.parser.Line;

public interface Operand {

  <R> R accept(OperandVisitor<R> visitor);

  Line line();
}
