package dk.nikolajbrinch.faz80.parser.operands;

public interface OperandProcessor<R> {

  default R process(Operand operand) {
    return operand == null ? null : switch (operand) {
      case RegisterOperand o -> processRegisterOperand(o);
      case ExpressionOperand o -> processExpressionOperand(o);
      case ConditionOperand o -> processConditionOperand(o);
      case GroupingOperand o -> processGroupingOperand(o);
    };
  }

  R processRegisterOperand(RegisterOperand operand);

  R processExpressionOperand(ExpressionOperand operand);

  R processConditionOperand(ConditionOperand operand);

  R processGroupingOperand(GroupingOperand operand);
}
