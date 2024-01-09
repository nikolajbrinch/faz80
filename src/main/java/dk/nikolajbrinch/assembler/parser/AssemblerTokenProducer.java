package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.scanner.AssemblerTokenType;
import dk.nikolajbrinch.parser.BaseScannerRegistry;
import dk.nikolajbrinch.parser.BaseTokenProducer;
import dk.nikolajbrinch.parser.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AssemblerTokenProducer extends BaseTokenProducer<AssemblerToken> {

  public AssemblerTokenProducer() {
    super(
        new BaseScannerRegistry<>() {
          @Override
          protected Scanner<AssemblerToken> createScanner(File file) throws IOException {
            return new AssemblerScanner(new FileInputStream(file));
          }
        });
  }

  @Override
  protected boolean isEof(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }
}
