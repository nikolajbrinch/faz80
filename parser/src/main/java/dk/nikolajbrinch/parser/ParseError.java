package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.faz80.base.errors.BaseError;

public record ParseError(ParseException exception) implements BaseError<ParseException> {}
