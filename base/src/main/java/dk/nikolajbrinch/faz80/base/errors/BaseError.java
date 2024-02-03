package dk.nikolajbrinch.faz80.base.errors;

public interface BaseError<T extends BaseException> {

  T exception();

}
