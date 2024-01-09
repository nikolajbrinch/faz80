package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.Macro;

public record MacroSymbol(Macro value) implements Symbol<Macro> {}
