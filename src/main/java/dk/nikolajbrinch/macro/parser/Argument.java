package dk.nikolajbrinch.macro.parser;

import dk.nikolajbrinch.macro.scanner.MacroToken;
import java.util.List;

public record Argument(List<MacroToken> tokens) {}
