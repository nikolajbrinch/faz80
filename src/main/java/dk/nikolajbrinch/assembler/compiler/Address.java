package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;

public record Address(NumberValue logicalAddress, NumberValue physicalAddress) {

  public Address add(NumberValue numberValue) {
    return new Address(logicalAddress.add(numberValue), physicalAddress.add(numberValue));
  }

}
