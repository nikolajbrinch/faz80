package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import java.util.List;

public interface ValuesStatement extends Statement {

  List<Expression> values();

}
