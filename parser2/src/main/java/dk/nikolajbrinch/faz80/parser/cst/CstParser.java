package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.AssemblerTokenProducer;
import dk.nikolajbrinch.faz80.parser.Condition;
import dk.nikolajbrinch.faz80.parser.Grouping;
import dk.nikolajbrinch.faz80.parser.Register;
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
import dk.nikolajbrinch.faz80.parser.cst.operands.ConditionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.ExpressionOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.GroupingOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.OperandNode;
import dk.nikolajbrinch.faz80.parser.cst.operands.RegisterOperandNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ArgumentsNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.LocalStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.MacroStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.ParameterNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.PhaseStartNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatEndNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatNode;
import dk.nikolajbrinch.faz80.parser.cst.scopes.RepeatStartNode;
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
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class CstParser {

  private final Logger logger = LoggerFactory.getLogger();

  private static final Set<Mnemonic> CONDITIONAL_INSTRUCTIONS =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);

  private final TokenProducer<AssemblerToken> tokenProducer;

  private final Symbols globals = new Symbols(null);

  private Symbols symbols = globals;

  private final CstParserConfiguration configuration;

  public CstParser() {
    this(null, new CstParserConfiguration());
  }

  public CstParser(CstParserConfiguration configuration) {
    this(null, configuration);
  }

  public CstParser(File baseDirectory) {
    this(baseDirectory, new CstParserConfiguration());
  }

  public CstParser(File baseDirectory, CstParserConfiguration configuration) {
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
    return new ProgramNode(globals, nodes(this::isEof));
  }

  private NodesNode nodes(BooleanSupplier predicate) {
    List<CstNode> nodes = new ArrayList<>();

    while (!predicate.getAsBoolean()) {
      CstNode node = node();

      if (node != null) {
        nodes.add(node);
      }
    }

    return new NodesNode(nodes);
  }

  private CstNode node() {
    CstNode node = null;

    try {
      AssemblerToken label = null;

      AssemblerToken token = peek();

      if (token.type() == AssemblerTokenType.IDENTIFIER && (isLabel(token))) {
        label = nextToken();
        token = peek();
      }

      node =
          switch (token.type()) {
            case IDENTIFIER -> identifier(label);
            case ORIGIN -> origin(label);
            case ALIGN -> align(label);
            case INCLUDE -> include(label);
            case INSERT -> insert(label);
            case DATA_BYTE -> dataByte(label);
            case DATA_WORD -> dataWord(label);
            case DATA_LONG -> dataLong(label);
            case DATA_TEXT -> dataText(label);
            case DATA_BLOCK -> dataBlock(label);
            case DATA -> data(label);
            case MACRO -> macro(label, null);
            case REPEAT -> repeat(label);
            case DUPLICATE -> duplicate(label);
            case LOCAL -> local(label);
            case PHASE -> phase(label);
            case IF, ELSE_IF -> conditional(label);
            case ELSE -> _else(label);
            case ENDIF -> endIf(label);
            case ASSERT -> assertion(label);
            case GLOBAL -> global(label);
            case SET -> set(label);
            case CONSTANT -> constant(label);
            case ASSIGN, EQUAL -> variable(label);
            case DIRECTIVE -> directive(label);
            case END -> end(label);
            case COMMENT -> newLine(label, null, nextToken(), nextToken());
            case NEWLINE -> newLine(label, null, null, nextToken());
            default -> skip(label);
          };
    } catch (CstParseException e) {
      logger.error(e.getMessage());
      sync();
    }

    return node;
  }

  private void sync() {
    while (!isEof() && !checkType(AssemblerTokenType.NEWLINE)) {
      nextToken();
    }

    expect(AssemblerTokenType.NEWLINE);
  }

  private LineNode origin(AssemblerToken label) {
    AssemblerToken token = nextToken();

    ExpressionNode location = expression();

    ExpressionNode fillByte = null;

    if (isComma()) {
      fillByte = expression();
    }

    return newCommandLine(label, new OriginNode(token, location, fillByte));
  }

  private LineNode align(AssemblerToken label) {
    AssemblerToken token = nextToken();

    ExpressionNode alignment = expression();

    ExpressionNode fillByte = null;

    if (isComma()) {
      fillByte = expression();
    }

    return newCommandLine(label, new AlignmentNode(token, alignment, fillByte));
  }

  private LineNode include(AssemblerToken label) {
    IncludeNode includeNode = new IncludeNode(nextToken(), expression());
    LineNode lineNode = newCommandLine(label, includeNode);

    if (configuration.isResolveIncludes()) {
      String filename =
          ((LiteralStringExpressionNode) includeNode.expression()).stringLiteral().text();
      try {
        newSource(StringUtil.unquote(filename));
      } catch (IOException e) {
        throw new CstParseException("Error including: " + filename, e);
      }
    } else {
      return lineNode;
    }

    return null;
  }

  private LineNode insert(AssemblerToken label) {
    return newCommandLine(label, new InsertNode(nextToken(), expression()));
  }

  private LineNode dataByte(AssemblerToken label) {
    return newCommandLine(label, new DataNode(DataType.BYTE, nextToken(), expressions()));
  }

  private LineNode dataWord(AssemblerToken label) {
    return newCommandLine(label, new DataNode(DataType.WORD, nextToken(), expressions()));
  }

  private LineNode dataLong(AssemblerToken label) {
    return newCommandLine(label, new DataNode(DataType.LONG, nextToken(), expressions()));
  }

  private LineNode dataText(AssemblerToken label) {
    return newCommandLine(label, new DataNode(DataType.TEXT, nextToken(), expressions()));
  }

  private LineNode dataBlock(AssemblerToken label) {
    AssemblerToken token = expect(AssemblerTokenType.DATA_BLOCK);

    throw new CstParseException("Support for : " + token.text() + " not implemented yet");
  }

  private LineNode data(AssemblerToken label) {
    AssemblerToken token = expect(AssemblerTokenType.DATA);

    throw new CstParseException("Support for : " + token.text() + " not implemented yet");
  }

  private LineNode directive(AssemblerToken label) {
    AssemblerToken token = expect(AssemblerTokenType.DIRECTIVE);

    throw new CstParseException("Unknown directive: " + token.text());
  }

  private RepeatNode repeat(AssemblerToken label) {
    return repeat(label, AssemblerTokenType.ENDREPEAT);
  }

  private RepeatNode duplicate(AssemblerToken label) {
    return repeat(label, AssemblerTokenType.ENDDUPLICATE);
  }

  private LocalNode local(AssemblerToken label) {
    return nestSymbols(
        () -> {
          LineNode startLine = newCommandLine(label, new LocalStartNode(nextToken()));
          NodesNode nodes = nodes(() -> checkType(AssemblerTokenType.ENDLOCAL));
          LineNode endLine = newCommandLine(null, new LocalEndNode(nextToken()));

          return new LocalNode(symbols, startLine, nodes, endLine);
        });
  }

  private PhaseNode phase(AssemblerToken label) {
    return nestSymbols(
        () -> {
          LineNode startLine = newCommandLine(label, new PhaseStartNode(nextToken(), expression()));
          NodesNode nodes = nodes(() -> checkType(AssemblerTokenType.DEPHASE));
          LineNode endLine = newCommandLine(null, new PhaseEndNode(nextToken()));

          return new PhaseNode(symbols, startLine, nodes, endLine);
        });
  }

  private ConditionalNode conditional(AssemblerToken label) {
    AssemblerToken token = nextToken();

    LineNode ifNode = null;

    if (token.type() == AssemblerTokenType.IF) {
      ifNode = newCommandLine(label, new IfNode(token, expression()));
    } else if (token.type() == AssemblerTokenType.ELSE_IF) {
      ifNode = newCommandLine(label, new ElseIfNode(token, expression()));
    }

    NodesNode thenBranch =
        nodes(
            () ->
                match(
                    AssemblerTokenType.ENDIF, AssemblerTokenType.ELSE, AssemblerTokenType.ELSE_IF));

    if (checkType(AssemblerTokenType.ENDIF)) {
      return new ConditionalNode(ifNode, thenBranch, null, null, node());
    }

    if (checkType(AssemblerTokenType.ELSE)) {
      return new ConditionalNode(
          ifNode, thenBranch, node(), nodes(() -> match(AssemblerTokenType.ENDIF)), node());
    }

    if (checkType(AssemblerTokenType.ELSE_IF)) {
      return new ConditionalNode(ifNode, thenBranch, node(), null, null);
    }

    return null;
  }

  private LineNode _else(AssemblerToken label) {
    return newCommandLine(label, new ElseNode(expect(AssemblerTokenType.ELSE)));
  }

  private LineNode endIf(AssemblerToken label) {
    return newCommandLine(label, new EndIfNode(expect(AssemblerTokenType.ENDIF)));
  }

  private RepeatNode repeat(AssemblerToken label, AssemblerTokenType endToken) {
    return nestSymbols(
        () -> {
          LineNode startLine =
              newCommandLine(label, new RepeatStartNode(nextToken(), expression()));
          NodesNode nodes = nodes(() -> checkType(endToken));
          LineNode endLine = newCommandLine(null, new RepeatEndNode(nextToken()));

          return new RepeatNode(symbols, startLine, nodes, endLine);
        });
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

  private LineNode assertion(AssemblerToken label) {
    return newCommandLine(label, new AssertionNode(nextToken(), expression()));
  }

  private LineNode global(AssemblerToken label) {
    AssemblerToken token = nextToken();
    AssemblerToken identifier = nextToken();
    globals.define(identifier.text(), SymbolType.GLOBAL);

    return newCommandLine(label, new GlobalNode(token, identifier));
  }

  private CstNode identifier(AssemblerToken label) {
    AssemblerToken identifier = nextToken();

    if (isMacroCall(identifier)) {
      return macroCall(label, identifier);
    }

    if (isInstruction(identifier)) {
      return instruction(label, identifier);
    }

    if (checkType(AssemblerTokenType.MACRO)) {
      return macro(label, identifier);
    }

    return null;
  }

  private LineNode instruction(AssemblerToken label, AssemblerToken mnmonic) {
    return newCommandLine(label, new InstructionNode(mnmonic, createOperands(mnmonic)));
  }

  private List<OperandNode> createOperands(AssemblerToken mnemonic) {
    List<OperandNode> operands = new ArrayList<>();

    while (isNotEol()) {
      operands.add(operand(mnemonic));

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return operands;
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

  private boolean isNotEol() {
    return !match(AssemblerTokenType.COMMENT, AssemblerTokenType.NEWLINE);
  }

  private boolean isComma() {
    return checkType(AssemblerTokenType.COMMA);
  }

  private MacroNode macro(AssemblerToken label, AssemblerToken identifier) {
    AssemblerToken token = nextToken();
    AssemblerToken name = label == null && identifier == null ? nextToken() : identifier;

    String macroName = null;

    if (name != null) {
      macroName = name.text();
    } else {
      if (label != null) {
        macroName = label.text();
      }
    }

    symbols.define(macroName, SymbolType.MACRO);

    return nestSymbols(
        () -> {
          AssemblerToken extraToken = null;
          if (checkType(AssemblerTokenType.COMMA)) {
            extraToken = nextToken();
          }

          LineNode startLine =
              newCommandLine(
                  label, new MacroStartNode(token, name, extraToken, createParameters()));
          NodesNode nodes = nodes(() -> checkType(AssemblerTokenType.ENDMACRO));
          LineNode endLine = newCommandLine(null, new MacroEndNode(nextToken()));

          return new MacroNode(symbols, startLine, nodes, endLine);
        });
  }

  private List<ParameterNode> createParameters() {
    List<ParameterNode> parameters = new ArrayList<>();

    while (isNotEol()) {
      parameters.add(parameter());

      if (isComma()) {
        nextToken();
      } else {
        break;
      }
    }

    return parameters;
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

  private LineNode macroCall(AssemblerToken label, AssemblerToken identifier) {
    return newCommandLine(label, new MacroCallNode(identifier, createArguments()));
  }

  private ArgumentsNode createArguments() {
    if (isGroupingStart()) {
      AssemblerToken groupStart = nextToken();
      Grouping grouping = Grouping.findByStartType(groupStart.type());
      AssemblerTokenType endType = grouping.end();
      List<ArgumentNode> arguments = arguments(endType);
      AssemblerToken groupEnd = expect(endType);

      return new ArgumentsNode(groupStart, arguments, groupEnd);
    }

    return new ArgumentsNode(null, arguments(null), null);
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

    tokenProducer.setMode(Mode.MACRO);

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

  private LineNode constant(AssemblerToken label) {
    if (label == null) {
      throw new CstParseException("Constant expects a label");
    }

    symbols.define(label.text(), SymbolType.CONSTANT);

    return newCommandLine(label, new ConstantNode(nextToken(), expression()));
  }

  private LineNode variable(AssemblerToken label) {
    if (label == null) {
      throw new CstParseException("Constant expects a label");
    }

    symbols.define(label.text(), SymbolType.VARIABLE);

    return newCommandLine(label, new VariableNode(nextToken(), expression()));
  }

  private LineNode set(AssemblerToken label) {
    if (!lineHasToken(AssemblerTokenType.COMMA)) {
      return variable(label);
    }

    return instruction(label, nextToken());
  }

  private LineNode end(AssemblerToken label) {
    AssemblerToken end = nextToken();

    return newCommandLine(label, new EndNode(end));
  }

  private boolean isInstruction(AssemblerToken identifier) {
    return Mnemonic.find(identifier.text()) != null;
  }

  private boolean isMacroCall(AssemblerToken identifier) {
    SymbolInfo symbolInfo = symbols.lookup(identifier.text());

    return (symbolInfo != null && symbolInfo.type() == SymbolType.MACRO);
  }

  private LineNode skip(AssemblerToken label) {
    while (!checkType(AssemblerTokenType.NEWLINE)) {
      nextToken();
    }

    return newLine(label, null, null, nextToken());
  }

  private LineNode newCommandLine(AssemblerToken label, CommandNode commandNode) {
    AssemblerToken comment = null;

    if (checkType(AssemblerTokenType.COMMENT)) {
      comment = nextToken();
    }

    AssemblerToken newline = expect(AssemblerTokenType.NEWLINE);

    return newLine(label, commandNode, comment, newline);
  }

  private LineNode newLine(
      AssemblerToken label,
      CommandNode commandNode,
      AssemblerToken comment,
      AssemblerToken newline) {
    return new LineNode(
        label == null ? null : new LabelNode(label),
        commandNode,
        comment == null ? null : new CommentNode(comment),
        new NewlineNode(newline));
  }

  private AssemblerToken expect(AssemblerTokenType type) {
    if (!checkType(type)) {
      AssemblerToken token = peek();
      throw new CstParseException(
          "Line: "
              + token.line().number()
              + ", column: "
              + token.start()
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
      return new IdentifierExpressionNode(nextToken());
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

    throw new CstParseException(peek().type() + "[" + peek().text() + "] -Expect expression");
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
