package dk.nikolajbrinch.parser;

public record ParseError(ParseException exception) implements BaseError<ParseException> {}
