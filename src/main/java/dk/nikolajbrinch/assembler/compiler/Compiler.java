package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.BlockStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class Compiler {

  private final Logger logger = LoggerFactory.getLogger();

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private boolean hasErrors = false;

  public void compile(File file) throws IOException {
    BlockStatement block = new AssemblerParser().parse(file);

    Assembler assembler = new Assembler(expressionEvaluator);
    assembler.assemble(block);
    hasErrors = assembler.hasErrors();

    if (hasErrors) {
      assembler
          .getErrors()
          .forEach(
              error -> {
                AssembleException exception = error.exception();
                Statement statement = exception.getStatement();

                Line line = statement.line();
                logger.error(
                    "Compilation error in line %d: %s", line.number(), exception.getMessage());
                logger.error("    %s%n", line.content().strip());
              });
    }
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
