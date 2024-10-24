package dk.nikolajbrinch.faz80.parser.operands;

import dk.nikolajbrinch.scanner.Line;

public sealed interface Operand
    permits ConditionOperand, RegisterOperand, ExpressionOperand, GroupingOperand {

  Line line();
}
