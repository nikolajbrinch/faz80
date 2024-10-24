package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;

public record ConditionOperand(AssemblerToken token, Line line, Condition condition) implements Operand {

}
