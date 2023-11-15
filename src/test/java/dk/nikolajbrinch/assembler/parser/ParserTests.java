package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.compiler.Compiler;
import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.MacroResolver;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ParserTests {

  @Test
  void testParseExpression() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * a + +b:: ^ $4f << %1111111 >>> 3 & 7 | 9"""
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      Assertions.assertEquals(
          "(expression (| (^ (== (+ 10011001 (/ (* (* (- 8) 207) (group (+ 304 4))) 5a)) (+ (* 6 (identifier: IDENTIFIER[@1:52-52(a)])) (+ (identifier: IDENTIFIER[@1:57-59(b::)])))) (& (>>> (<< 4f 1111111) 3) 7)) 9))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }

  @Test
  void testParseString() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            "Hej" + 'ø' + "med dig"
            """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      Assertions.assertEquals(
          "(expression (+ (+ \"Hej\" 'ø') \"med dig\"))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }

  @Test
  void testParseIndexed() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        d set 5
        xor (ix+d)
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseMisc() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            org 0
            const equ 0x00
            var1:: set %00000001
            label1
            label::  ld a, $
            label    ld $$, d
            .align 256  ; this is a comment
            .local
            ; comments
            ld a, ($0034)
            .endlocal
            label:   ld d, e
            macro macro1 param1=0, param2=5
            label:   ld d, e
            endm
            macro2 macro param3, param4
            .endm
            rept $2 - $
            ld a, $34
            endr
            .phase $1000
            ; do this code in phase
            xor a, a ; zero a
            ; end

                        #end

            .dephase
            #if 1 == 1
            #endif
            ld a, 1 & 2
            ld b, 2 | 1
            ld c, 4 ^ 3
            ld a, 1 && '\\0'
            ld b, 2 || 1
            ld c, ~3
            ld c, '\\n'
            howdy: .byte 0x00, $12, %11111111
            """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseMacro() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            mx = 0
            .macro macro1 param1=0, param2=4, param3='\\0'



            L:
            ld a, param1
            ld b, param2
            ld c, param3
            .endm
            mx = mx + 1

            macro1(1, 2)
            """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      List<Statement> resolved = new MacroResolver(new ExpressionEvaluator()).resolve(statements);
      for (Statement statement : resolved) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseMacroCall() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro a1, a2=0
        endm
        label: set 89
        macro1 1
        macro1 <ld a, b>, <2>
        macro1 (1, 2)
        macro1 (<1>, <"string">)
        macro1 label, <>
        ; comment
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      List<Statement> resolved = new MacroResolver(new ExpressionEvaluator()).resolve(statements);
      for (Statement statement : resolved) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseRepeat() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        count1 equ 4
        rept count1
        label: set 89
        ; comment
        endr
        count2 set 5
        rept count2
        label: set 89
        ; comment
        endr
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      List<Statement> resolved = new MacroResolver(new ExpressionEvaluator()).resolve(statements);
      for (Statement statement : resolved) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseMacroCallSpecial() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
        macro1 macro p1, p2, p3, p4
        ld a, p1
        ld b, p2
        ld c, p3
        ld d, p4
        endm
        label: set 89
        macro1 <label>, <>, <<>, <>>
        """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      List<Statement> resolved = new MacroResolver(new ExpressionEvaluator()).resolve(statements);
      for (Statement statement : resolved) {
        System.out.println(new AstPrinter().print(statement));
      }

      System.out.println("-----");

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseIfElifElseEndif() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            #if 1 == 1
            var1:: set 0x1000
            #elif 2 == 2
            ld a, c
            #else
            ld a, d
            #endif
            """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParseSet() throws IOException {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(
                """
            var1:: set %00000001
            label1: set a, $1
            """
                    .getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Disabled
  @Test
  void testParseMath48() throws IOException {
    try (InputStream inputStream = ParserTests.class.getResourceAsStream("/Math48.z80");
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }
}
