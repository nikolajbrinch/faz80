package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import java.util.List;

public interface Parser {

  List<Statement> parse();
}
