package dk.nikolajbrinch.faz80.linker;

import dk.nikolajbrinch.faz80.base.errors.BaseError;

public record LinkError(LinkException exception) implements BaseError<LinkException> {}
