package dk.nikolajbrinch.faz80.parser.operands;

public interface OperandVisitor<R> {

  R visitRegisterOperand(RegisterOperand operand);

  R visitExpressionOperand(ExpressionOperand operand);

  R visitConditionOperand(ConditionOperand operand);

  R visitGroupingOperand(GroupingOperand operand);
}
