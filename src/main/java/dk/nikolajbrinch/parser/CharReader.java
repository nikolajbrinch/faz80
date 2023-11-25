package dk.nikolajbrinch.parser;

public interface CharReader extends Reader<Char> {

  int getLine();

  int getPosition();

}
