package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public record ScanError(ErrorType type, AssemblerToken errorToken) {}
