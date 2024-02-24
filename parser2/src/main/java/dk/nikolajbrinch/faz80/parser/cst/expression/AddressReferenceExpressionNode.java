package dk.nikolajbrinch.faz80.parser.cst.expression;

import dk.nikolajbrinch.faz80.parser.cst.NodeVisitor;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record AddressReferenceExpressionNode(AssemblerToken addressReference) implements ExpressionNode {


  @Override
  public ExpressionType expressionType() {
    return ExpressionType.ADDRESS_REFERENCE;
  }


  @Override
  public <R> R accept(NodeVisitor<R> visitor) {
    return visitor.visitAddressReferenceNode(this);
  }

}
