package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.Position;
import dk.nikolajbrinch.scanner.SourceInfo;
import dk.nikolajbrinch.scanner.Token;

/**
 * Represents a token in the assembler source code.
 *
 * @param type the type of the token
 * @param sourceInfo the source information
 * @param position the position of the token in the source
 * @param line the line of the token
 * @param startColumn the start position of the token in the line
 * @param endColumn the end position of the token in the line
 * @param text the text of the token
 */
public record AssemblerToken(
    AssemblerTokenType type,
    SourceInfo sourceInfo,
    Position position,
    Line line,
    int startColumn,
    int endColumn,
    String text)
    implements Token {

  @Override
  public String toString() {
    return StringUtil.escape(text()) + ": " + type();
  }

  public static final AssemblerToken NONE =
      new AssemblerToken(AssemblerTokenType.NONE, SourceInfo.NONE, Position.NONE, Line.NONE, -1, -1, null);
}
