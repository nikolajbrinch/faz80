package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.scanner.TokenType;
import java.util.Arrays;

public enum Grouping {
  PARENS(new GroupingPair(TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN)),
  BRACKETS(new GroupingPair(TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET)),
  BRACES(new GroupingPair(TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE));

  private final GroupingPair pair;

  private Grouping(GroupingPair pair) {
    this.pair = pair;
  }

  public static TokenType[] startTypes() {
    return Arrays.stream(values()).map(Grouping::start).toList().toArray(new TokenType[0]);
  }

  public static Grouping findByStartType(TokenType tokenType) {
    return Arrays.stream(values())
        .filter(g -> g.pair.start() == tokenType)
        .findFirst()
        .orElse(null);
  }

  public TokenType start() {
    return pair.start();
  }

  public TokenType end() {
    return pair.end();
  }
}
