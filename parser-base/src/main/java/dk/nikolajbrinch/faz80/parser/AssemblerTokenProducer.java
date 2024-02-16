package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.scanner.AssemblerScanner;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.scanner.BaseScannerRegistry;
import dk.nikolajbrinch.scanner.BaseTokenProducer;
import dk.nikolajbrinch.scanner.Scanner;
import dk.nikolajbrinch.scanner.ScannerSource;
import java.io.File;

public class AssemblerTokenProducer extends BaseTokenProducer<AssemblerToken> {

  public AssemblerTokenProducer(File directory) {
    super(
        new BaseScannerRegistry<>(directory) {
          @Override
          protected Scanner<AssemblerToken> createScanner(ScannerSource source) {
            return new AssemblerScanner(source);
          }
        });
  }

  @Override
  protected boolean isEof(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }

}
