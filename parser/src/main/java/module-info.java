module dk.nikolajbrinch.faz80.parser {
  requires dk.nikolajbrinch.faz80.base;
  requires dk.nikolajbrinch.faz80.scanner;
  requires dk.nikolajbrinch.faz80.parser.base;

  exports dk.nikolajbrinch.faz80.parser;
  exports dk.nikolajbrinch.faz80.parser.evaluator;
  exports dk.nikolajbrinch.faz80.parser.expressions;
  exports dk.nikolajbrinch.faz80.parser.operands;
  exports dk.nikolajbrinch.faz80.parser.statements;
  exports dk.nikolajbrinch.faz80.parser.symbols;

}
