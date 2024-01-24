package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.BaseException;
import dk.nikolajbrinch.parser.Line;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

  private final Logger logger = LoggerFactory.getLogger();

  private final List<BaseError<? extends BaseException>> errors = new ArrayList<>();

  private AssemblerParser parser;
  private Assembler assembler;

  private Linker linker;

  private BlockStatement block;
  private Assembled assembled;

  private Linked linked;

  private File directory;

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public void compile(String source) throws IOException {
    parse(source);

    if (!parser.hasErrors()) {
      assemble(block);
    }

    if (!assembler.hasErrors()) {
      link(assembled);
    }
  }

  public void compile(File file) throws IOException {
    parse(file);

    if (!parser.hasErrors()) {
      assemble(block);
    }

    if (!assembler.hasErrors()) {
      link(assembled);
    }
  }

  public void parse(String source) throws IOException {
    parser = new AssemblerParser(directory);
    block = parser.parse(source);

    errors.clear();
    errors.addAll(parser.getErrors());
  }

  public void parse(File file) throws IOException {
    parser = new AssemblerParser(file.getParentFile());
    block = parser.parse(file);

    errors.clear();
    errors.addAll(parser.getErrors());
  }

  public void assemble(BlockStatement block) {
    assembler = new Assembler(new ExpressionEvaluator());
    assembled = assembler.assemble(block);
    errors.addAll(assembler.getErrors());

    if (assembler.hasErrors()) {
      assembler
          .getErrors()
          .forEach(
              error -> {
                AssembleException exception = error.exception();
                Statement statement = exception.getStatement();

                Line line = statement.line();
                logger.error(
                    "Compilation error in line %d: %s", line.number(), exception.getMessage());
                logger.error("    %s%n", line.content().strip());
              });
    }
  }

  public void link(Assembled assembled) {
    linker = new Linker();
    Linked linked = linker.link(assembled);

    this.linked = linked;
  }
  public BlockStatement getParseResult() {
    return block;
  }

  public Assembled getAssembleResult() {
    return assembled;
  }

  public Linked getLinkResult() {
    return linked;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<BaseError<? extends BaseException>> getErrors() {
    return errors;
  }

}
