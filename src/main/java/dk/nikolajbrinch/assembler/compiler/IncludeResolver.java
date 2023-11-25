package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import dk.nikolajbrinch.assembler.scanner.AssemblerScanner;
import dk.nikolajbrinch.parser.ParseException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IncludeResolver {

  private boolean hasErrors = false;

  public List<Statement> resolve(List<Statement> statements) {
    List<Statement> resolvedStatements = new ArrayList<>();

    for (Statement statement : statements) {
      if (statement instanceof IncludeStatement include) {
        resolvedStatements.addAll(include(include.string().text()));
      } else {
        resolvedStatements.add(statement);
      }
    }

    return resolvedStatements;
  }

  public boolean hasErrors() {
    return hasErrors;
  }

  private List<Statement> include(String filename) {
    try (AssemblerScanner scanner = new AssemblerScanner(new FileInputStream(filename))) {
      return new AssemblerParser(scanner).parse();
    } catch (IOException e) {
      throw new ParseException("Exception including " + filename, e);
    }
  }

}
