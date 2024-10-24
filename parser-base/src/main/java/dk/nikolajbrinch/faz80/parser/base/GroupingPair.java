package dk.nikolajbrinch.faz80.parser.base;

import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;

public record GroupingPair(AssemblerTokenType start, AssemblerTokenType end) {}
