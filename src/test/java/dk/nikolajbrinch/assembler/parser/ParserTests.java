package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.parser.compiler.Compiler;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.util.AstPrinter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParserTests {

  @Test
  void testParse1() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        """
            0b10011001 + -8 * 0207 * (304 + 4) / 0x5a == 0o6 * a + +b:: ^ $4f << %1111111 >>> 3 & 7 | 9""".getBytes(
            StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      Assertions.assertEquals(
          "(statement (== (+ 10011001 (/ (* (* (- 8) 207) (group (+ 304 4))) 5a)) (| (^ (+ (* 6 a) (+ b::)) (& (>>> (<< 4f 1111111) 3) 7)) 9)))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }

  @Test
  void testParse2() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        """
            "Hej" + 'ø' + "med dig"
            """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      Assertions.assertEquals(
          "(statement (+ (+ \"Hej\" 'ø') \"med dig\"))",
          new AstPrinter().print(statements.get(0)));

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }
    }
  }

  @Test
  void testParse3() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
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
            """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParse4() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
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
            """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParse5() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        """
            #if 1 == 1
            var1:: set 0x1000
            #elif 2 == 2
            ld a, c
            #else
            ld a, d
            #endif
            """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParse6() throws IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        """
            var1:: set %00000001
            label1: set a, $1
            """.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream)) {

      List<Statement> statements = new Parser(scanner).parse();

      for (Statement statement : statements) {
        System.out.println(new AstPrinter().print(statement));
      }

      new Compiler().compile(statements);
    }
  }

  @Test
  void testParse7() throws IOException {
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
