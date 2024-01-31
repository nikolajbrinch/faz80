package dk.nikolajbrinch.assembler.parser.statements;

import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import java.util.List;

public interface ValuesStatement extends Statement {

  List<Expression> values();

}
