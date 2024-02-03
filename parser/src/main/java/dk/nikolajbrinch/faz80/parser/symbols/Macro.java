package dk.nikolajbrinch.faz80.parser.symbols;

import dk.nikolajbrinch.faz80.parser.Parameter;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import java.util.List;

public record Macro(String name, List<Parameter> parameters, Statement block) {}
