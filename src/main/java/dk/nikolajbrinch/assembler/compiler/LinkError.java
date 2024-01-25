package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.parser.BaseError;

public record LinkError(LinkException exception) implements BaseError<LinkException> {}
