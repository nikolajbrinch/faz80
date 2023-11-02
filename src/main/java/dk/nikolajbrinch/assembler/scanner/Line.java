package dk.nikolajbrinch.assembler.scanner;

import java.util.List;
import java.util.stream.Collectors;

public record Line(int line, List<Token> tokens) {

  @Override
  public String toString() {
    return tokens.stream().map(Token::toString).collect(Collectors.joining(","));
  }
}
