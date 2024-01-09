package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Compiler {

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private boolean hasErrors = false;

  public void compile(File file) throws IOException {
    List<Statement> statements = new AssemblerParser().parse(file);

    Assembler assembler = new Assembler(expressionEvaluator);
    assembler.assemble(statements);
    hasErrors = assembler.hasErrors();
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
