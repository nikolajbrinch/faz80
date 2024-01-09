package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.assembler.scanner.AssemblerTokenType;
import dk.nikolajbrinch.macro.scanner.MacroTokenType;
import java.util.Arrays;

public enum Grouping {
  PARENS(new GroupingPair(MacroTokenType.LEFT_PAREN, MacroTokenType.RIGHT_PAREN)),
  BRACKETS(new GroupingPair(MacroTokenType.LEFT_BRACKET, MacroTokenType.RIGHT_BRACKET)),
  BRACES(new GroupingPair(MacroTokenType.LEFT_BRACE, MacroTokenType.RIGHT_BRACE)),
  ANGLE(new GroupingPair(MacroTokenType.LESS, MacroTokenType.GREATER));

  private final GroupingPair pair;

  Grouping(GroupingPair pair) {
    this.pair = pair;
  }

  public static MacroTokenType[] startTypes() {
    return Arrays.stream(values()).map(Grouping::start).toList().toArray(new MacroTokenType[0]);
  }

  public static Grouping findByStartType(MacroTokenType tokenType) {
    return Arrays.stream(values())
        .filter(g -> g.pair.start() == tokenType)
        .findFirst()
        .orElse(null);
  }

  public MacroTokenType start() {
    return pair.start();
  }

  public MacroTokenType end() {
    return pair.end();
  }
}
