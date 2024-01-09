package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.scanner.MacroTokenType;

public record GroupingPair(MacroTokenType start, MacroTokenType end) {}
