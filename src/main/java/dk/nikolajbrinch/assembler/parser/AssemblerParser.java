package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.compiler.ExpressionEvaluator;
import dk.nikolajbrinch.assembler.compiler.Macro;
import dk.nikolajbrinch.assembler.compiler.symbols.ExpressionSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.LabelSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.MacroSymbol;
import dk.nikolajbrinch.assembler.compiler.symbols.Symbol;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolTable;
import dk.nikolajbrinch.assembler.compiler.symbols.SymbolType;
import dk.nikolajbrinch.assembler.compiler.symbols.UndefinedSymbolException;
import dk.nikolajbrinch.assembler.compiler.symbols.ValueSymbol;
import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.Expression;
import dk.nikolajbrinch.assembler.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.parser.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.assembler.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.parser.operands.ConditionOperand;
import dk.nikolajbrinch.assembler.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.assembler.parser.operands.GroupingOperand;
import dk.nikolajbrinch.assembler.parser.operands.Operand;
import dk.nikolajbrinch.assembler.parser.operands.RegisterOperand;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.parser.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.parser.scanner.Mnemonic;
import dk.nikolajbrinch.assembler.parser.statements.AlignStatement;
import dk.nikolajbrinch.assembler.parser.statements.AssertStatement;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataByteStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataLongStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataTextStatement;
import dk.nikolajbrinch.assembler.parser.statements.DataWordStatement;
import dk.nikolajbrinch.assembler.parser.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.parser.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.parser.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.parser.statements.InsertStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.LabelStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import dk.nikolajbrinch.assembler.util.StringUtil;
import dk.nikolajbrinch.parser.BaseParser;
import dk.nikolajbrinch.parser.FileSource;
import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.StringSource;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssemblerParser extends BaseParser<Statement, AssemblerTokenType, AssemblerToken> {

  private static final Logger logger = LoggerFactory.getLogger();
  private static final AssemblerTokenType[] EOF_TYPES =
      new AssemblerTokenType[] {AssemblerTokenType.EOF, AssemblerTokenType.END};
  private static final AssemblerTokenType[] COMMENT_TYPES =
      new AssemblerTokenType[] {AssemblerTokenType.COMMENT};

  private final Set<Mnemonic> conditionalInstructions =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);
  private AssemblerTokenType eos = AssemblerTokenType.NEWLINE;

  private Mode mode = Mode.NORMAL;

  private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

  private final Configuration configuration = new Configuration();

  private SymbolTable currentSymbolTable;

  public AssemblerParser() {
    super(new AssemblerTokenProducer(), true);
  }

  public BlockStatement parse(File file) throws IOException {
    newSource(new FileSource(file));

    return parse();
  }

  public BlockStatement parse(String source) throws IOException {
    newSource(new StringSource(source));

    return parse();
  }

  private BlockStatement parse() {
    currentSymbolTable = new SymbolTable();

    List<Statement> statements = new ArrayList<>();

    while (!isEof()) {
      consumeBlankLines();

      if (!isEof()) {
        Statement declaration = declaration();

        if (declaration != null) {
          statements.add(declaration);
        }
      }
    }

    return new BlockStatement(currentSymbolTable, statements);
  }

  private Statement declaration() {
    try {
      return switch (peek().type()) {
        case IDENTIFIER -> identifier();
        case ORIGIN -> origin();
        case ALIGN -> align();
        case INCLUDE -> include();
        case INSERT -> insert();
        case DATA_BYTE -> dataByte();
        case DATA_WORD -> dataWord();
        case DATA_LONG -> dataLong();
        case DATA_TEXT -> dataText();
        case DATA_BLOCK -> dataBlock();
        case DATA -> data();
        case MACRO -> macro(null);
        case REPEAT -> repeat();
        case DUPLICATE -> duplicate();
        case LOCAL -> local();
        case PHASE -> phase();
        case IF, ELSE, ELSE_IF, ENDIF -> condition();
        case ASSERT -> assertion();
        case GLOBAL -> global();
        case SET -> instruction(nextToken());
        case DIRECTIVE -> directive();
        default -> statement();
      };
    } catch (ParseException e) {
      reportError(e);

      synchronize();

      return null;
    }
  }

  private Statement directive() {
    AssemblerToken token = consume(AssemblerTokenType.DIRECTIVE, "Expect directive");

    throw new ParseException(token, "Unknown directive: " + token.text());
  }

  private Statement identifier() {
    AssemblerToken identifier = consume(AssemblerTokenType.IDENTIFIER, "Expect identifier");

    if (currentSymbolTable.exists(identifier.text())) {
      /*
       * Check if identifier is a macroCall reference
       */
      SymbolType symbolType = null;

      try {
        symbolType = currentSymbolTable.getSymbolType(identifier.text());
      } catch (UndefinedSymbolException e) {
        throw new ParseException(identifier, e.getMessage(), e);
      }

      if (symbolType == SymbolType.MACRO) {
        return macroCall(identifier);
      }
    }

    if (Mnemonic.find(identifier.text()) != null) {
      /*
       * Check if identifier is an instruction
       */
      return instruction(identifier);
    }

    return switch (peek().type()) {
      case CONSTANT -> constant(identifier);
      case ASSIGN, EQUAL -> variable(identifier);
      case SET -> set(identifier);
      case MACRO -> macro(identifier);
      default -> label(identifier);
    };
  }

  private Statement set(AssemblerToken identifier) {
    if (lineHasToken(AssemblerTokenType.COMMA)) {
      return label(identifier);
    }

    return variable(identifier);
  }

  private Statement label(AssemblerToken identifier) {
    if (checkType(AssemblerTokenType.NEWLINE)) {
      consume(AssemblerTokenType.NEWLINE, "Expect newline after identifier");
    }

    LabelStatement statement = new LabelStatement(identifier);

    currentSymbolTable.define(
        identifier.text(), SymbolType.LABEL, new LabelSymbol(identifier.text()));



    return statement;
  }

  private Statement constant(AssemblerToken identifier) {
    nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after constant.");

    ConstantStatement statement = new ConstantStatement(identifier, value);

    Symbol<?> symbol;

    try {
      symbol = new ValueSymbol(expressionEvaluator.evaluate(value, currentSymbolTable));
    } catch (Exception e) {
      symbol = new ExpressionSymbol(value);
    }

    currentSymbolTable.define(identifier.text(), SymbolType.CONSTANT, symbol);

    return statement;
  }

  private Statement variable(AssemblerToken identifier) {
    nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after variable.");

    VariableStatement statement = new VariableStatement(identifier, value);

    Symbol<?> symbol;

    try {
      symbol = new ValueSymbol(expressionEvaluator.evaluate(value, currentSymbolTable));
    } catch (Exception e) {
      symbol = new ExpressionSymbol(value);
    }

    currentSymbolTable.define(identifier.text(), SymbolType.VARIABLE, symbol);

    return statement;
  }

  private Statement origin() {
    consume(AssemblerTokenType.ORIGIN, "Expect origin");

    Expression location = expression();
    Expression fillByte = null;

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      fillByte = expression();
    }

    expectEol("Expect newline or eof after origin declaration.");

    return new OriginStatement(location, fillByte);
  }

  private Statement align() {
    consume(AssemblerTokenType.ALIGN, "Expect align");

    Expression alignment = expression();
    Expression fillByte = null;

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      fillByte = expression();
    }

    expectEol("Expect newline or eof after origin declaration.");

    return new AlignStatement(alignment, fillByte);
  }

  private Statement include() {
    consume(AssemblerTokenType.INCLUDE, "Expect include");

    AssemblerToken string = consume(AssemblerTokenType.STRING, "Expect string after include");

    if (configuration.isResolveIncludes()) {
      try {
        newSource(new FileSource(StringUtil.unquote(string.text())));
      } catch (IOException e) {
        throw new ParseException(string, e.getMessage(), e);
      }
    } else {
      return new IncludeStatement(string);
    }

    return null;
  }

  private Statement insert() {
    consume(AssemblerTokenType.INSERT, "Expect insert");

    AssemblerToken string = consume(AssemblerTokenType.STRING, "Expect string after insert");

    return new InsertStatement(string);
  }

  private Statement dataByte() {
    consume(AssemblerTokenType.DATA_BYTE, "Expect byte");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after byte declaration.");

    return new DataByteStatement(values);
  }

  private Statement dataWord() {
    consume(AssemblerTokenType.DATA_WORD, "Expect word");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after word declaration.");

    return new DataWordStatement(values);
  }

  private Statement dataLong() {
    consume(AssemblerTokenType.DATA_LONG, "Expect long");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after long declaration.");

    return new DataLongStatement(values);
  }

  private Statement dataText() {
    consume(AssemblerTokenType.DATA_TEXT, "Expect text");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after long declaration.");

    return new DataTextStatement(values);
  }

  private Statement dataBlock() {
    consume(AssemblerTokenType.DATA_BLOCK, "Expect data block");

    return null;
  }

  private Statement data() {
    consume(AssemblerTokenType.DATA, "Expect data");

    return null;
  }

  private Statement global() {
    consume(AssemblerTokenType.GLOBAL, "Expect global");

    AssemblerToken identifier =
        consume(AssemblerTokenType.IDENTIFIER, "Expect identifier after global!");

    expectEol("Expect newline or eof after global declaration.");

    return new GlobalStatement(identifier);
  }

  private Statement assertion() {
    consume(AssemblerTokenType.ASSERT, "Expect assert");

    Expression expression = expression();

    expectEol("Expect newline or eof after assert declaration.");

    return new AssertStatement(expression);
  }

  private Statement macro(AssemblerToken identifier) {
    AssemblerToken name = identifier;

    consume(AssemblerTokenType.MACRO, "Expect macro");

    if (name == null) {
      name = consume(AssemblerTokenType.IDENTIFIER, "Expect identifier for macro name after macro");

      if (peek().type() == AssemblerTokenType.COMMA) {
        /*
         * Some assembly syntaxes allow a comma after the macro name to seprate the arguments
         */
        nextToken();
      }
    }

    List<Parameter> parameters = parseParams();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

    SymbolTable macroSymbolTable = new SymbolTable(currentSymbolTable);
    parameters.forEach(
        parameter ->
            macroSymbolTable.define(
                parameter.name().text(),
                SymbolType.MACRO_ARGUMENT,
                new ExpressionSymbol(parameter.defaultValue())));

    BlockStatement block = block(macroSymbolTable, AssemblerTokenType.ENDMACRO);

    consume(AssemblerTokenType.ENDMACRO, "Expect endmacro after body!");
    expectEol("Expect newline or eof after endmacro.");

    MacroStatement statement = new MacroStatement(name, macroSymbolTable, parameters, block);

    currentSymbolTable.define(
        name.text(), SymbolType.MACRO, new MacroSymbol(new Macro(name.text(), parameters, block)));

    return statement;
  }

  /*
   * TODO: Parse this using semantic mode
   */
  private List<Parameter> parseParams() {
    List<Parameter> parameters = new ArrayList<>();

    if (checkType(AssemblerTokenType.IDENTIFIER)) {
      parameters.add(parseParam());

      while (checkType(AssemblerTokenType.COMMA)) {
        nextToken();
        parameters.add(parseParam());
      }
    }

    return parameters;
  }

  private Parameter parseParam() {
    AssemblerToken name =
        consume(AssemblerTokenType.IDENTIFIER, "Expect identifier for macro parameter");

    Expression expression = null;

    if (checkType(AssemblerTokenType.EQUAL)) {
      nextToken();
      expression = expression();
    }

    /*
     * TODO: default value can be a Statement
     */
    return new Parameter(name, expression);
  }

  private Statement macroCall(AssemblerToken identifier) {
    mode = Mode.MACRO_CALL;
    try {
      AssemblerToken name = identifier;

      AssemblerTokenType stop;

      if (checkType(AssemblerTokenType.LEFT_PAREN)) {
        consume(AssemblerTokenType.LEFT_PAREN, "Expect ( before macro call arguments");
        stop = AssemblerTokenType.RIGHT_PAREN;
      } else {
        stop = AssemblerTokenType.NEWLINE;
      }

      List<Statement> arguments = parseArgs(stop);

      if (stop == AssemblerTokenType.RIGHT_PAREN) {
        consume(AssemblerTokenType.RIGHT_PAREN, "Expect ) after macro call arguments");
        consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

        return new MacroCallStatement(name, arguments);
      }

      consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

      return new MacroCallStatement(name, arguments);
    } finally {
      resetMode();
    }
  }

  /*
   * TODO: Rework scanner and parser to create args as strings for later macro expansion
   */
  private List<Statement> parseArgs(AssemblerTokenType... stop) {
    List<Statement> arguments = new ArrayList<>();

    while (!match(stop)) {
      if (checkType(AssemblerTokenType.LESS)) {
        try {
          AssemblerToken start =
              consume(AssemblerTokenType.LESS, "Expect < before macro call argument");
          eos = AssemblerTokenType.GREATER;
          if (checkType(eos)) {
            arguments.add(new EmptyStatement(start.line()));
            expectEol("Expect > after macro call argument");
          } else if (checkType(AssemblerTokenType.GREATER_GREATER)) {
            AssemblerToken token =
                consume(AssemblerTokenType.GREATER_GREATER, "Expect < before macro call argument");
            arguments.add(
                new ExpressionStatement(
                    new LiteralExpression(
                        new AssemblerToken(
                            AssemblerTokenType.CHAR,
                            getSourceInfo(),
                            token.position(),
                            token.line(),
                            token.start(),
                            token.end() - 1,
                            "'>'"))));
          } else {
            arguments.add(declaration());
            if (checkType(eos)) {
              expectEol("Expect > after macro call argument");
            }
          }
        } finally {
          resetEos();
        }
      } else if (checkType(AssemblerTokenType.LESS_LESS)) {
        try {
          AssemblerToken token =
              consume(AssemblerTokenType.LESS_LESS, "Expect < before macro call argument");
          eos = AssemblerTokenType.GREATER;
          if (checkType(eos)) {
            arguments.add(
                new ExpressionStatement(
                    new LiteralExpression(
                        new AssemblerToken(
                            AssemblerTokenType.CHAR,
                            getSourceInfo(),
                            token.position(),
                            token.line(),
                            token.start() + 1,
                            token.end(),
                            "'<'"))));
            expectEol("Expect > after macro call argument");
          } else {
            arguments.add(declaration());
            if (checkType(eos)) {
              expectEol("Expect > after macro call argument");
            }
          }
        } finally {
          resetEos();
        }
      } else {
        arguments.add(new ExpressionStatement(expression()));
      }

      if (checkType(AssemblerTokenType.COMMA)) {
        nextToken();
      }
    }

    return arguments;
  }

  private Statement repeat() {
    consume(AssemblerTokenType.REPEAT, "Expect repeat");

    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after repeat.");

    Statement block = block(AssemblerTokenType.ENDREPEAT);

    consume(AssemblerTokenType.ENDREPEAT, "Expect endr after body!");
    expectEol("Expect newline or eof after endr.");

    return new RepeatStatement(expression, block);
  }

  private Statement duplicate() {
    consume(AssemblerTokenType.DUPLICATE, "Expect duplicate");

    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after duplicate.");

    Statement block = block(AssemblerTokenType.ENDDUPLICATE);

    consume(AssemblerTokenType.ENDDUPLICATE, "Expect edup after body!");
    expectEol("Expect newline or eof after edup.");

    return new RepeatStatement(expression, block);
  }

  private Statement local() {
    consume(AssemblerTokenType.LOCAL, "Expect local");
    consume(AssemblerTokenType.NEWLINE, "Expect newline after local.");

    BlockStatement block = block(AssemblerTokenType.ENDLOCAL);

    consume(AssemblerTokenType.ENDLOCAL, "Expect endlocal after body!");
    expectEol("Expect newline or eof after endlocal.");

    return new LocalStatement(block);
  }

  private Statement phase() {
    consume(AssemblerTokenType.PHASE, "Expect phase");
    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after phase.");

    Statement block = block(AssemblerTokenType.DEPHASE);

    consume(AssemblerTokenType.DEPHASE, "Expect dephase after body!");
    expectEol("Expect newline or eof after dephase.");

    return new PhaseStatement(expression, block);
  }

  private Statement condition() {
    boolean error = false;

    if (match(AssemblerTokenType.IF)) {
      consume(AssemblerTokenType.IF, "Expect if");
    } else if (match(AssemblerTokenType.ELSE_IF)) {
      consume(AssemblerTokenType.ELSE_IF, "Expect else if");
    } else {
      error = true;
    }

    Expression expression = null;

    if (!error) {
      expression = expression();

      consume(AssemblerTokenType.NEWLINE, "Expect newline after if.");
    }

    AssemblerToken token =
        search(AssemblerTokenType.ELSE, AssemblerTokenType.ELSE_IF, AssemblerTokenType.ENDIF);

    Statement thenBranch = null;
    Statement elseBranch = null;

    List<ParseException> errors = new ArrayList<>();

    if (token != null) {
      if (token.type() == AssemblerTokenType.ENDIF) {
        thenBranch = block(AssemblerTokenType.ENDIF);
        AssemblerToken temp = consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
        if (error) {
          errors.add(new ParseException(temp, "Missing #if or #elif"));
        }
        expectEol("Expect newline or eof after endif.");
      } else if (token.type() == AssemblerTokenType.ELSE) {
        thenBranch = block(AssemblerTokenType.ELSE);
        AssemblerToken temp1 = consume(AssemblerTokenType.ELSE, "Expect else after body!");
        if (error) {
          errors.add(new ParseException(temp1, "Missing #if or #elif"));
        }
        consume(AssemblerTokenType.NEWLINE, "Expect newline after else!");
        elseBranch = block(AssemblerTokenType.ENDIF);
        AssemblerToken temp2 = consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
        if (error) {
          errors.add(new ParseException(temp2, "Missing #if or #elif"));
        }
        expectEol("Expect newline or eof after endif.");
      } else if (token.type() == AssemblerTokenType.ELSE_IF) {
        thenBranch = block(AssemblerTokenType.ELSE_IF);
        if (error) {
          errors.add(new ParseException(nextToken(), "Missing #if or #elif"));
        } else {
          elseBranch = condition();
        }
      }
    }

    if (!errors.isEmpty()) {
      ParseException exception = errors.removeFirst();
      errors.forEach(this::reportError);

      throw exception;
    }

    return new ConditionalStatement(expression, thenBranch, elseBranch);
  }

  private BlockStatement block(AssemblerTokenType endToken) {
    return block(null, endToken);
  }

  private BlockStatement block(SymbolTable symbolTable, AssemblerTokenType endToken) {
    List<Statement> statements = new ArrayList<>();

    SymbolTable previousSymbolTable = currentSymbolTable;

    try {
      currentSymbolTable = new SymbolTable(symbolTable == null ? previousSymbolTable : symbolTable);

      while (!isEof()) {
        consumeBlankLines();

        if (checkType(endToken)) {
          break;
        }

        statements.add(declaration());
      }

      return new BlockStatement(currentSymbolTable, statements);
    } finally {
      currentSymbolTable = previousSymbolTable;
    }
  }

  private Statement instruction(AssemblerToken mnemonic) {
    List<Operand> operands = new ArrayList<>();

    while (!isEol()) {
      operands.add(operand(mnemonic));

      if (match(AssemblerTokenType.COMMA)) {
        nextToken();
      } else {
        break;
      }
    }

    expectEol("Expect newline or eof after instruction.");

    return new InstructionStatement(mnemonic, operands);
  }

  private Operand operand(AssemblerToken mnemonic) {
    if (isEol()) {
      return null;
    }

    Operand operand = null;

    if (conditionalInstructions.contains(Mnemonic.find(mnemonic.text()))) {
      Expression expression = expression();

      Condition condition = null;
      AssemblerToken token = null;

      if (expression instanceof IdentifierExpression identifierExpression) {
        token = identifierExpression.token();
        condition = Condition.find(token.text());

        if (condition != null) {
          operand = new ConditionOperand(token.line(), condition);
        }
      }

      if (operand == null) {
        operand = new ExpressionOperand(expression);
      }
    } else {
      Grouping grouping = null;

      if (isGroupingStart()) {
        grouping = Grouping.findByStartType(nextToken().type());
      }

      AssemblerToken token = peek();
      if (token.type() == AssemblerTokenType.IDENTIFIER) {
        Register register = Register.find(token.text());

        if (register != null) {
          AssemblerToken registerToken = nextToken();
          Expression displacement = null;

          if (match(AssemblerTokenType.PLUS, AssemblerTokenType.MINUS)) {
            displacement = expression();
          }

          operand = new RegisterOperand(registerToken.line(), register, displacement);
        }
      }

      if (operand == null) {
        operand = new ExpressionOperand(expression());
      }

      if (grouping != null) {
        operand = new GroupingOperand(operand);
        AssemblerTokenType endType = grouping.end();
        consume(endType, "Expect " + endType.name() + " after indirect or indexed expression");
      }
    }

    return operand;
  }

  private Statement statement() {
    return expressionStatement();
  }

  private Statement expressionStatement() {
    Expression expression = expression();

    expectEol("Expect newline or eof after expression.");

    return new ExpressionStatement(expression);
  }

  public Expression expression() {
    return logicalOr();
  }

  private Expression logicalOr() {
    Expression expression = logicalAnd();

    while (match(AssemblerTokenType.PIPE_PIPE)) {
      AssemblerToken operator = nextToken();
      Expression right = logicalAnd();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression logicalAnd() {
    Expression expression = bitwiseOr();

    while (match(AssemblerTokenType.AND_AND)) {
      AssemblerToken operator = nextToken();
      Expression right = bitwiseOr();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseOr() {
    Expression expression = bitwiseXor();

    while (match(AssemblerTokenType.PIPE)) {
      AssemblerToken operator = nextToken();
      Expression right = bitwiseXor();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseXor() {
    Expression expression = bitwiseAnd();

    while (match(AssemblerTokenType.CARET)) {
      AssemblerToken operator = nextToken();
      Expression right = bitwiseAnd();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseAnd() {
    Expression expression = equality();

    while (match(AssemblerTokenType.AND)) {
      AssemblerToken operator = nextToken();
      Expression right = equality();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression equality() {
    Expression expression = relational();

    while (match(AssemblerTokenType.BANG_EQUAL, AssemblerTokenType.EQUAL_EQUAL)) {
      AssemblerToken operator = nextToken();
      Expression right = relational();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression relational() {
    Expression expression = shift();

    while (eos == AssemblerTokenType.NEWLINE
        ? match(
            AssemblerTokenType.GREATER,
            AssemblerTokenType.GREATER_EQUAL,
            AssemblerTokenType.LESS,
            AssemblerTokenType.LESS_EQUAL)
        : match(
            AssemblerTokenType.GREATER_EQUAL,
            AssemblerTokenType.LESS,
            AssemblerTokenType.LESS_EQUAL)) {
      AssemblerToken operator = nextToken();
      Expression right = shift();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression shift() {
    Expression expression = additive();

    while (match(
        AssemblerTokenType.LESS_LESS,
        AssemblerTokenType.GREATER_GREATER,
        AssemblerTokenType.GREATER_GREATER_GREATER)) {
      AssemblerToken operator = nextToken();
      Expression right = additive();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression additive() {
    Expression expression = multiplicative();

    while (match(AssemblerTokenType.MINUS, AssemblerTokenType.PLUS)) {
      AssemblerToken operator = nextToken();
      Expression right = multiplicative();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression multiplicative() {
    Expression expression = unary();

    while (match(AssemblerTokenType.STAR, AssemblerTokenType.SLASH)) {
      AssemblerToken operator = nextToken();
      Expression right = unary();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression unary() {
    if (match(
        AssemblerTokenType.TILDE,
        AssemblerTokenType.BANG,
        AssemblerTokenType.MINUS,
        AssemblerTokenType.PLUS)) {
      AssemblerToken operator = nextToken();
      Expression right = unary();
      return new UnaryExpression(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    /*
     * Literal expressions
     */
    if (match(
        AssemblerTokenType.DECIMAL_NUMBER,
        AssemblerTokenType.OCTAL_NUMBER,
        AssemblerTokenType.HEX_NUMBER,
        AssemblerTokenType.BINARY_NUMBER,
        AssemblerTokenType.STRING,
        AssemblerTokenType.CHAR)) {
      return new LiteralExpression(nextToken());
    }

    /*
     * Identifier expressions
     */
    if (match(AssemblerTokenType.IDENTIFIER)) {
      AssemblerToken identifier = nextToken();

      if (currentSymbolTable.exists(identifier.text())) {
        try {
          if (currentSymbolTable.getSymbolType(identifier.text()) == SymbolType.MACRO) {
            consume(AssemblerTokenType.LEFT_PAREN, "Expect ( before macro call arguments");

            List<Statement> arguments = parseArgs(AssemblerTokenType.RIGHT_PAREN);

            consume(AssemblerTokenType.RIGHT_PAREN, "Expect ) after macro call arguments");

            return new MacroCallExpression(identifier, arguments);
          }
        } catch (UndefinedSymbolException e) {
          throw new ParseException(identifier, e.getMessage(), e);
        }
      }

      return new IdentifierExpression(identifier);
    }

    /*
     * Address reference expressions
     */
    if (match(AssemblerTokenType.DOLLAR, AssemblerTokenType.DOLLAR_DOLLAR)) {
      return new AddressExpression(nextToken());
    }

    /*
     * Grouping expressions
     */
    if (isGroupingStart()) {
      Grouping grouping = Grouping.findByStartType(nextToken().type());
      Expression expression = expression();
      AssemblerTokenType endType = grouping.end();
      consume(endType, "Expect " + endType.name() + " after expression");

      return new GroupingExpression(expression);
    }

    throw error(peek(), "Expect expression");
  }

  private boolean isGroupingStart() {
    return match(Grouping.startTypes());
  }

  private void synchronize() {
    resetEos();
    while (!isEol()) {
      nextToken();
    }

    expectEol("Expect newline");
  }

  private void resetEos() {
    eos = AssemblerTokenType.NEWLINE;
  }

  private void resetMode() {
    mode = Mode.NORMAL;
  }

  private void expectEol(String message) {
    if (eos == AssemblerTokenType.NEWLINE) {
      if (!isEof()) {
        consume(eos, message);
      }
    } else {
      consume(eos, message);
    }
  }

  protected AssemblerToken consume(AssemblerTokenType type, String message) {
    if (checkType(type)) {
      return nextToken();
    }

    throw error(peek(), message);
  }

  protected ParseException error(AssemblerToken token, String message) {
    return new ParseException(token, message);
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

  protected void consumeBlankLines() {
    while (match(AssemblerTokenType.NEWLINE)) {
      nextToken();
    }
  }

  @Override
  protected boolean isType(AssemblerToken token, AssemblerTokenType type) {
    return token.type() == type;
  }

  @Override
  protected AssemblerTokenType[] getEofTypes() {
    return EOF_TYPES;
  }

  @Override
  protected AssemblerTokenType[] getCommentTypes() {
    return COMMENT_TYPES;
  }

  protected boolean isEol() {
    return isEof() || peek().type() == AssemblerTokenType.NEWLINE;
  }

  protected boolean isEol(int position) {
    return isEof(position) || peek(position).type() == AssemblerTokenType.NEWLINE;
  }

  enum Mode {
    NORMAL,
    MACRO_CALL
  }
}
