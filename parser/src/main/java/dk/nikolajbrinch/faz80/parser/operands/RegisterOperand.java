package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;

public record RegisterOperand(AssemblerToken token, Line line, Register register, Expression displacement)
    implements Operand {

}
