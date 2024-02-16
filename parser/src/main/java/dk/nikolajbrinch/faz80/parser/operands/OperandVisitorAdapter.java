package dk.nikolajbrinch.faz80.parser.operands;

public interface OperandVisitorAdapter<R> extends OperandVisitor<R> {

  @Override
  default R visitRegisterOperand(RegisterOperand operand) {
    return null;
  }

  @Override
  default R visitExpressionOperand(ExpressionOperand operand) {
    return null;
  }

  @Override
  default R visitConditionOperand(ConditionOperand operand) {
    return null;
  }

  @Override
  default R visitGroupingOperand(GroupingOperand operand) {
    return null;
  }
}
