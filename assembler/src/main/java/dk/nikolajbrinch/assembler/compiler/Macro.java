package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.Parameter;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import java.util.List;

public record Macro(String name, List<Parameter> parameters, Statement block) {}
