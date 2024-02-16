package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.Position;
import dk.nikolajbrinch.scanner.SourceInfo;
import dk.nikolajbrinch.scanner.Token;

public record AssemblerToken(
    AssemblerTokenType type,
    SourceInfo sourceInfo,
    Position position,
    Line line,
    int start,
    int end,
    String text)
    implements Token {

  @Override
  public String toString() {
    return StringUtil.escape(text()) + ": " + type();
  }

}
