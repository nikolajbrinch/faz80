package dk.nikolajbrinch.assembler.linker;

import dk.nikolajbrinch.parser.BaseError;

public record LinkError(LinkException exception) implements BaseError<LinkException> {}
