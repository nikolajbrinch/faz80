package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;

public record GroupingPair(AssemblerTokenType start, AssemblerTokenType end) {}
