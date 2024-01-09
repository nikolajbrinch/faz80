package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import dk.nikolajbrinch.macro.statements.TextStatement;
import dk.nikolajbrinch.macro.statements.IdentifierStatement;
import dk.nikolajbrinch.macro.statements.IncludeStatement;
import dk.nikolajbrinch.macro.statements.MacroCallStatement;
import dk.nikolajbrinch.macro.statements.MacroStatement;
import dk.nikolajbrinch.macro.statements.Statement;
import dk.nikolajbrinch.macro.statements.StatementVisitor;
import java.util.stream.Collectors;

public class MacroAstPrinter implements StatementVisitor<String> {

  public String print(Statement statement) {
    return statement.accept(this);
  }

  @Override
  public String visitMacroStatement(MacroStatement statement) {
    return String.format(
        "Macro: [%s], [%s]%nLine end: [%s]%nBody: [%s]",
        statement.name().text(),
        statement.parameters().stream()
            .map(
                parameter ->
                    String.format(
                        "%s=%s",
                        parameter.name().text(),
                        parameter.defaultValue().stream()
                            .map(MacroToken::text)
                            .collect(Collectors.joining())))
            .collect(Collectors.joining(", ")),
        statement.lineEnd().stream().map(MacroToken::text).collect(Collectors.joining()),
        statement.body().stream().map(MacroToken::text).collect(Collectors.joining()));
  }

  @Override
  public String visitIdentifierStatement(IdentifierStatement statement) {
    return String.format("Identifier: [%s]", statement.identifier().text());
  }

  @Override
  public String visitTextStatement(TextStatement statement) {
    return String.format(
        "Block: [%s]",
        statement.tokens().stream().map(MacroToken::text).collect(Collectors.joining()));
  }

  @Override
  public String visitMacroCallStatement(MacroCallStatement statement) {
    return String.format(
        "Macro call: [%s], [%s]",
        statement.name().text(),
        statement.arguments().stream()
            .map(
                argument ->
                    argument.tokens().stream()
                        .map(token -> token.text())
                        .collect(Collectors.joining()))
            .collect(Collectors.joining(", ")));
  }

  @Override
  public String visitIncludeStatement(IncludeStatement statement) {
    return String.format("Include: %s", statement.filename().text());
  }
}
