package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.values.Value;

public record ValueSymbol(Value<?> value) implements Symbol<Value<?>> {}
