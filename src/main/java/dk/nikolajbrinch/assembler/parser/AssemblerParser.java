package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.ast.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.ast.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.ast.expressions.ConditionExpression;
import dk.nikolajbrinch.assembler.ast.expressions.Expression;
import dk.nikolajbrinch.assembler.ast.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.ast.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.ast.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.ast.expressions.RegisterExpression;
import dk.nikolajbrinch.assembler.ast.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.ast.statements.AlignStatement;
import dk.nikolajbrinch.assembler.ast.statements.AssertStatement;
import dk.nikolajbrinch.assembler.ast.statements.BlockStatement;
import dk.nikolajbrinch.assembler.ast.statements.ByteStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.ast.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.ast.statements.EmptyStatement;
import dk.nikolajbrinch.assembler.ast.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.ast.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.ast.statements.IncludeStatement;
import dk.nikolajbrinch.assembler.ast.statements.InsertStatement;
import dk.nikolajbrinch.assembler.ast.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.ast.statements.LabelStatement;
import dk.nikolajbrinch.assembler.ast.statements.LocalStatement;
import dk.nikolajbrinch.assembler.ast.statements.LongStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroCallStatement;
import dk.nikolajbrinch.assembler.ast.statements.MacroStatement;
import dk.nikolajbrinch.assembler.ast.statements.OriginStatement;
import dk.nikolajbrinch.assembler.ast.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.ast.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.ast.statements.VariableStatement;
import dk.nikolajbrinch.assembler.ast.statements.WordStatement;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.scanner.AssemblerTokenType;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import dk.nikolajbrinch.parser.BaseParser;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssemblerParser extends BaseParser<AssemblerTokenType, AssemblerToken> {

  private final Set<Mnemonic> conditionalInstructions =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);
  private AssemblerTokenType eos = AssemblerTokenType.NEWLINE;

  private Mode mode = Mode.NORMAL;

  public AssemblerParser(Scanner<AssemblerToken> scanner) {
    super(scanner);
  }

  protected static void reportError(AssemblerToken token, String message) {
    if (token.type() == AssemblerTokenType.EOF) {
      report(token.line() + ", " + token.start() + ": at end", message);
    } else {
      report(token.line() + ", " + token.start() + ": at '" + token.text() + "'", message);
    }
  }

  protected static void report(String location, String message) {
    System.out.println(message);
    System.out.println(location);
  }

  public List<Statement> parse() {
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

    return statements;
  }

  private Statement declaration() {
    try {
      return switch (peek().type()) {
        case IDENTIFIER -> {
          AssemblerToken identifier = consume(AssemblerTokenType.IDENTIFIER, "Expect identifier");

          if (Mnemonic.find(identifier.text()) != null) {
            yield instruction(identifier);
          }

          yield switch (peek().type()) {
            case CONSTANT -> constant(identifier);
            case ASSIGN, EQUAL -> variable(identifier);
            case SET -> set(identifier);
            case MACRO -> macro(identifier);
            case LEFT_PAREN -> macroCall(identifier);
            default -> {
              AssemblerToken nextToken = peek();

              /*
               * If next token is an instruction, this must be a label
               */
              if (nextToken.type() == AssemblerTokenType.IDENTIFIER
                  && Mnemonic.find(nextToken.text()) != null) {
                yield label(identifier);
              }

              if (mode != Mode.MACRO_CALL) {
                Statement macroCall = null;

                try {
                  macroCall = macroCall(identifier);
                } catch (ParseException e) {
                  /*
                   * Not a macroCall
                   */
                }

                if (macroCall != null) {
                  yield macroCall;
                }
              }

              yield label(identifier);
            }
          };
        }
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
        case IF -> condition();
        case ASSERT -> assertion();
        case GLOBAL -> global();
        case SET -> instruction(nextToken());
        default -> statement();
      };

    } catch (ParseException e) {
      synchronize();

      return null;
    }
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

    return new LabelStatement(identifier);
  }

  private Statement constant(AssemblerToken identifier) {
    nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after constant.");

    return new ConstantStatement(identifier, value);
  }

  private Statement variable(AssemblerToken identifier) {
    nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after variable.");

    return new VariableStatement(identifier, value);
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

    return new IncludeStatement(string);
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

    return new ByteStatement(values);
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

    return new WordStatement(values);
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

    return new LongStatement(values);
  }

  private Statement dataText() {
    consume(AssemblerTokenType.DATA_TEXT, "Expect text");

    return null;
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
    }

    List<Parameter> parameters = parseParams();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

    Statement block = block(AssemblerTokenType.ENDMACRO);

    consume(AssemblerTokenType.ENDMACRO, "Expect endmarco after body!");
    expectEol("Expect newline or eof after endmarco.");

    return new MacroStatement(name, parameters, block);
  }

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

      if (!arguments.isEmpty()) {
        consume(AssemblerTokenType.NEWLINE, "Expect newline after macro.");

        return new MacroCallStatement(name, arguments);
      }

      return null;
    } finally {
      resetMode();
    }
  }

  /*
   * TODO: Rework scanner and parser to create args as strings for later macro expansion
   */
  private List<Statement> parseArgs(AssemblerTokenType stop) {
    List<Statement> arguments = new ArrayList<>();

    while (!checkType(stop)) {
      if (checkType(AssemblerTokenType.LESS)) {
        try {
          consume(AssemblerTokenType.LESS, "Expect < before macro call argument");
          eos = AssemblerTokenType.GREATER;
          if (checkType(eos)) {
            arguments.add(new EmptyStatement());
            expectEol("Expect > after macro call argument");
          } else if (checkType(AssemblerTokenType.GREATER_GREATER)) {
            AssemblerToken token =
                consume(AssemblerTokenType.GREATER_GREATER, "Expect < before macro call argument");
            arguments.add(
                new ExpressionStatement(
                    new LiteralExpression(
                        new AssemblerToken(
                            AssemblerTokenType.CHAR,
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

    Statement block = block(AssemblerTokenType.ENDLOCAL);

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
    if (match(AssemblerTokenType.IF)) {
      consume(AssemblerTokenType.IF, "Expect if");
    } else if (match(AssemblerTokenType.ELSE_IF)) {
      consume(AssemblerTokenType.ELSE_IF, "Expect else if");
    }

    Expression expression = expression();

    consume(AssemblerTokenType.NEWLINE, "Expect newline after if.");

    AssemblerToken token =
        search(AssemblerTokenType.ELSE, AssemblerTokenType.ELSE_IF, AssemblerTokenType.ENDIF);

    Statement thenBranch = null;
    Statement elseBranch = null;

    if (token.type() == AssemblerTokenType.ENDIF) {
      thenBranch = block(AssemblerTokenType.ENDIF);
      consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
      expectEol("Expect newline or eof after endif.");
    } else if (token.type() == AssemblerTokenType.ELSE) {
      thenBranch = block(AssemblerTokenType.ELSE);
      consume(AssemblerTokenType.ELSE, "Expect else after body!");
      consume(AssemblerTokenType.NEWLINE, "Expect newline after else!");
      elseBranch = block(AssemblerTokenType.ENDIF);
      consume(AssemblerTokenType.ENDIF, "Expect endif after body!");
      expectEol("Expect newline or eof after endif.");
    } else if (token.type() == AssemblerTokenType.ELSE_IF) {
      thenBranch = block(AssemblerTokenType.ELSE_IF);
      elseBranch = condition();
    }

    return new ConditionalStatement(expression, thenBranch, elseBranch);
  }

  private BlockStatement block(AssemblerTokenType endToken) {
    List<Statement> statements = new ArrayList<>();

    while (!isEof()) {
      consumeBlankLines();

      if (checkType(endToken)) {
        break;
      }

      statements.add(declaration());
    }

    return new BlockStatement(statements);
  }

  private Statement instruction(AssemblerToken mnemonic) {
    Expression left = operand(mnemonic);

    Expression right = null;
    if (match(AssemblerTokenType.COMMA)) {
      nextToken();

      right = operand(mnemonic);
    }

    expectEol("Expect newline or eof after instruction.");

    return new InstructionStatement(mnemonic, left, right);
  }

  private Expression operand(AssemblerToken mnemonic) {
    Expression expression = null;

    if (conditionalInstructions.contains(Mnemonic.find(mnemonic.text()))) {
      AssemblerToken token = consume(AssemblerTokenType.IDENTIFIER, "Expect condition!");

      Condition condition = Condition.find(token.text());

      if (condition != null) {
        nextToken();
        expression = new ConditionExpression(condition);
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
          nextToken();
          Expression displacement = null;

          if (checkType(AssemblerTokenType.PLUS)) {
            nextToken();
            displacement = expression();
          }

          expression = new RegisterExpression(register, displacement);
        }
      }

      if (expression == null) {
        expression = expression();
      }

      if (grouping != null) {
        expression = new GroupingExpression(expression);
        AssemblerTokenType endType = grouping.end();
        consume(endType, "Expect " + endType.name() + " after indirect or indexed expression");
      }
    }

    return expression;
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
    return assignment();
  }

  private Expression assignment() {
    Expression expression = logicalOr();

    //    if (control.match(TokenType.EQUAL)) {
    //      control.nextToken();
    //      Expression expression = logicalOr();
    //
    //      return new AssignExpression(control.getAndClearLastIdentifier(), expression);
    //    }

    return expression;
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
      return new IdentifierExpression(nextToken());
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

  protected RuntimeException error(AssemblerToken token, String message) {
    reportError(token, message);

    return new ParseException(message);
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
  protected AssemblerTokenType getEofType() {
    return AssemblerTokenType.EOF;
  }

  @Override
  protected AssemblerTokenType getCommentType() {
    return AssemblerTokenType.COMMENT;
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
