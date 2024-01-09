package dk.nikolajbrinch.parser.impl;

import dk.nikolajbrinch.parser.Logger;

public class LoggerFactory {

  public static Logger getLogger() {
    return LoggerImpl.INSTANCE;
  }
}
