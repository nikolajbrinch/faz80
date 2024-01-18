package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerScanner;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.parser.ScannerSource;
import dk.nikolajbrinch.parser.SourceInfo;
import dk.nikolajbrinch.parser.BaseScannerRegistry;
import dk.nikolajbrinch.parser.BaseTokenProducer;
import dk.nikolajbrinch.parser.Scanner;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AssemblerTokenProducer extends BaseTokenProducer<AssemblerToken> {

  public AssemblerTokenProducer() {
    super(
        new BaseScannerRegistry<>() {
          @Override
          protected Scanner<AssemblerToken> createScanner(ScannerSource source)
              throws IOException {
            return new AssemblerScanner(source);
          }
        });
  }

  @Override
  protected boolean isEof(AssemblerToken token) {
    return token.type() == AssemblerTokenType.EOF;
  }
}
