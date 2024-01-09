package dk.nikolajbrinch.parser;

public interface CharReader extends Reader<Char> {

  Line getLine();

  int getPosition();

}
