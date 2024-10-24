package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import java.util.List;

public sealed interface ValuesStatement extends Statement
    permits DataByteStatement, DataLongStatement, DataTextStatement, DataWordStatement {

  List<Expression> values();
}
