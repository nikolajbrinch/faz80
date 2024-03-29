package dk.nikolajbrinch.faz80.compiler;

import dk.nikolajbrinch.faz80.assembler.AssembleResult;
import dk.nikolajbrinch.faz80.assembler.Assembled;
import dk.nikolajbrinch.faz80.assembler.Assembler;
import dk.nikolajbrinch.faz80.parser.evaluator.ExpressionEvaluator;
import dk.nikolajbrinch.faz80.linker.LinkResult;
import dk.nikolajbrinch.faz80.linker.Linker;
import dk.nikolajbrinch.faz80.parser.AssemblerParseResult;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.faz80.base.errors.BaseError;
import dk.nikolajbrinch.faz80.base.errors.BaseException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

  private final List<BaseError<? extends BaseException>> errors = new ArrayList<>();

  private AssemblerParser parser;
  private Assembler assembler;

  private Linker linker;

  private AssemblerParseResult parseResult;
  private AssembleResult assembleResult;

  private LinkResult linkResult;

  private File directory;

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public void compile(String source) throws IOException {
    parse(source);

    if (!parseResult.hasErrors()) {
      assemble(parseResult.block());
    }

    if (assembleResult != null && !assembleResult.hasErrors()) {
      link(assembleResult.assembled());
    }
  }

  public void compile(File file) throws IOException {
    parse(file);

    if (!parseResult.hasErrors()) {
      assemble(parseResult.block());
    }

    if (assembleResult != null && !assembleResult.hasErrors()) {
      link(assembleResult.assembled());
    }
  }

  public void parse(String source) throws IOException {
    parser = new AssemblerParser(directory);
    parseResult = parser.parse(source);

    errors.clear();
    errors.addAll(parser.getErrors());
  }

  public void parse(File file) throws IOException {
    parser = new AssemblerParser(file.getParentFile());
    parseResult = parser.parse(file);

    errors.clear();
    errors.addAll(parser.getErrors());
  }

  public void assemble(BlockStatement block) {
    assembler = new Assembler(new ExpressionEvaluator());
    assembleResult = assembler.assemble(block);
    errors.addAll(assembleResult.errors());
  }

  public void link(Assembled assembled) {
    linker = new Linker();
    linkResult = linker.link(assembled);
    errors.addAll(linkResult.errors());
  }

  public AssemblerParseResult getParseResult() {
    return parseResult;
  }

  public AssembleResult getAssembleResult() {
    return assembleResult;
  }

  public LinkResult getLinkResult() {
    return linkResult;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<BaseError<? extends BaseException>> getErrors() {
    return errors;
  }
}
