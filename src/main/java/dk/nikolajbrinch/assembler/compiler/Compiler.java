package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.util.List;

public class Compiler {

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private final IncludeResolver includeResolver = new IncludeResolver();

  private boolean hasErrors = false;

  public void compile(List<Statement> statements) {
    List<Statement> resolvedStatements = includeResolver.resolve(statements);
    hasErrors = includeResolver.hasErrors();

    if (!hasErrors) {
      MacroResolver macroResolver = new MacroResolver(expressionEvaluator);
      resolvedStatements = macroResolver.resolve(resolvedStatements);
      hasErrors = macroResolver.hasErrors();
    }

    if (!hasErrors) {
      Assembler assembler = new Assembler(expressionEvaluator);
      assembler.assemble(resolvedStatements);
      hasErrors = assembler.hasErrors();
    }
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
