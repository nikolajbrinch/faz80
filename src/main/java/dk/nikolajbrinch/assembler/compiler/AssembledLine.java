package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.statements.Statement;

public record AssembledLine(Statement statement, ByteSource byteSource) {}
