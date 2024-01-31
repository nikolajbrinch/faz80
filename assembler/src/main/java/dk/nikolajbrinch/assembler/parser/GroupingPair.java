package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;

public record GroupingPair(AssemblerTokenType start, AssemblerTokenType end) {}
