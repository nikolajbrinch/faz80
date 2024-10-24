package dk.nikolajbrinch.faz80.parser.expressions;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public sealed interface Expression
    permits BinaryExpression,
        UnaryExpression,
        GroupingExpression,
        IdentifierExpression,
        AddressExpression,
        MacroCallExpression,
        NumberExpression,
        StringExpression {

  SourceInfo sourceInfo();

  Line line();
}
