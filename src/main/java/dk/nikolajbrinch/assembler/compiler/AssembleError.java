package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.parser.BaseError;

public record AssembleError(AssembleException exception) implements BaseError<AssembleException> {}
