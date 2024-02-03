package dk.nikolajbrinch.faz80.linker;

import dk.nikolajbrinch.faz80.base.errors.BaseException;

public class LinkException extends BaseException {

  public LinkException(String message) {
    super(message);
  }

  public LinkException(String message, Throwable cause) {
    super(message, cause);
  }

}
