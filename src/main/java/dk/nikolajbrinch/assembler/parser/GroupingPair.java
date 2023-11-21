package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.scanner.TokenType;

public record GroupingPair(TokenType start, TokenType end) {}
