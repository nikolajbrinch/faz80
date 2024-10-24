package dk.nikolajbrinch.faz80.parser;

import dk.nikolajbrinch.faz80.base.util.StringUtil;
import dk.nikolajbrinch.faz80.parser.base.AssemblerTokenProducer;
import dk.nikolajbrinch.faz80.parser.base.Condition;
import dk.nikolajbrinch.faz80.parser.base.Grouping;
import dk.nikolajbrinch.faz80.parser.base.Register;
import dk.nikolajbrinch.faz80.parser.base.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.base.values.StringValue;
import dk.nikolajbrinch.faz80.parser.expressions.AddressExpression;
import dk.nikolajbrinch.faz80.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.faz80.parser.expressions.Expression;
import dk.nikolajbrinch.faz80.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.faz80.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.faz80.parser.expressions.MacroCallExpression;
import dk.nikolajbrinch.faz80.parser.expressions.NumberExpression;
import dk.nikolajbrinch.faz80.parser.expressions.StringExpression;
import dk.nikolajbrinch.faz80.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.faz80.parser.operands.ConditionOperand;
import dk.nikolajbrinch.faz80.parser.operands.ExpressionOperand;
import dk.nikolajbrinch.faz80.parser.operands.GroupingOperand;
import dk.nikolajbrinch.faz80.parser.operands.Operand;
import dk.nikolajbrinch.faz80.parser.operands.RegisterOperand;
import dk.nikolajbrinch.faz80.parser.statements.AlignStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssertStatement;
import dk.nikolajbrinch.faz80.parser.statements.AssignStatement;
import dk.nikolajbrinch.faz80.parser.statements.BlockStatement;
import dk.nikolajbrinch.faz80.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataByteStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataLongStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataTextStatement;
import dk.nikolajbrinch.faz80.parser.statements.DataWordStatement;
import dk.nikolajbrinch.faz80.parser.statements.EmptyStatement;
import dk.nikolajbrinch.faz80.parser.statements.EndStatement;
import dk.nikolajbrinch.faz80.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.faz80.parser.statements.IncludeStatement;
import dk.nikolajbrinch.faz80.parser.statements.InsertStatement;
import dk.nikolajbrinch.faz80.parser.statements.InstructionStatement;
import dk.nikolajbrinch.faz80.parser.statements.LocalStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroCallStatement;
import dk.nikolajbrinch.faz80.parser.statements.MacroStatement;
import dk.nikolajbrinch.faz80.parser.statements.OriginStatement;
import dk.nikolajbrinch.faz80.parser.statements.PhaseStatement;
import dk.nikolajbrinch.faz80.parser.statements.RepeatStatement;
import dk.nikolajbrinch.faz80.parser.statements.SectionStatement;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.parser.symbols.Macro;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolTable;
import dk.nikolajbrinch.faz80.parser.symbols.SymbolType;
import dk.nikolajbrinch.faz80.parser.symbols.UndefinedSymbolException;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.AssemblerTokenType;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import dk.nikolajbrinch.parser.BaseParser;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.ParseMessage;
import dk.nikolajbrinch.scanner.impl.FileSource;
import dk.nikolajbrinch.scanner.impl.StringSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class AssemblerParser
    extends BaseParser<
        Statement,
        AssemblerTokenType,
        AssemblerParserConfiguration,
        AssemblerToken,
        AssemblerParseResult> {

  private final Set<Mnemonic> conditionalInstructions =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);
  private AssemblerTokenType eos = AssemblerTokenType.NEWLINE;

  private Mode mode = Mode.NORMAL;

  private Set<AssemblerToken> missingIdentifiers = new HashSet<>();

  private final SymbolTable globaleSymbolTable = new SymbolTable();

  private SymbolTable currentSymbolTable = globaleSymbolTable;

  public AssemblerParser() {
    this(new AssemblerParserConfiguration());
  }

  public AssemblerParser(AssemblerParserConfiguration configuration) {
    this(configuration, null);
  }

  public AssemblerParser(File directory) {
    this(new AssemblerParserConfiguration(), null);
  }

  public AssemblerParser(AssemblerParserConfiguration configuration, File directory) {
    super(configuration, new AssemblerTokenProducer(directory));
  }

  public AssemblerParseResult parse(File file) throws IOException {
    newSource(new FileSource(file));

    return parse();
  }

  public AssemblerParseResult parse(String source) throws IOException {
    newSource(new StringSource(source));

    return parse();
  }

  private AssemblerParseResult parse() {
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

    Iterator<AssemblerToken> iterator = missingIdentifiers.iterator();
    while (iterator.hasNext()) {
      AssemblerToken token = iterator.next();
      if (currentSymbolTable.exists(token.text())) {
        iterator.remove();
      } else {
        getMessages().add(ParseMessage.error(token, "Undefined identifier."));
      }
    }

    return new AssemblerParseResult(
        new BlockStatement(currentSymbolTable, statements), getMessages());
  }

  private Statement declaration() {
    try {
      AssemblerTokenType type = peek().type();

      return switch (type) {
        case IDENTIFIER -> identifier();
        case SECTION -> section();
        case ORIGIN -> origin();
        case ALIGN -> align();
        case INCLUDE -> include();
        case INSERT -> insert();
        case DATA_BYTE -> dataByte();
        case DATA_WORD_BE, DATA_WORD_LE -> dataWord(type);
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
        case END -> end();
        default -> statement();
      };
    } catch (ParseException e) {
      reportError(e);

      synchronize();

      return null;
    }
  }

  private Statement end() {
    AssemblerToken end = consume(AssemblerTokenType.END, "Expect end");

    AssemblerToken eol = expectEol("Expect newline or eof after end.");

    return new EndStatement(end);
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
        /*
         * Ignore
         */
      }

      if (symbolType != null && symbolType == SymbolType.MACRO) {
        return macroCall(identifier);
      }
    }

    if (Mnemonic.find(identifier.text()) != null) {
      /*
       * Check if identifier is an instruction
       */
      return instruction(identifier);
    }

    Statement statement =
        switch (peek().type()) {
          case CONSTANT -> constant(identifier);
          case ASSIGN, EQUAL -> variable(identifier);
          case SET -> set(identifier);
          case MACRO -> macro(identifier);
          default -> label(identifier);
        };

    if (statement != null) {
      missingIdentifiers.remove(identifier);
    }

    return statement;
  }

  private Statement section() {
    AssemblerToken token = consume(AssemblerTokenType.SECTION, "Expect ection");

    AssemblerToken name =
        consume(AssemblerTokenType.IDENTIFIER, "Expect identifier for section name after section");

    return new SectionStatement(token, name);
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

    return defineAndAssign(
        null,
        identifier,
        SymbolType.LABEL,
        new AddressExpression(
            new AssemblerToken(
                AssemblerTokenType.DOLLAR,
                identifier.sourceInfo(),
                identifier.position(),
                identifier.line(),
                identifier.startColumn(),
                identifier.endColumn(),
                "$")));
  }

  private Statement constant(AssemblerToken identifier) {
    AssemblerToken token = nextToken();

    Expression value = expression();

    AssemblerToken eol = expectEol("Expect newline or eof after constant.");

    return defineAndAssign(token, identifier, SymbolType.CONSTANT, value);
  }

  private Statement variable(AssemblerToken identifier) {
    AssemblerToken token = nextToken();

    Expression value = expression();

    AssemblerToken eol = expectEol("Expect newline or eof after variable.");

    return defineAndAssign(token, identifier, SymbolType.VARIABLE, value);
  }

  private Statement origin() {
    AssemblerToken token = consume(AssemblerTokenType.ORIGIN, "Expect origin");

    Expression location = expression();
    Expression fillByte = null;

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      fillByte = expression();
    }

    AssemblerToken eol = expectEol("Expect newline or eof after origin declaration.");

    return new OriginStatement(token, location, fillByte);
  }

  private Statement align() {
    AssemblerToken token = consume(AssemblerTokenType.ALIGN, "Expect align");

    Expression alignment = expression();
    Expression fillByte = null;

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      fillByte = expression();
    }

    AssemblerToken eol = expectEol("Expect newline or eof after origin declaration.");

    return new AlignStatement(token, alignment, fillByte);
  }

  private Statement include() {
    AssemblerToken token = consume(AssemblerTokenType.INCLUDE, "Expect include");

    AssemblerToken string = consume(AssemblerTokenType.STRING, "Expect string after include");

    if (getConfiguration().isResolveIncludes()) {
      try {
        newSource(StringUtil.unquote(string.text()));
      } catch (IOException e) {
        throw new ParseException(string, e.getMessage(), e);
      }
    } else {
      return new IncludeStatement(token, string);
    }

    return null;
  }

  private Statement insert() {
    AssemblerToken token = consume(AssemblerTokenType.INSERT, "Expect insert");

    AssemblerToken string = consume(AssemblerTokenType.STRING, "Expect string after insert");

    return new InsertStatement(token, string);
  }

  private Statement dataByte() {
    AssemblerToken token = consume(AssemblerTokenType.DATA_BYTE, "Expect byte");

    List<Expression> values = commaSeparatedExpressions();

    expectEol("Expect newline or eof after byte declaration.");

    return new DataByteStatement(token, values);
  }

  private Statement dataWord(AssemblerTokenType tokenType) {
    AssemblerToken token = consume(tokenType, "Expect word");

    List<Expression> values = commaSeparatedExpressions();

    expectEol("Expect newline or eof after word declaration.");

    return new DataWordStatement(token, values);
  }

  private Statement dataLong() {
    AssemblerToken token = consume(AssemblerTokenType.DATA_LONG, "Expect long");

    List<Expression> values = commaSeparatedExpressions();

    expectEol("Expect newline or eof after long declaration.");

    return new DataLongStatement(token, values);
  }

  private Statement dataText() {
    AssemblerToken token = consume(AssemblerTokenType.DATA_TEXT, "Expect text");

    List<Expression> values = commaSeparatedExpressions();

    expectEol("Expect newline or eof after text declaration.");

    return new DataTextStatement(token, values);
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

    globaleSymbolTable.define(identifier.text(), SymbolType.LABEL);

    return null;
  }

  private Statement assertion() {
    AssemblerToken token = consume(AssemblerTokenType.ASSERT, "Expect assert");

    Expression expression = expression();

    expectEol("Expect newline or eof after assert declaration.");

    return new AssertStatement(token, expression);
  }

  private Statement macro(AssemblerToken identifier) {
    AssemblerToken name = identifier;

    AssemblerToken startToken = consume(AssemblerTokenType.MACRO, "Expect macro");

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
        parameter -> {
          macroSymbolTable.define(parameter.name().text(), SymbolType.MACRO_ARGUMENT);
          macroSymbolTable.assign(
              parameter.name().text(),
              SymbolType.MACRO_ARGUMENT,
              Optional.ofNullable(parameter.defaultValue()));
        });

    BlockStatement block = block(macroSymbolTable, AssemblerTokenType.ENDMACRO);

    AssemblerToken endToken = consume(AssemblerTokenType.ENDMACRO, "Expect endmacro after body!");
    expectEol("Expect newline or eof after endmacro.");

    MacroStatement statement =
        new MacroStatement(startToken, endToken, name, macroSymbolTable, parameters, block);

    currentSymbolTable.define(name.text(), SymbolType.MACRO);
    currentSymbolTable.assign(
        name.text(), SymbolType.MACRO, Optional.of(new Macro(name.text(), parameters, block)));

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

      AssemblerToken eol;

      if (stop == AssemblerTokenType.RIGHT_PAREN) {
        consume(AssemblerTokenType.RIGHT_PAREN, "Expect ) after macro call arguments");
        eol = consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

        return new MacroCallStatement(name, arguments);
      }

      eol = consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

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
            arguments.add(new EmptyStatement(start));
            expectEol("Expect > after macro call argument");
          } else if (checkType(AssemblerTokenType.GREATER_GREATER)) {
            AssemblerToken token =
                consume(AssemblerTokenType.GREATER_GREATER, "Expect < before macro call argument");
            AssemblerToken stringToken =
                new AssemblerToken(
                    AssemblerTokenType.CHAR,
                    getSourceInfo(),
                    token.position(),
                    token.line(),
                    token.startColumn(),
                    token.endColumn() - 1,
                    "'>'");
            arguments.add(
                new ExpressionStatement(
                    new StringExpression(stringToken, StringValue.create(stringToken))));
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
            AssemblerToken stringToken =
                new AssemblerToken(
                    AssemblerTokenType.CHAR,
                    getSourceInfo(),
                    token.position(),
                    token.line(),
                    token.startColumn() + 1,
                    token.endColumn(),
                    "'<'");
            arguments.add(
                new ExpressionStatement(
                    new StringExpression(stringToken, StringValue.create(stringToken))));
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
    AssemblerToken startToken = consume(AssemblerTokenType.REPEAT, "Expect repeat");

    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after repeat.");

    BlockStatement block = block(AssemblerTokenType.ENDREPEAT);

    AssemblerToken endToken = consume(AssemblerTokenType.ENDREPEAT, "Expect endr after body!");
    expectEol("Expect newline or eof after endr.");

    return new RepeatStatement(startToken, endToken, expression, block);
  }

  private Statement duplicate() {
    AssemblerToken startToken = consume(AssemblerTokenType.DUPLICATE, "Expect duplicate");

    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after duplicate.");

    BlockStatement block = block(AssemblerTokenType.ENDDUPLICATE);

    AssemblerToken endToken = consume(AssemblerTokenType.ENDDUPLICATE, "Expect edup after body!");
    expectEol("Expect newline or eof after edup.");

    return new RepeatStatement(startToken, endToken, expression, block);
  }

  private Statement local() {
    AssemblerToken startToken = consume(AssemblerTokenType.LOCAL, "Expect local");
    consume(AssemblerTokenType.NEWLINE, "Expect newline after local.");

    BlockStatement block = block(AssemblerTokenType.ENDLOCAL);

    AssemblerToken endToken = consume(AssemblerTokenType.ENDLOCAL, "Expect endlocal after body!");
    expectEol("Expect newline or eof after endlocal.");

    return new LocalStatement(startToken, endToken, block);
  }

  private Statement phase() {
    AssemblerToken startToken = consume(AssemblerTokenType.PHASE, "Expect phase");
    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after phase.");

    BlockStatement block = block(AssemblerTokenType.DEPHASE);

    AssemblerToken endToken = consume(AssemblerTokenType.DEPHASE, "Expect dephase after body!");
    expectEol("Expect newline or eof after dephase.");

    return new PhaseStatement(startToken, endToken, expression, block);
  }

  private Statement condition() {
    boolean error = false;

    AssemblerToken ifToken = null;
    AssemblerToken elseToken = null;
    AssemblerToken endToken = null;

    if (match(AssemblerTokenType.IF)) {
      ifToken = consume(AssemblerTokenType.IF, "Expect if");
    } else if (match(AssemblerTokenType.ELSE_IF)) {
      ifToken = consume(AssemblerTokenType.ELSE_IF, "Expect else if");
    } else {
      error = true;
    }

    Expression expression = null;

    if (!error) {
      expression = expression();

      consume(AssemblerTokenType.NEWLINE, "Expect newline after if.");
    }

    /*
     * Search for the next token that can end a block start by if
     */
    AssemblerToken token =
        search(AssemblerTokenType.ELSE, AssemblerTokenType.ELSE_IF, AssemblerTokenType.ENDIF);

    Statement thenBranch = null;
    Statement elseBranch = null;

    List<ParseException> errors = new ArrayList<>();

    if (token != null) {
      if (token.type() == AssemblerTokenType.ENDIF) {
        /*
         * Then branch up until End If
         */
        thenBranch = block(AssemblerTokenType.ENDIF);
        endToken = consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
        if (error) {
          errors.add(new ParseException(endToken, "Missing #if or #elif"));
        }
        expectEol("Expect newline or eof after endif.");
      } else if (token.type() == AssemblerTokenType.ELSE) {
        /*
         * Then branch up until Else
         */
        thenBranch = block(AssemblerTokenType.ELSE);
        elseToken = consume(AssemblerTokenType.ELSE, "Expect else after body!");
        if (error) {
          errors.add(new ParseException(elseToken, "Missing #if or #elif"));
        }
        consume(AssemblerTokenType.NEWLINE, "Expect newline after else!");
        /*
         * Else branch up until End If
         */
        elseBranch = block(AssemblerTokenType.ENDIF);
        endToken = consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
        if (error) {
          errors.add(new ParseException(endToken, "Missing #if or #elif"));
        }
        expectEol("Expect newline or eof after endif.");
      } else if (token.type() == AssemblerTokenType.ELSE_IF) {
        /*
         * Then branch up until Else If
         */
        thenBranch = block(AssemblerTokenType.ELSE_IF);
        if (error) {
          errors.add(new ParseException(nextToken(), "Missing #if or #elif"));
        } else {
          /*
           * Else If branch
           */
          elseBranch = condition();
        }
      }
    }

    if (!errors.isEmpty()) {
      ParseException exception = errors.removeFirst();
      errors.forEach(this::reportError);

      throw exception;
    }

    return new ConditionalStatement(
        ifToken, elseToken, endToken, expression, thenBranch, elseBranch);
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

      return new BlockStatement(
          currentSymbolTable, statements.stream().filter(Objects::nonNull).toList());
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

    AssemblerToken eol = expectEol("Expect newline or eof after instruction.");

    Mnemonic instruction = Mnemonic.find(mnemonic.text());

    //    if (operands.size() < instruction.getOperandsLowerBound()) {
    //      throw new ParseException(
    //          mnemonic,
    //          "Instruction "
    //              + instruction
    //              + " requires at least "
    //              + instruction.getOperandsLowerBound()
    //              + " operands");
    //    } else if (operands.size() > instruction.getOperandsUpperBound()) {
    //      throw new ParseException(
    //          mnemonic,
    //          "Instruction "
    //              + instruction
    //              + " accepts maximum "
    //              + instruction.getOperandsLowerBound()
    //              + " operands");
    //    }

    return new InstructionStatement(mnemonic, operands);
  }

  private Operand operand(AssemblerToken mnemonic) {
    if (isEol()) {
      return null;
    }

    if (isGroupingStart()) {
      AssemblerToken groupStart = nextToken();
      Grouping grouping = Grouping.findByStartType(groupStart.type());
      Operand operand = operand(mnemonic);
      AssemblerTokenType endType = grouping.end();
      consume(endType, "Expect " + endType.name());

      return new GroupingOperand(operand);
    }

    AssemblerToken token = peek();

    if (token.type() == AssemblerTokenType.IDENTIFIER) {
      if (conditionalInstructions.contains(Mnemonic.find(mnemonic.text()))) {
        Condition condition = Condition.find(token.text());

        if (condition != null) {
          missingIdentifiers.remove(token);
          return new ConditionOperand(nextToken(), token.line(), condition);
        }
      }

      Register register = Register.find(token.text());

      if (register != null) {
        AssemblerToken registerToken = nextToken();
        Expression displacement = null;

        if (match(AssemblerTokenType.PLUS, AssemblerTokenType.MINUS)) {
          displacement = expression();
        }

        return new RegisterOperand(registerToken, registerToken.line(), register, displacement);
      }
    }

    return new ExpressionOperand(expression());
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
        AssemblerTokenType.BINARY_NUMBER)) {
      AssemblerToken numberToken = nextToken();
      return new NumberExpression(numberToken, NumberValue.create(numberToken));
    } else if (match(AssemblerTokenType.STRING, AssemblerTokenType.CHAR)) {
      AssemblerToken stringToken = nextToken();
      return new StringExpression(stringToken, StringValue.create(stringToken));
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
      } else {
        missingIdentifiers.add(identifier);
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

  private Statement defineAndAssign(
      AssemblerToken token, AssemblerToken identifier, SymbolType type, Expression expression) {
    currentSymbolTable.define(identifier.text(), type);

    return new AssignStatement(token, identifier, type, expression);
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

  private AssemblerToken expectEol(String message) {
    if (eos == AssemblerTokenType.NEWLINE) {
      if (!isEof()) {
        return consume(eos, message);
      }
    } else {
      return consume(eos, message);
    }

    return null;
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

  private List<Expression> commaSeparatedExpressions() {
    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (match(AssemblerTokenType.COMMA)) {
      nextToken();
      values.add(expression());
    }

    return values;
  }
}
