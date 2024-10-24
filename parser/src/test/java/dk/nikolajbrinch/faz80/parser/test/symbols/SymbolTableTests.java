package dk.nikolajbrinch.faz80.parser.test.symbols;

import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import dk.nikolajbrinch.faz80.parser.symbols.UndefinedSymbolException;
import dk.nikolajbrinch.faz80.parser.symbols.WrongSymbolTypeException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SymbolTableTests {

  @Test
  void testSimple() throws Exception {
    SymbolTable symbolTable = new SymbolTable();

    symbolTable.define("symbol", SymbolType.CONSTANT);
    symbolTable.assign("symbol", SymbolType.CONSTANT, Optional.of(NumberValue.create(100L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(100L, ((NumberValue) symbolTable.get("symbol").get()).value());

    symbolTable.assign("symbol", SymbolType.CONSTANT, Optional.of(NumberValue.create(200L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(200L, ((NumberValue) symbolTable.get("symbol").get()).value());

    Assertions.assertTrue(symbolTable.exists("symbol"));
  }

  @Test
  void testScope() throws Exception {
    SymbolTable parent = new SymbolTable();
    SymbolTable symbolTable = new SymbolTable(parent);

    parent.define("symbol", SymbolType.CONSTANT);
    parent.assign("symbol", SymbolType.CONSTANT, Optional.of(NumberValue.create(100L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(100L, ((NumberValue) symbolTable.get("symbol").get()).value());

    symbolTable.assign("symbol", SymbolType.CONSTANT, Optional.of(NumberValue.create(200L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(200L, ((NumberValue) symbolTable.get("symbol").get()).value());

    Assertions.assertTrue(parent.exists("symbol"));
    Assertions.assertTrue(symbolTable.exists("symbol"));
  }

  @Test
  void testUndefinedSymbol() throws Exception {
    SymbolTable symbolTable = new SymbolTable();

    Assertions.assertThrows(UndefinedSymbolException.class, () -> symbolTable.get("symbol"));

    Assertions.assertThrows(
        UndefinedSymbolException.class,
        () ->
            symbolTable.assign(
                "symbol", SymbolType.CONSTANT, Optional.of(NumberValue.create(200L))));
  }

  @Test
  void testWrongSymbolType() throws Exception {
    SymbolTable symbolTable = new SymbolTable();

    symbolTable.define("symbol", SymbolType.CONSTANT);

    Assertions.assertThrows(
        WrongSymbolTypeException.class,
        () ->
            symbolTable.assign(
                "symbol", SymbolType.VARIABLE, Optional.of(NumberValue.create(200L))));
  }
}
