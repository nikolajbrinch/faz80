package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.parser.BaseScannerRegistry;
import dk.nikolajbrinch.parser.BaseTokenProducer;
import dk.nikolajbrinch.parser.Scanner;
import dk.nikolajbrinch.parser.ScannerSource;
import java.io.File;
import java.io.IOException;

public class AssemblerTokenProducer extends BaseTokenProducer<AssemblerToken> {

  public AssemblerTokenProducer(File directory) {
    super(
        new BaseScannerRegistry<>(directory) {
          @Override
          protected Scanner<AssemblerToken> createScanner(ScannerSource source) throws IOException {
            return new AssemblerScanner(source);
          }
        });
  }

  @Override
  protected boolean isEof(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }
}
