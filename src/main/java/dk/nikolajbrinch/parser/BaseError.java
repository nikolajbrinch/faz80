package dk.nikolajbrinch.parser;

public interface BaseError<T extends BaseException> {

  T exception();

}
