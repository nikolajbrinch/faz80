package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.base.AssemblerTokenProducer;
import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.parser.base.Grouping;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatNode;
import dk.nikolajbrinch.faz80.parser.cst.blocks.RepeatStartNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ElseNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.EndIfNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.IfNode;
import dk.nikolajbrinch.faz80.parser.cst.data.DataNode;
import dk.nikolajbrinch.faz80.parser.cst.data.DataType;
import dk.nikolajbrinch.faz80.parser.cst.expression.AddressReferenceExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.BinaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.ExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.GroupingExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.IdentifierExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralNumberExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.LiteralStringExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.expression.UnaryExpressionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AlignmentNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.AssertionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.ConstantNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.EndNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.GlobalNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.IncludeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InsertNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.InstructionNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.MacroCallNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OpcodeNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.OriginNode;
import dk.nikolajbrinch.faz80.parser.cst.instructions.VariableNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.MacroSymbolNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.macros.ParametersNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.symbols.SymbolInfo;
import dk.nikolajbrinch.faz80.parser.cst.symbols.SymbolType;
import dk.nikolajbrinch.faz80.parser.cst.symbols.Symbols;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.faz80.scanner.Mode;
import dk.nikolajbrinch.scanner.ScannerSource;
import dk.nikolajbrinch.scanner.TokenProducer;
import dk.nikolajbrinch.scanner.impl.FileSource;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Parser {

  private final Logger logger = LoggerFactory.getLogger();

  private static final Set<Mnemonic> CONDITIONAL_INSTRUCTIONS =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);

  private final TokenProducer<AssemblerToken> tokenProducer;

  private final Symbols globals = new Symbols(null);

  private Symbols symbols = globals;

  private final ParserConfiguration configuration;

  public Parser() {
    this(null, new ParserConfiguration());
  }

  public Parser(ParserConfiguration configuration) {
    this(null, configuration);
  }

  public Parser(File baseDirectory) {
    this(baseDirectory, new ParserConfiguration());
  }

  public Parser(File baseDirectory, ParserConfiguration configuration) {
    this.tokenProducer = new AssemblerTokenProducer(baseDirectory);
    this.configuration = configuration;
  }

  public ProgramNode parse(File file) throws IOException {
    newSource(new FileSource(file));

    return parse();
  }

  public ProgramNode parse(String source) throws IOException {
    newSource(new StringSource(source));

    return parse();
  }

  private ProgramNode parse() {
    return new ProgramNode(globals, new LinesNode(parseLines(this::isEof)));
  }

  private List<LineNode> parseLines(BooleanSupplier predicate) {
    List<LineNode> lines = new ArrayList<>();

    while (!predicate.getAsBoolean()) {
      LineNode line = parseLine();

      if (line != null) {
        lines.add(line);
      }
    }

    return lines;
  }

  public LineNode parseLine() {
    try {
      LabelNode label = label();

      return line(label);
    } catch (ParseException e) {
      logger.error(e.getMessage());
      sync();
    }

    return null;
  }

  private LabelNode label() {
    AssemblerToken token = peek();

    LabelNode label = null;

    if (token.type() == AssemblerTokenType.IDENTIFIER && (isLabel(token))) {
      label = new LabelNode(nextToken());

      String labelName = label.label().text();

      symbols.define(labelName, SymbolType.LABEL);
    }

    return label;
  }

  private LineNode line(LabelNode label) {
    AssemblerToken token = peek();

    return switch (token.type()) {
      case SECTION -> section();
      case INCLUDE -> include();
      case MACRO -> macro(label);
      case REPEAT -> repeat(AssemblerTokenType.ENDREPEAT);
      case DUPLICATE -> repeat(AssemblerTokenType.ENDDUPLICATE);
      case LOCAL -> local();
      case PHASE -> phase();
      case IF, ELSE_IF -> conditional();
      default -> instructionLine(label);
    };
  }

  private LineNode instructionLine(LabelNode label) {
    return new BasicLineNode(label, instruction(label), comment(), newline());
  }

  private InstructionNode instruction(LabelNode label) {
    AssemblerToken token = peek();

    return switch (token.type()) {
      case IDENTIFIER -> identifier();
      case ORIGIN -> origin();
      case ALIGN -> align();
      case INSERT -> insert();
      case DATA_BYTE -> dataByte();
      case DATA_WORD_LE -> dataWordLe();
      case DATA_WORD_BE -> dataWordBe();
      case DATA_LONG -> dataLong();
      case DATA_TEXT -> dataText();
      case DATA_BLOCK -> dataBlock();
      case DATA -> data();
      case ELSE -> _else();
      case ENDIF -> endIf();
      case ASSERT -> assertion();
      case GLOBAL -> global();
      case SET -> set(label);
      case CONSTANT -> constant(label);
      case ASSIGN, EQUAL -> variable(label);
      case DIRECTIVE -> directive();
      case END -> end();
      case ENDMACRO -> endMacro();
      case ENDREPEAT -> endRepeat();
      case ENDDUPLICATE -> endRepeat();
      case ENDLOCAL -> endLocal();
      case DEPHASE -> dePhase();
      default -> empty();
    };
  }

  private CommentNode comment() {
    if (checkType(AssemblerTokenType.COMMENT)) {
      return new CommentNode(nextToken());
    }

    return null;
  }

  private NewlineNode newline() {
    AssemblerToken newline;

    if (isEof()) {
      newline = nextToken();
    } else {
      newline = expect(AssemblerTokenType.NEWLINE);
    }

    return new NewlineNode(newline);
  }

  private void sync() {
    while (!isEof() && !checkType(AssemblerTokenType.NEWLINE)) {
      nextToken();
    }

    expect(AssemblerTokenType.NEWLINE);
  }

  private InstructionNode empty() {
    return new EmptyNode();
  }

  private InstructionNode origin() {
    AssemblerToken token = nextToken();

    ExpressionNode location = expression();

    ExpressionNode fillByte = null;

    if (isComma()) {
      fillByte = expression();
    }

    return new OriginNode(token, location, fillByte);
  }

  private InstructionNode align() {
    AssemblerToken token = nextToken();

    ExpressionNode alignment = expression();

    ExpressionNode fillByte = null;

    if (isComma()) {
      fillByte = expression();
    }

    return new AlignmentNode(token, alignment, fillByte);
  }

  private LineNode section() {
    AssemblerToken token = nextToken();
    AssemblerToken name = nextToken();

    SectionNode sectionNode = new SectionNode(token, name);

    return newBasicLine(sectionNode);
 }

  private LineNode include() {
    IncludeNode includeNode = new IncludeNode(nextToken(), expression());
    LineNode lineNode = newBasicLine(includeNode);

    if (configuration.isResolveIncludes()) {
      String filename =
          ((LiteralStringExpressionNode) includeNode.expression()).stringLiteral().text();
      try {
        newSource(StringUtil.unquote(filename));
      } catch (IOException e) {
        throw new ParseException("Error including: " + filename, e);
      }
    } else {
      return lineNode;
    }

    return null;
  }

  private InstructionNode insert() {
    return new InsertNode(nextToken(), expression());
  }

  private InstructionNode dataByte() {
    return new DataNode(DataType.BYTE, nextToken(), expressions());
  }

  private InstructionNode dataWordLe() {
    return new DataNode(DataType.WORD_LE, nextToken(), expressions());
  }

  private InstructionNode dataWordBe() {
    return new DataNode(DataType.WORD_BE, nextToken(), expressions());
  }

  private InstructionNode dataLong() {
    return new DataNode(DataType.LONG, nextToken(), expressions());
  }

  private InstructionNode dataText() {
    return new DataNode(DataType.TEXT, nextToken(), expressions());
  }

  private InstructionNode dataBlock() {
    AssemblerToken token = expect(AssemblerTokenType.DATA_BLOCK);

    ExpressionNode expression = expression();

    ExpressionNode value = null;

    if (isComma()) {
      value = expression();
    }

    return new SpaceNode(token, expression, value);
  }

  private InstructionNode data() {
    AssemblerToken token = expect(AssemblerTokenType.DATA);

    return new SpaceNode(token, expression(), null);
  }

  private InstructionNode directive() {
    AssemblerToken token = expect(AssemblerTokenType.DIRECTIVE);

    throw new ParseException("Unknown directive: " + token.text());
  }

  private RepeatNode repeat(AssemblerTokenType endToken) {
    LineNode start = newBasicLine(new RepeatStartNode(nextToken(), expression()));
    List<LineNode> lines = parseLines(() -> checkType(endToken));
    LineNode end = parseLine();

    return new RepeatNode(start, new LinesNode(lines), end);
  }

  private InstructionNode endRepeat() {
    return new RepeatEndNode(nextToken());
  }

  private LocalNode local() {
    return nestSymbols(
        () -> {
          LineNode startLine = newBasicLine(new LocalStartNode(nextToken()));
          List<LineNode> lines = parseLines(() -> checkType(AssemblerTokenType.ENDLOCAL));
          LineNode endLine = parseLine();

          return new LocalNode(symbols, startLine, new LinesNode(lines), endLine);
        });
  }

  private InstructionNode endLocal() {
    return new LocalEndNode(nextToken());
  }

  private PhaseNode phase() {
    LineNode start = newBasicLine(new PhaseStartNode(nextToken(), expression()));
    List<LineNode> lines = parseLines(() -> checkType(AssemblerTokenType.DEPHASE));
    LineNode end = parseLine();

    return new PhaseNode(start, new LinesNode(lines), end);
  }

  private InstructionNode dePhase() {
    return new PhaseEndNode(nextToken());
  }

  private ConditionalNode conditional() {
    AssemblerToken token = nextToken();

    LineNode ifNode =
        switch (token.type()) {
          case IF -> newBasicLine(new IfNode(token, expression()));
          case ELSE_IF -> newBasicLine(new ElseIfNode(token, expression()));
          default -> null;
        };

    List<LineNode> thenLines =
        parseLines(
            () ->
                match(
                    AssemblerTokenType.ENDIF, AssemblerTokenType.ELSE, AssemblerTokenType.ELSE_IF));

    return switch (peek().type()) {
      case ENDIF -> new ConditionalNode(ifNode, new LinesNode(thenLines), null, null, parseLine());
      case ELSE ->
          new ConditionalNode(
              ifNode,
              new LinesNode(thenLines),
              parseLine(),
              new LinesNode(parseLines(() -> match(AssemblerTokenType.ENDIF))),
              parseLine());
      case ELSE_IF ->
          new ConditionalNode(ifNode, new LinesNode(thenLines), parseLine(), null, null);
      default -> null;
    };
  }

  private InstructionNode _else() {
    return new ElseNode(expect(AssemblerTokenType.ELSE));
  }

  private InstructionNode endIf() {
    return new EndIfNode(expect(AssemblerTokenType.ENDIF));
  }

  private <R> R nestSymbols(Supplier<R> supplier) {
    Symbols previousSymbols = symbols;

    symbols = new Symbols(previousSymbols);
    try {
      return supplier.get();
    } finally {
      symbols = previousSymbols;
    }
  }

  private InstructionNode assertion() {
    return new AssertionNode(nextToken(), expression());
  }

  private InstructionNode global() {
    AssemblerToken token = nextToken();
    AssemblerToken identifier = nextToken();
    globals.define(identifier.text(), SymbolType.GLOBAL);

    return new GlobalNode(token, identifier);
  }

  private InstructionNode identifier() {
    AssemblerToken identifier = nextToken();

    if (isMacroCall(identifier)) {
      return macroCall(identifier);
    }

    if (isOpcode(identifier)) {
      return opcode(identifier);
    }

    return null;
  }

  private InstructionNode opcode(AssemblerToken mnemonic) {
    List<OperandNode> operands = new ArrayList<>();

    while (isNotEol()) {
      operands.add(operand(mnemonic));

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return new OpcodeNode(mnemonic, operands);
  }

  private OperandNode operand(AssemblerToken mnemonic) {
    if (isGroupingStart()) {
      AssemblerToken groupStart = nextToken();
      Grouping grouping = Grouping.findByStartType(groupStart.type());
      OperandNode operand = operand(mnemonic);
      AssemblerTokenType endType = grouping.end();
      AssemblerToken groupEnd = expect(endType);

      return new GroupingOperandNode(groupStart, operand, groupEnd);
    }

    if (isCondition(mnemonic)) {
      return condition();
    }

    if (isRegister()) {
      return register();
    }

    return new ExpressionOperandNode(expression());
  }

  private RegisterOperandNode register() {
    AssemblerToken registerToken = nextToken();

    AssemblerToken operator = null;
    ExpressionNode displacement = null;

    if (match(AssemblerTokenType.PLUS, AssemblerTokenType.MINUS)) {
      operator = nextToken();

      displacement = expression();
    }

    return new RegisterOperandNode(registerToken, operator, displacement);
  }

  private ConditionOperandNode condition() {
    return new ConditionOperandNode(nextToken());
  }

  private boolean isRegister() {
    return Register.find(peek().text()) != null;
  }

  private boolean isCondition(AssemblerToken mnemonic) {
    Mnemonic foundMnemonic = Mnemonic.find(mnemonic.text());

    if (CONDITIONAL_INSTRUCTIONS.contains(foundMnemonic)) {
      return Condition.find(peek().text()) != null;
    }

    return false;
  }

  private boolean isGroupingStart() {
    return match(Grouping.startTypes());
  }

  private boolean isComma() {
    return checkType(AssemblerTokenType.COMMA);
  }

  private BlockNode<? extends Node> macro(LabelNode label) {
    AssemblerToken token = nextToken();
    AssemblerToken name = label == null ? nextToken() : label.label();

    String macroName = name.text();

    symbols.define(macroName, SymbolType.MACRO);

    AssemblerToken extraToken = null;
    if (checkType(AssemblerTokenType.COMMA)) {
      extraToken = nextToken();
    }

    LineNode startLine = newBasicLine(new MacroStartNode(token, name, extraToken, parameters()));

    MacroNode macroNode = null;

    if (configuration.isExpandMacros()) {
      tokenProducer.setMode(Mode.MACRO_BODY);
      TextNode textNode = new TextNode(expect(AssemblerTokenType.TEXT));
      tokenProducer.setMode(Mode.NORMAL);
      LineNode endLine = parseLine();

      MacroSymbolNode macroSymbolNode = new MacroSymbolNode(startLine, textNode, endLine);
      symbols.assign(macroName, macroSymbolNode);
    } else {
      List<LineNode> lines = null;
      lines = parseLines(() -> checkType(AssemblerTokenType.ENDMACRO));
      LineNode endLine = parseLine();

      macroNode = new MacroNode(startLine, new LinesNode(lines), endLine);
    }

    return macroNode;
  }

  private ParametersNode parameters() {
    List<ParameterNode> parameters = new ArrayList<>();

    while (isNotEol()) {
      parameters.add(parameter());

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return new ParametersNode(null, parameters, null);
  }

  private ParameterNode parameter() {
    AssemblerToken name = nextToken();

    ExpressionNode expression = null;

    if (checkType(AssemblerTokenType.EQUAL)) {
      nextToken();
      expression = expression();
    }

    return new ParameterNode(name, expression);
  }

  private InstructionNode endMacro() {
    return new MacroEndNode(nextToken());
  }

  private InstructionNode macroCall(AssemblerToken identifier) {
    ArgumentsNode argumentsNode;

    if (isGroupingStart()) {
      AssemblerToken groupStart = nextToken();
      Grouping grouping = Grouping.findByStartType(groupStart.type());
      AssemblerTokenType endType = grouping.end();
      List<ArgumentNode> arguments = arguments(endType);
      AssemblerToken groupEnd = expect(endType);

      argumentsNode = new ArgumentsNode(groupStart, arguments, groupEnd);
    } else {
      argumentsNode = new ArgumentsNode(null, arguments(null), null);
    }

    MacroCallNode macroCallNode = new MacroCallNode(identifier, argumentsNode);

    if (configuration.isExpandMacros()) {
      expandMacro(macroCallNode, symbols);
      return null;
    } else {
      symbols.reference(identifier.text(), symbols.lookup(identifier.text()));
    }

    return macroCallNode;
  }

  private void expandMacro(MacroCallNode macroCallNode, Symbols symbols) {
    String text = macroCallNode.name().text();
    Optional<Node> node = symbols.get(text);


  }

  private List<ArgumentNode> arguments(AssemblerTokenType groupingEnd) {
    List<ArgumentNode> arguments = new ArrayList<>();

    while (isNotEol()) {
      arguments.add(argument(groupingEnd));

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return arguments;
  }

  private ArgumentNode argument(AssemblerTokenType groupingEnd) {
    List<AssemblerToken> tokens = new ArrayList<>();

    AssemblerToken boundsStart = null;

    tokenProducer.setMode(Mode.MACRO_ARGUMENT);

    try {
      if (checkType(AssemblerTokenType.LESS)) {
        boundsStart = nextToken();
      }

      while (isNotEol()) {
        if (boundsStart == null && checkType(AssemblerTokenType.COMMA)) {
          break;
        }

        if (boundsStart != null
            && (checkType(AssemblerTokenType.GREATER)
                && match(
                    peek(2),
                    AssemblerTokenType.COMMA,
                    AssemblerTokenType.COMMENT,
                    AssemblerTokenType.NEWLINE))) {
          break;
        }

        if (boundsStart != null
            && (checkType(AssemblerTokenType.GREATER)
                && groupingEnd != null
                && match(peek(2), groupingEnd)
                && match(peek(3), AssemblerTokenType.COMMENT, AssemblerTokenType.NEWLINE))) {
          break;
        }

        if (groupingEnd != null
            && checkType(groupingEnd)
            && match(peek(2), AssemblerTokenType.COMMENT, AssemblerTokenType.NEWLINE)) {
          break;
        }

        tokens.add(nextToken());
      }

      AssemblerToken boundsEnd = null;
      if (boundsStart != null) {
        boundsEnd = expect(AssemblerTokenType.GREATER);
      }

      return new ArgumentNode(boundsStart, tokens, boundsEnd);
    } finally {
      tokenProducer.setMode(Mode.NORMAL);
    }
  }

  private InstructionNode constant(LabelNode label) {
    if (label == null) {
      throw new ParseException("Constant expects a label");
    }

    String constantName = label.label().text();

    symbols.define(constantName, SymbolType.CONSTANT);

    ConstantNode constantNode = new ConstantNode(nextToken(), expression());

    symbols.assign(constantName, constantNode);

    return constantNode;
  }

  private InstructionNode variable(LabelNode label) {
    if (label == null) {
      throw new ParseException("Constant expects a label");
    }

    String variableName = label.label().text();

    symbols.define(variableName, SymbolType.VARIABLE);

    VariableNode variableNode = new VariableNode(nextToken(), expression());

    symbols.assign(variableName, variableNode);

    return variableNode;
  }

  private InstructionNode set(LabelNode label) {
    if (!lineHasToken(AssemblerTokenType.COMMA)) {
      return variable(label);
    }

    return opcode(nextToken());
  }

  private InstructionNode end() {
    return new EndNode(expect(AssemblerTokenType.END));
  }

  private LineNode newBasicLine(InstructionNode commandNode) {
    return new BasicLineNode(commandNode, comment(), newline());
  }

  private boolean isOpcode(AssemblerToken identifier) {
    return Mnemonic.find(identifier.text()) != null;
  }

  private boolean isMacroCall(AssemblerToken identifier) {
    SymbolInfo symbolInfo = symbols.lookup(identifier.text());

    return (symbolInfo != null && symbolInfo.type() == SymbolType.MACRO);
  }

  private AssemblerToken expect(AssemblerTokenType type) {
    if (!checkType(type)) {
      AssemblerToken token = peek();
      throw new ParseException(
          "Line: "
              + token.line().number()
              + ", column: "
              + token.startColumn()
              + " - Expected "
              + type.name()
              + ", got "
              + token.text());
    }

    return nextToken();
  }

  private boolean isLabel(AssemblerToken identifier) {
    SymbolInfo symbolInfo = symbols.lookup(identifier.text());

    if (symbolInfo != null && symbolInfo.type() == SymbolType.MACRO) {
      return false;
    }

    return Mnemonic.find(identifier.text()) == null;
  }

  public ExpressionNode expression() {
    return logicalOr();
  }

  private ExpressionNode logicalOr() {
    ExpressionNode expression = logicalAnd();

    while (match(AssemblerTokenType.PIPE_PIPE)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = logicalAnd();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode logicalAnd() {
    ExpressionNode expression = bitwiseOr();

    while (match(AssemblerTokenType.AND_AND)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = bitwiseOr();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode bitwiseOr() {
    ExpressionNode expression = bitwiseXor();

    while (match(AssemblerTokenType.PIPE)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = bitwiseXor();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode bitwiseXor() {
    ExpressionNode expression = bitwiseAnd();

    while (match(AssemblerTokenType.CARET)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = bitwiseAnd();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode bitwiseAnd() {
    ExpressionNode expression = equality();

    while (match(AssemblerTokenType.AND)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = equality();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode equality() {
    ExpressionNode expression = relational();

    while (match(AssemblerTokenType.BANG_EQUAL, AssemblerTokenType.EQUAL_EQUAL)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = relational();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode relational() {
    ExpressionNode expression = shift();

    while (match(
        AssemblerTokenType.GREATER,
        AssemblerTokenType.GREATER_EQUAL,
        AssemblerTokenType.LESS,
        AssemblerTokenType.LESS_EQUAL)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = shift();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode shift() {
    ExpressionNode expression = additive();

    while (match(
        AssemblerTokenType.LESS_LESS,
        AssemblerTokenType.GREATER_GREATER,
        AssemblerTokenType.GREATER_GREATER_GREATER)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = additive();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode additive() {
    ExpressionNode expression = multiplicative();

    while (match(AssemblerTokenType.MINUS, AssemblerTokenType.PLUS)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = multiplicative();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode multiplicative() {
    ExpressionNode expression = unary();

    while (match(AssemblerTokenType.STAR, AssemblerTokenType.SLASH)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = unary();
      expression = new BinaryExpressionNode(expression, operator, right);
    }

    return expression;
  }

  private ExpressionNode unary() {
    if (match(
        AssemblerTokenType.TILDE,
        AssemblerTokenType.BANG,
        AssemblerTokenType.MINUS,
        AssemblerTokenType.PLUS)) {
      AssemblerToken operator = nextToken();
      ExpressionNode right = unary();
      return new UnaryExpressionNode(operator, right);
    }

    return primary();
  }

  private ExpressionNode primary() {
    /*
     * Literal expressions
     */
    if (match(
        AssemblerTokenType.DECIMAL_NUMBER,
        AssemblerTokenType.OCTAL_NUMBER,
        AssemblerTokenType.HEX_NUMBER,
        AssemblerTokenType.BINARY_NUMBER)) {
      AssemblerToken numberToken = nextToken();
      return new LiteralNumberExpressionNode(numberToken);
    } else if (match(AssemblerTokenType.STRING, AssemblerTokenType.CHAR)) {
      AssemblerToken stringToken = nextToken();
      return new LiteralStringExpressionNode(stringToken);
    }

    /*
     * Identifier expressions
     */
    if (match(AssemblerTokenType.IDENTIFIER)) {
      AssemblerToken identifier = nextToken();
      symbols.reference(identifier.text(), symbols.lookup(identifier.text()));
      return new IdentifierExpressionNode(identifier);
    }

    /*
     * Address reference expressions
     */
    if (match(AssemblerTokenType.DOLLAR, AssemblerTokenType.DOLLAR_DOLLAR)) {
      return new AddressReferenceExpressionNode(nextToken());
    }

    /*
     * Grouping expressions
     */
    if (isGroupingStart()) {
      AssemblerToken groupStart = nextToken();
      Grouping grouping = Grouping.findByStartType(groupStart.type());
      ExpressionNode expression = expression();
      AssemblerTokenType endType = grouping.end();
      AssemblerToken groupEnd = expect(endType);

      return new GroupingExpressionNode(groupStart, expression, groupEnd);
    }

    throw new ParseException(peek().type() + "[" + peek().text() + "] -Expect expression");
  }

  private List<ExpressionNode> expressions() {
    List<ExpressionNode> expressions = new ArrayList<>();

    while (isNotEol()) {
      expressions.add(expression());

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return expressions;
  }

  protected void newSource(ScannerSource source) throws IOException {
    tokenProducer.newSource(source);
  }

  protected void newSource(String filename) throws IOException {
    tokenProducer.newSource(filename);
  }

  protected boolean match(AssemblerTokenType... types) {
    for (AssemblerTokenType type : types) {
      if (checkType(type)) {
        return true;
      }
    }

    return false;
  }

  protected boolean match(AssemblerToken token, AssemblerTokenType... types) {
    for (AssemblerTokenType type : types) {
      if (isType(token, type)) {
        return true;
      }
    }

    return false;
  }

  /** Checks if the next token is of a specific type without consuming the next token */
  protected boolean checkType(AssemblerTokenType type) {
    if (isEof()) {
      return false;
    }

    return isType(peek(), type);
  }

  /** Consumes and returns the next token */
  protected AssemblerToken nextToken() {
    return tokenProducer.next();
  }

  /** Looks at the next token without consuming it */
  protected AssemblerToken peek() {
    return tokenProducer.peek();
  }

  /** Look ahead at a token without consuming it */
  protected AssemblerToken peek(int position) {
    return tokenProducer.peek(position);
  }

  /** Checks if the next token is EOF */
  protected boolean isEof() {
    return match(peek(), AssemblerTokenType.EOF);
  }

  protected boolean isEof(int position) {
    return match(peek(position), AssemblerTokenType.EOF);
  }

  private boolean isNotEol() {
    return !match(AssemblerTokenType.COMMENT, AssemblerTokenType.NEWLINE);
  }

  protected boolean isType(AssemblerToken token, AssemblerTokenType type) {
    return token.type() == type;
  }

  protected boolean lineHasToken(AssemblerTokenType type) {
    boolean hasToken = false;

    int position = 1;

    while (!(peek(position).type() == AssemblerTokenType.NEWLINE
            || peek(position).type() == AssemblerTokenType.EOF)
        && !hasToken) {
      hasToken = (peek(position).type() == type);

      position++;
    }

    return hasToken;
  }
}
