package dk.nikolajbrinch.scanner;

public interface CharReader extends Reader<Char> {

  Line getCurrentLine();

  int getCurrentPosition();

  int getCurrentColumn();

  int getLineCount();

  void resetLine();
}
