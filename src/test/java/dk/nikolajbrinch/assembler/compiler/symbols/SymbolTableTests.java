package dk.nikolajbrinch.assembler.compiler.symbols;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SymbolTableTests {

  @Test
  void testSimple() throws Exception {
    SymbolTable symbolTable = new SymbolTable();

    symbolTable.define("symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(100L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(100L, ((NumberValue) symbolTable.get("symbol").value()).value());

    symbolTable.assign("symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(200L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(200L, ((NumberValue) symbolTable.get("symbol").value()).value());

    Assertions.assertTrue(symbolTable.exists("symbol"));
  }

  @Test
  void testScope() throws Exception {
    SymbolTable parent = new SymbolTable();
    SymbolTable symbolTable = new SymbolTable(parent);

    parent.define("symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(100L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(100L, ((NumberValue) symbolTable.get("symbol").value()).value());

    symbolTable.assign("symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(200L)));

    Assertions.assertNotNull(symbolTable.get("symbol"));
    Assertions.assertEquals(200L, ((NumberValue) symbolTable.get("symbol").value()).value());

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
                "symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(200L))));
  }

  @Test
  void testWrongSymbolType() throws Exception {
    SymbolTable symbolTable = new SymbolTable();

    symbolTable.define("symbol", SymbolType.CONSTANT, new ValueSymbol(NumberValue.create(100L)));

    Assertions.assertThrows(
        WrongSymbolTypeException.class,
        () ->
            symbolTable.assign(
                "symbol", SymbolType.VARIABLE, new ValueSymbol(NumberValue.create(200L))));
  }
}
