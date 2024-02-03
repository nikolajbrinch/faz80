package dk.nikolajbrinch.scanner;

public interface CharReader extends Reader<Char> {

  Line getLine();

  int getPosition();

  int getLinePosition();
}
