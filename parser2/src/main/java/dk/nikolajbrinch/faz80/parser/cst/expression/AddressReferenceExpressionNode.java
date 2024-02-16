package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.CstVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record AddressReferenceExpressionNode(AssemblerToken addressReference) implements ExpressionNode {


  @Override
  public ExpressionType expressionType() {
    return ExpressionType.ADDRESS_REFERENCE;
  }


  @Override
  public <R> R accept(CstVisitor<R> visitor) {
    return visitor.visitAddressReferenceNode(this);
  }

}
