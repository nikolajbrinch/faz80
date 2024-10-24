package dk.nikolajbrinch.faz80.parser.operands;

public interface NoOpOperandProcessor extends OperandProcessor<Operand>{

  @Override
  default Operand processRegisterOperand(RegisterOperand operand) {
    return operand;
  }

  @Override
  default Operand processExpressionOperand(ExpressionOperand operand) {
    return operand;
  }

  @Override
  default Operand processConditionOperand(ConditionOperand operand) {
    return operand;
  }

  @Override
  default Operand processGroupingOperand(GroupingOperand operand) {
    return operand;
  }
}
