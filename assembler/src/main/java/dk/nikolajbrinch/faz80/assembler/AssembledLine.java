package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.parser.statements.Statement;

public record AssembledLine(Statement statement, ByteSource byteSource) {}
