package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.scanner.MacroScanner;
import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.macro.scanner.MacroTokenType;
import dk.nikolajbrinch.parser.BaseScannerRegistry;
import dk.nikolajbrinch.parser.BaseTokenProducer;
import dk.nikolajbrinch.parser.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MacroTokenProducer extends BaseTokenProducer<MacroToken> {

  public MacroTokenProducer() {
    super(
        new BaseScannerRegistry<>() {
          @Override
          protected Scanner<MacroToken> createScanner(File file) throws IOException {
            return new MacroScanner(new FileInputStream(file));
          }
        });
  }

  @Override
  protected boolean isEof(MacroToken token) {
    return token.type() == MacroTokenType.EOF;
  }
}
