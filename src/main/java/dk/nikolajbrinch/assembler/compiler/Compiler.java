package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.util.List;

public class Compiler {

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private final IncludeResolver includeResolver = new IncludeResolver();

  public void compile(List<Statement> statements) {
    List<Statement> resolvedStatements = includeResolver.resolve(statements);

    MacroResolver macroResolver = new MacroResolver(expressionEvaluator);
    resolvedStatements = macroResolver.resolve(resolvedStatements);

    Assembler assembler = new Assembler(expressionEvaluator);
    assembler.assemble(resolvedStatements);
  }
}
