package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.values.NumberValue;

public record Address(NumberValue logicalAddress, NumberValue physicalAddress) {

  public Address add(NumberValue numberValue) {
    return new Address(logicalAddress.add(numberValue), physicalAddress.add(numberValue));
  }
}
