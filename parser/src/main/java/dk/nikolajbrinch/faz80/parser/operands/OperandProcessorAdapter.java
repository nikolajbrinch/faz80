package dk.nikolajbrinch.faz80.parser.operands;

public interface OperandProcessorAdapter<R> extends OperandProcessor<R> {

  default R processRegisterOperand(RegisterOperand operand) { return null; }

  default R processExpressionOperand(ExpressionOperand operand) { return null; }

  default R processConditionOperand(ConditionOperand operand) { return null; }

  default R processGroupingOperand(GroupingOperand operand) { return null; }
}
