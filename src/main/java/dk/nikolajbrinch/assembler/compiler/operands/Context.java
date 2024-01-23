package dk.nikolajbrinch.assembler.compiler.operands;

import dk.nikolajbrinch.assembler.compiler.Address;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;

public record Context(SymbolTable symbols, Address currentAddress) {}
