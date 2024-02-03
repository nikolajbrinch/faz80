package dk.nikolajbrinch.faz80.scanner;

import java.util.List;
import java.util.stream.Collectors;

public record TokenLine(int line, List<AssemblerToken> tokens) {

  @Override
  public String toString() {
    return tokens.stream().map(AssemblerToken::toString).collect(Collectors.joining(","));
  }
}
