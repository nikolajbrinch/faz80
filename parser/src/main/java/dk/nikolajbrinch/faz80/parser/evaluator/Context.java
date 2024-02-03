package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;

public record Context(SymbolTable symbols, Address currentAddress) {}
