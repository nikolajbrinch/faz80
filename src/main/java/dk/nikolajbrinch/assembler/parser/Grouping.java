package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import java.util.Arrays;

public enum Grouping {
  PARENS(new GroupingPair(AssemblerTokenType.LEFT_PAREN, AssemblerTokenType.RIGHT_PAREN)),
  BRACKETS(new GroupingPair(AssemblerTokenType.LEFT_BRACKET, AssemblerTokenType.RIGHT_BRACKET)),
  BRACES(new GroupingPair(AssemblerTokenType.LEFT_BRACE, AssemblerTokenType.RIGHT_BRACE));

  private final GroupingPair pair;

  Grouping(GroupingPair pair) {
    this.pair = pair;
  }

  public static AssemblerTokenType[] startTypes() {
    return Arrays.stream(values()).map(Grouping::start).toList().toArray(new AssemblerTokenType[0]);
  }

  public static Grouping findByStartType(AssemblerTokenType tokenType) {
    return Arrays.stream(values())
        .filter(g -> g.pair.start() == tokenType)
        .findFirst()
        .orElse(null);
  }

  public AssemblerTokenType start() {
    return pair.start();
  }

  public AssemblerTokenType end() {
    return pair.end();
  }
}
