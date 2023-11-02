package dk.nikolajbrinch.assembler.parser.expressions;

import dk.nikolajbrinch.assembler.parser.expressions.AddressExpression;
import dk.nikolajbrinch.assembler.parser.expressions.AssignExpression;
import dk.nikolajbrinch.assembler.parser.expressions.BinaryExpression;
import dk.nikolajbrinch.assembler.parser.expressions.GroupingExpression;
import dk.nikolajbrinch.assembler.parser.expressions.IdentifierExpression;
import dk.nikolajbrinch.assembler.parser.expressions.LiteralExpression;
import dk.nikolajbrinch.assembler.parser.expressions.UnaryExpression;
import dk.nikolajbrinch.assembler.parser.statements.AlignStatement;
import dk.nikolajbrinch.assembler.parser.statements.AssertStatement;
import dk.nikolajbrinch.assembler.parser.statements.BlockStatement;
import dk.nikolajbrinch.assembler.parser.statements.ByteStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConditionalStatement;
import dk.nikolajbrinch.assembler.parser.statements.ConstantStatement;
import dk.nikolajbrinch.assembler.parser.statements.ExpressionStatement;
import dk.nikolajbrinch.assembler.parser.statements.GlobalStatement;
import dk.nikolajbrinch.assembler.parser.statements.LocalStatement;
import dk.nikolajbrinch.assembler.parser.statements.LongStatement;
import dk.nikolajbrinch.assembler.parser.statements.MacroStatement;
import dk.nikolajbrinch.assembler.parser.statements.InstructionStatement;
import dk.nikolajbrinch.assembler.parser.statements.OriginStatement;
import dk.nikolajbrinch.assembler.parser.statements.PhaseStatement;
import dk.nikolajbrinch.assembler.parser.statements.RepeatStatement;
import dk.nikolajbrinch.assembler.parser.statements.VariableStatement;
import dk.nikolajbrinch.assembler.parser.statements.WordStatement;

public interface ExpressionVisitor<R> {

  R visitBinaryExpression(BinaryExpression expression);

  R visitUnaryExpression(UnaryExpression expression);

  R visitGroupingExpression(GroupingExpression expression);

  R visitLiteralExpression(LiteralExpression expression);

  R visitIdentifierExpression(IdentifierExpression expression);

  R visitAddressExpression(AddressExpression addressExpression);

  R visitAssignExpression(AssignExpression assignExpression);

  R visitRegisterExpression(RegisterExpression registerExpression);
}
