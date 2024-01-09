package dk.nikolajbrinch.macro.preprocessor;

import dk.nikolajbrinch.assembler.compiler.Environment;
import dk.nikolajbrinch.macro.parser.Argument;
import dk.nikolajbrinch.macro.parser.Parameter;
import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.macro.scanner.MacroTokenType;
import dk.nikolajbrinch.macro.statements.TextStatement;
import dk.nikolajbrinch.macro.statements.IdentifierStatement;
import dk.nikolajbrinch.macro.statements.IncludeStatement;
import dk.nikolajbrinch.macro.statements.MacroCallStatement;
import dk.nikolajbrinch.macro.statements.MacroStatement;
import dk.nikolajbrinch.macro.statements.Statement;
import dk.nikolajbrinch.macro.statements.StatementVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MacroResolver implements StatementVisitor<String> {

  private final Environment globals = new Environment();

  private Environment environment = globals;
  private boolean hasErrors = false;

  private boolean macroMode = false;

  public List<String> resolve(List<Statement> statements) {
    List<String> resolvedStatements = new ArrayList<>();

    for (Statement statement : statements) {
      String resolved = statement.accept(this);

      if (resolved != null) {
        resolvedStatements.add(resolved);
      }
    }

    return resolvedStatements;
  }

  public boolean hasErrors() {
    return hasErrors;
  }

  @Override
  public String visitMacroStatement(MacroStatement statement) {
    globals.define(statement.name().text(), statement);

    return null;
  }

  @Override
  public String visitIdentifierStatement(IdentifierStatement statement) {
    return statement.identifier().text();
  }

  @Override
  public String visitTextStatement(TextStatement statement) {
    if (macroMode) {
      return statement.tokens().stream()
          .map(
              token -> {
                if (token.type() == MacroTokenType.IDENTIFIER) {
                  if (environment.exists(token.text())) {
                    return String.valueOf(environment.get(token.text()));
                  }
                }

                return token.text();
              })
          .collect(Collectors.joining());
    }

    return statement.tokens().stream().map(MacroToken::text).collect(Collectors.joining());
  }

  @Override
  public String visitMacroCallStatement(MacroCallStatement statement) {
    return processMacro(statement);
  }

  @Override
  public String visitIncludeStatement(IncludeStatement statement) {
    return statement.include().text()
        + statement.space().stream().map(MacroToken::text).collect(Collectors.joining())
        + statement.filename().text();
  }

  private String processMacro(MacroCallStatement statement) {
    MacroStatement macro = (MacroStatement) environment.get(statement.name().text());

    Environment macroEnvironment = new Environment(environment);

    for (int i = 0; i < Math.max(statement.arguments().size(), macro.parameters().size()); i++) {
      Argument argument = i < statement.arguments().size() ? statement.arguments().get(i) : null;
      Parameter parameter = i < macro.parameters().size() ? macro.parameters().get(i) : null;

      if (parameter != null) {
        String name = parameter.name().text();

        String defaultValue =
            parameter.defaultValue().stream().map(MacroToken::text).collect(Collectors.joining());

        if (defaultValue != null) {
          macroEnvironment.define(name, defaultValue);
        }

        if (!(defaultValue != null && argument == null)) {
          macroEnvironment.define(
              name, argument.tokens().stream().map(MacroToken::text).collect(Collectors.joining()));
        }
      }
    }

    return withEnvironment(macroEnvironment, () -> new TextStatement(macro.body()).accept(this));
  }

  private String withEnvironment(Environment environment, Supplier<String> supplier) {
    Environment previous = this.environment;

    try {
      this.macroMode = true;
      this.environment = environment;

      return supplier.get();
    } finally {
      this.macroMode = false;
      this.environment = previous;
    }
  }
}
