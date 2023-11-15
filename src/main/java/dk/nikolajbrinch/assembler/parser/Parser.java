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
import dk.nikolajbrinch.assembler.ast.statements.EndStatement;
import dk.nikolajbrinch.assembler.ast.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.ast.statements.GlobalStatement;
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
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.scanner.Token;
import dk.nikolajbrinch.assembler.scanner.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {

  static enum Mode {
    NORMAL,
    MACRO_CALL;
  }

  private final Control control;

  private TokenType eos = TokenType.NEWLINE;

  private Mode mode = Mode.NORMAL;

  public Parser(Scanner scanner) {
    this.control = new Control(scanner);
  }

  private final Set<Mnemonic> conditionalInstructions =
      Set.of(Mnemonic.JR, Mnemonic.JP, Mnemonic.CALL, Mnemonic.RET);

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();

    while (!control.isEof()) {
      control.consumeBlankLines();

      if (!control.isEof()) {
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
      return switch (control.peek().type()) {
        case IDENTIFIER -> {
          Token identifier = control.consume(TokenType.IDENTIFIER, "Expect identifier");

          if (Mnemonic.find(identifier.text()) != null) {
            yield instruction(identifier);
          }

          yield switch (control.peek().type()) {
            case CONSTANT -> constant(identifier);
            case ASSIGN, EQUAL -> variable(identifier);
            case SET -> set(identifier);
            case MACRO -> macro(identifier);
            case LEFT_PAREN -> macroCall(identifier);
            default -> {
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
        case END -> end();
        case SET -> instruction(control.nextToken());
        default -> statement();
      };

    } catch (ParseException e) {
      synchronize();

      return null;
    }
  }

  private Statement set(Token identifier) {
    if (control.lineHasToken(TokenType.COMMA)) {
      return label(identifier);
    }

    return variable(identifier);
  }

  private Statement label(Token identifier) {
    if (control.checkType(TokenType.NEWLINE)) {
      control.consume(TokenType.NEWLINE, "Expect newline after identifier");
    }

    return new LabelStatement(identifier);
  }

  private Statement constant(Token identifier) {
    control.nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after constant.");

    return new ConstantStatement(identifier, value);
  }

  private Statement variable(Token identifier) {
    control.nextToken();

    Expression value = expression();

    expectEol("Expect newline or eof after variable.");

    return new VariableStatement(identifier, value);
  }

  private Statement origin() {
    control.consume(TokenType.ORIGIN, "Expect origin");

    Expression location = expression();
    Expression fillByte = null;

    while (control.match(TokenType.COMMA)) {
      control.nextToken();
      fillByte = expression();
    }

    expectEol("Expect newline or eof after origin declaration.");

    return new OriginStatement(location, fillByte);
  }

  private Statement align() {
    control.consume(TokenType.ALIGN, "Expect align");

    Expression alignment = expression();
    Expression fillByte = null;

    while (control.match(TokenType.COMMA)) {
      control.nextToken();
      fillByte = expression();
    }

    expectEol("Expect newline or eof after origin declaration.");

    return new AlignStatement(alignment, fillByte);
  }

  private Statement include() {
    control.consume(TokenType.INCLUDE, "Expect include");

    return null;
  }

  private Statement insert() {
    control.consume(TokenType.INSERT, "Expect insert");

    return null;
  }

  private Statement dataByte() {
    control.consume(TokenType.DATA_BYTE, "Expect byte");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (control.match(TokenType.COMMA)) {
      control.nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after byte declaration.");

    return new ByteStatement(values);
  }

  private Statement dataWord() {
    control.consume(TokenType.DATA_WORD, "Expect word");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (control.match(TokenType.COMMA)) {
      control.nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after word declaration.");

    return new WordStatement(values);
  }

  private Statement dataLong() {
    control.consume(TokenType.DATA_LONG, "Expect long");

    List<Expression> values = new ArrayList<>();
    values.add(expression());

    while (control.match(TokenType.COMMA)) {
      control.nextToken();
      values.add(expression());
    }

    expectEol("Expect newline or eof after long declaration.");

    return new LongStatement(values);
  }

  private Statement dataText() {
    control.consume(TokenType.DATA_TEXT, "Expect text");

    return null;
  }

  private Statement dataBlock() {
    control.consume(TokenType.DATA_BLOCK, "Expect data block");

    return null;
  }

  private Statement data() {
    control.consume(TokenType.DATA, "Expect data");

    return null;
  }

  private Statement global() {
    control.consume(TokenType.GLOBAL, "Expect global");

    Token identifier = control.consume(TokenType.IDENTIFIER, "Expect identifier after global!");

    expectEol("Expect newline or eof after global declaration.");

    return new GlobalStatement(identifier);
  }

  private Statement assertion() {
    control.consume(TokenType.ASSERT, "Expect assert");

    Expression expression = expression();

    expectEol("Expect newline or eof after assert declaration.");

    return new AssertStatement(expression);
  }

  private Statement macro(Token identifier) {
    Token name = identifier;

    control.consume(TokenType.MACRO, "Expect macro");

    if (name == null) {
      name = control.consume(TokenType.IDENTIFIER, "Expect identifier for macro name after macro");
    }

    List<Parameter> parameters = parseParams();

    control.consume(TokenType.NEWLINE, "Expect newline after macro.");

    Statement block = block(TokenType.ENDMACRO);

    control.consume(TokenType.ENDMACRO, "Expect endmarco after body!");
    expectEol("Expect newline or eof after endmarco.");

    return new MacroStatement(name, parameters, block);
  }

  private List<Parameter> parseParams() {
    List<Parameter> parameters = new ArrayList<>();

    if (control.checkType(TokenType.IDENTIFIER)) {
      parameters.add(parseParam());

      while (control.checkType(TokenType.COMMA)) {
        control.nextToken();
        parameters.add(parseParam());
      }
    }

    return parameters;
  }

  private Parameter parseParam() {
    Token name = control.consume(TokenType.IDENTIFIER, "Expect identifier for macro parameter");

    Expression expression = null;

    if (control.checkType(TokenType.EQUAL)) {
      control.nextToken();
      expression = expression();
    }

    return new Parameter(name, expression);
  }

  private Statement macroCall(Token identifier) {
    mode = Mode.MACRO_CALL;
    try {
      Token name = identifier;

      TokenType stop;

      if (control.checkType(TokenType.LEFT_PAREN)) {
        control.consume(TokenType.LEFT_PAREN, "Expect ( before macro call arguments");
        stop = TokenType.RIGHT_PAREN;
      } else {
        stop = TokenType.NEWLINE;
      }

      List<Statement> arguments = parseArgs(stop);

      if (stop == TokenType.RIGHT_PAREN) {
        control.consume(TokenType.RIGHT_PAREN, "Expect ) after macro call arguments");
        control.consume(TokenType.NEWLINE, "Expect newline after macro.");

        return new MacroCallStatement(name, arguments);
      }

      if (!arguments.isEmpty()) {
        control.consume(TokenType.NEWLINE, "Expect newline after macro.");

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
  private List<Statement> parseArgs(TokenType stop) {
    List<Statement> arguments = new ArrayList<>();

    while (!control.checkType(stop)) {
      if (control.checkType(TokenType.LESS)) {
        try {
          control.consume(TokenType.LESS, "Expect < before macro call argument");
          eos = TokenType.GREATER;
          if (control.checkType(eos)) {
            arguments.add(new EmptyStatement());
            expectEol("Expect > after macro call argument");
          } else if (control.checkType(TokenType.GREATER_GREATER)) {
            Token token =
                control.consume(TokenType.GREATER_GREATER, "Expect < before macro call argument");
            arguments.add(
                new ExpressionStatement(
                    new LiteralExpression(
                        new Token(
                            TokenType.CHAR, token.line(), token.start(), token.end() - 1, "'>'"))));
          } else {
            arguments.add(declaration());
            if (control.checkType(eos)) {
              expectEol("Expect > after macro call argument");
            }
          }
        } finally {
          resetEos();
        }
      } else if (control.checkType(TokenType.LESS_LESS)) {
        try {
          Token token = control.consume(TokenType.LESS_LESS, "Expect < before macro call argument");
          eos = TokenType.GREATER;
          if (control.checkType(eos)) {
            arguments.add(
                new ExpressionStatement(
                    new LiteralExpression(
                        new Token(
                            TokenType.CHAR, token.line(), token.start() + 1, token.end(), "'<'"))));
            expectEol("Expect > after macro call argument");
          } else {
            arguments.add(declaration());
            if (control.checkType(eos)) {
              expectEol("Expect > after macro call argument");
            }
          }
        } finally {
          resetEos();
        }
      } else {
        arguments.add(new ExpressionStatement(expression()));
      }

      if (control.checkType(TokenType.COMMA)) {
        control.nextToken();
      }
    }

    return arguments;
  }

  private Statement repeat() {
    control.consume(TokenType.REPEAT, "Expect repeat");

    Expression expression = expression();

    control.consume(TokenType.NEWLINE, "Expect newline after repeat.");

    Statement block = block(TokenType.ENDREPEAT);

    control.consume(TokenType.ENDREPEAT, "Expect endr after body!");
    expectEol("Expect newline or eof after endr.");

    return new RepeatStatement(expression, block);
  }

  private Statement duplicate() {
    control.consume(TokenType.DUPLICATE, "Expect duplicate");

    Expression expression = expression();

    control.consume(TokenType.NEWLINE, "Expect newline after duplicate.");

    Statement block = block(TokenType.ENDDUPLICATE);

    control.consume(TokenType.ENDDUPLICATE, "Expect edup after body!");
    expectEol("Expect newline or eof after edup.");

    return new RepeatStatement(expression, block);
  }

  private Statement local() {
    control.consume(TokenType.LOCAL, "Expect local");
    control.consume(TokenType.NEWLINE, "Expect newline after local.");

    Statement block = block(TokenType.ENDLOCAL);

    control.consume(TokenType.ENDLOCAL, "Expect endlocal after body!");
    expectEol("Expect newline or eof after endlocal.");

    return new LocalStatement(block);
  }

  private Statement phase() {
    control.consume(TokenType.PHASE, "Expect phase");
    Expression expression = expression();

    control.consume(TokenType.NEWLINE, "Expect newline after phase.");

    Statement block = block(TokenType.DEPHASE);

    control.consume(TokenType.DEPHASE, "Expect dephase after body!");
    expectEol("Expect newline or eof after dephase.");

    return new PhaseStatement(expression, block);
  }

  private Statement condition() {
    if (control.match(TokenType.IF)) {
      control.consume(TokenType.IF, "Expect if");
    } else if (control.match(TokenType.ELSE_IF)) {
      control.consume(TokenType.ELSE_IF, "Expect else if");
    }

    Expression expression = expression();

    control.consume(TokenType.NEWLINE, "Expect newline after if.");

    Token token = control.search(TokenType.ELSE, TokenType.ELSE_IF, TokenType.ENDIF);

    Statement thenBranch = null;
    Statement elseBranch = null;

    if (token.type() == TokenType.ENDIF) {
      thenBranch = block(TokenType.ENDIF);
      control.consume(TokenType.ENDIF, "Expect endif after body!");
      expectEol("Expect newline or eof after endif.");
    } else if (token.type() == TokenType.ELSE) {
      thenBranch = block(TokenType.ELSE);
      control.consume(TokenType.ELSE, "Expect else after body!");
      control.consume(TokenType.NEWLINE, "Expect newline after else!");
      elseBranch = block(TokenType.ENDIF);
      control.consume(TokenType.ENDIF, "Expect endif after body!");
      expectEol("Expect newline or eof after endif.");
    } else if (token.type() == TokenType.ELSE_IF) {
      thenBranch = block(TokenType.ELSE_IF);
      elseBranch = condition();
    }

    return new ConditionalStatement(expression, thenBranch, elseBranch);
  }

  private Statement end() {
    return new EndStatement(control.consume(TokenType.END, "Expect end"));
  }

  private BlockStatement block(TokenType endToken) {
    List<Statement> statements = new ArrayList<>();

    while (!control.isEof()) {
      control.consumeBlankLines();

      if (control.checkType(endToken)) {
        break;
      }

      statements.add(declaration());
    }

    return new BlockStatement(statements);
  }

  private Statement instruction(Token mnemonic) {
    Expression left = operand(mnemonic);

    Expression right = null;
    if (control.match(TokenType.COMMA)) {
      control.nextToken();

      right = operand(mnemonic);
    }

    expectEol("Expect newline or eof after instruction.");

    return new InstructionStatement(mnemonic, left, right);
  }

  private Expression operand(Token mnemonic) {
    Expression expression = null;

    if (conditionalInstructions.contains(Mnemonic.find(mnemonic.text()))) {
      Token token = control.consume(TokenType.IDENTIFIER, "Expect condition!");

      Condition condition = Condition.find(token.text());

      if (condition != null) {
        control.nextToken();
        expression = new ConditionExpression(condition);
      }
    } else {
      boolean isGrouping = false;

      if (control.checkType(TokenType.LEFT_PAREN)) {
        control.nextToken();
        isGrouping = true;
      }

      if (control.checkType(TokenType.IDENTIFIER)) {
        Token token = control.consume(TokenType.IDENTIFIER, "Expect identifier");

        Register register = Register.find(token.text());

        if (register != null) {
          if (register == Register.AF && control.checkType(TokenType.BANG)) {
            control.nextToken();
            register = Register.AF_BANG;
          }

          Expression displacement = null;

          if (control.checkType(TokenType.PLUS)) {
            control.nextToken();
            displacement = expression();
          }

          expression = new RegisterExpression(register, displacement);
        } else {
          expression = expression();
        }

        if (isGrouping) {
          expression = new GroupingExpression(expression);
          control.consume(TokenType.RIGHT_PAREN, "Expect ) after indirect or indexed expression");
        }
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

    while (control.match(TokenType.PIPE_PIPE)) {
      Token operator = control.nextToken();
      Expression right = logicalAnd();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression logicalAnd() {
    Expression expression = bitwiseOr();

    while (control.match(TokenType.AND_AND)) {
      Token operator = control.nextToken();
      Expression right = bitwiseOr();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseOr() {
    Expression expression = bitwiseXor();

    while (control.match(TokenType.PIPE)) {
      Token operator = control.nextToken();
      Expression right = bitwiseXor();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseXor() {
    Expression expression = bitwiseAnd();

    while (control.match(TokenType.CARET)) {
      Token operator = control.nextToken();
      Expression right = bitwiseAnd();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression bitwiseAnd() {
    Expression expression = equality();

    while (control.match(TokenType.AND)) {
      Token operator = control.nextToken();
      Expression right = equality();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression equality() {
    Expression expression = relational();

    while (control.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = control.nextToken();
      Expression right = relational();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression relational() {
    Expression expression = shift();

    while (eos == TokenType.NEWLINE
        ? control.match(
            TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)
        : control.match(TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      Token operator = control.nextToken();
      Expression right = shift();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression shift() {
    Expression expression = additive();

    while (control.match(
        TokenType.LESS_LESS, TokenType.GREATER_GREATER, TokenType.GREATER_GREATER_GREATER)) {
      Token operator = control.nextToken();
      Expression right = additive();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression additive() {
    Expression expression = multiplicative();

    while (control.match(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = control.nextToken();
      Expression right = multiplicative();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression multiplicative() {
    Expression expression = unary();

    while (control.match(TokenType.STAR, TokenType.SLASH)) {
      Token operator = control.nextToken();
      Expression right = unary();
      expression = new BinaryExpression(expression, operator, right);
    }

    return expression;
  }

  private Expression unary() {
    if (control.match(TokenType.TILDE, TokenType.BANG, TokenType.MINUS, TokenType.PLUS)) {
      Token operator = control.nextToken();
      Expression right = unary();
      return new UnaryExpression(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    /*
     * Literal expressions
     */
    if (control.match(
        TokenType.DECIMAL_NUMBER,
        TokenType.OCTAL_NUMBER,
        TokenType.HEX_NUMBER,
        TokenType.BINARY_NUMBER,
        TokenType.STRING,
        TokenType.CHAR)) {
      return new LiteralExpression(control.nextToken());
    }

    /*
     * Identifier expressions
     */
    if (control.match(TokenType.IDENTIFIER)) {
      return new IdentifierExpression(control.nextToken());
    }

    /*
     * Address reference expressions
     */
    if (control.match(TokenType.DOLLAR, TokenType.DOLLAR_DOLLAR)) {
      return new AddressExpression(control.nextToken());
    }

    /*
     * Grouping expressions
     */
    if (control.match(TokenType.LEFT_PAREN)) {
      control.nextToken();
      Expression expression = expression();
      control.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression");

      return new GroupingExpression(expression);
    }

    throw control.error(control.peek(), "Expect expression");
  }

  private void synchronize() {
    resetEos();
    while (!control.isEol()) {
      control.nextToken();
    }

    expectEol("Expect newline");
  }

  private void resetEos() {
    eos = TokenType.NEWLINE;
  }

  private void resetMode() {
    mode = Mode.NORMAL;
  }

  private void expectEol(String message) {
    if (eos == TokenType.NEWLINE) {
      if (!control.isEof()) {
        control.consume(eos, message);
      }
    } else {
      control.consume(eos, message);
    }
  }
}
