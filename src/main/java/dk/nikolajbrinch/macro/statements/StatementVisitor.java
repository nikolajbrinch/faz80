package dk.nikolajbrinch.macro.statements;

public interface StatementVisitor<R> {

  R visitMacroStatement(MacroStatement statement);

  R visitIdentifierStatement(IdentifierStatement statement);

  R visitTextStatement(TextStatement statement);

  R visitMacroCallStatement(MacroCallStatement statement);

  R visitIncludeStatement(IncludeStatement statement);
}
