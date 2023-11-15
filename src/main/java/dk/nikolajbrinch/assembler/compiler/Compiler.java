package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.util.List;

public class Compiler {

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private MacroResolver macroResolver;

  private Assembler assembler;

  public void compile(List<Statement> statements) {
    macroResolver = new MacroResolver(expressionEvaluator);
    List<Statement> resolvedStatements = macroResolver.resolve(statements);

    assembler = new Assembler(expressionEvaluator);
    assembler.assemble(resolvedStatements);
  }
}
