package dk.nikolajbrinch.faz80.compiler;

import dk.nikolajbrinch.faz80.assembler.AssembleResult;
import dk.nikolajbrinch.faz80.assembler.Assembled;
import dk.nikolajbrinch.faz80.assembler.Assembler;
import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.linker.LinkResult;
import dk.nikolajbrinch.faz80.linker.Linker;
import dk.nikolajbrinch.faz80.parser.AssemblerAnalyzer;
import dk.nikolajbrinch.faz80.parser.AssemblerParseResult;
import dk.nikolajbrinch.faz80.parser.AssemblerParser;
import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.evaluator.ExpressionEvaluator;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

  private final Logger logger = LoggerFactory.getLogger();

  private final List<BaseMessage> messages = new ArrayList<>();

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
    long currentTime = System.currentTimeMillis();
    parser = new AssemblerParser(directory);
    parseResult = parser.parse(source);
    logger.debug("Parsing took: " + (System.currentTimeMillis() - currentTime) + "ms");

    currentTime = System.currentTimeMillis();
    parseResult = new AssemblerAnalyzer().analyze(parseResult);
    logger.debug("Analyzing took: " + (System.currentTimeMillis() - currentTime) + "ms");

    messages.clear();
    messages.addAll(parseResult.messages());
  }

  public void parse(File file) throws IOException {
    long currentTime = System.currentTimeMillis();
    parser = new AssemblerParser(file.getParentFile());
    parseResult = parser.parse(file);
    logger.debug("Parsing took: " + (System.currentTimeMillis() - currentTime) + "ms");

    currentTime = System.currentTimeMillis();
    parseResult = new AssemblerAnalyzer().analyze(parseResult);
    logger.debug("Analyzing took: " + (System.currentTimeMillis() - currentTime) + "ms");

    messages.clear();
    messages.addAll(parseResult.messages());
  }

  public void assemble(BlockStatement block) {
    long currentTime = System.currentTimeMillis();
    assembler = new Assembler(new ExpressionEvaluator());
    assembleResult = assembler.assemble(block);
    messages.addAll(assembleResult.messages());
    logger.debug("Assembling took: " + (System.currentTimeMillis() - currentTime) + "ms");
  }

  public void link(Assembled assembled) {
    long currentTime = System.currentTimeMillis();
    linker = new Linker();
    linkResult = linker.link(assembled);
    messages.addAll(linkResult.messages());
    logger.debug("Linking took: " + (System.currentTimeMillis() - currentTime) + "ms");
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
    return messages.stream().anyMatch(BaseMessage::isError);
  }

  public List<BaseMessage> getMessages() {
    return messages;
  }
}
