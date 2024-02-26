package dk.nikolajbrinch.scanner;

public interface CharReader extends Reader<Char> {

  Line getLine();

  int getPosition();

  int getColumn();

  int getLineCount();

  void resetLine();
}
