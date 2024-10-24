module dk.nikolajbrinch.faz80.parser2 {
    requires dk.nikolajbrinch.faz80.base;
    requires dk.nikolajbrinch.faz80.scanner;
    requires dk.nikolajbrinch.faz80.parser.base;

    exports dk.nikolajbrinch.faz80.parser.cst;
    exports dk.nikolajbrinch.faz80.parser.cst.blocks;
    exports dk.nikolajbrinch.faz80.parser.cst.conditional;
    exports dk.nikolajbrinch.faz80.parser.cst.data;
    exports dk.nikolajbrinch.faz80.parser.cst.expression;
    exports dk.nikolajbrinch.faz80.parser.cst.instructions;
    exports dk.nikolajbrinch.faz80.parser.cst.macros;
    exports dk.nikolajbrinch.faz80.parser.cst.operands;
    exports dk.nikolajbrinch.faz80.parser.cst.scopes;
}
