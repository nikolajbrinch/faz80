package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.base.errors.BaseError;

public record AssembleError(AssembleException exception) implements BaseError<AssembleException> {}
