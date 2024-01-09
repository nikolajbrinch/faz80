package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.Address;

public record AddressSymbol(Address value) implements Symbol<Address> {}
