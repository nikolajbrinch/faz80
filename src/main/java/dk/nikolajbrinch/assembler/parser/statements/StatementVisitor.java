package dk.nikolajbrinch.assembler.parser.statements;

public interface StatementVisitor<R> {

  R visitExpressionStatement(ExpressionStatement statement);

  R visitInstructionStatement(InstructionStatement opcodeStatement);

  R visitConstantStatement(ConstantStatement constantStatement);

  R visitVariableStatement(VariableStatement variableStatement);

  R visitByteStatement(ByteStatement byteStatement);

  R visitLongStatement(LongStatement longStatement);

  R visitWordStatement(WordStatement wordStatement);

  R visitOriginStatement(OriginStatement originStatement);

  R visitAlignStatement(AlignStatement alignStatement);

  R visitBlockStatement(BlockStatement blockStatement);

  R visitLocalStatement(LocalStatement localStatement);

  R visitMacroStatement(MacroStatement macroStatement);

  R visitPhaseStatement(PhaseStatement phaseStatement);

  R visitRepeatStatement(RepeatStatement repeatStatement);

  R visitConditionalStatement(ConditionalStatement conditionalStatement);

  R visitAssertStatement(AssertStatement assertStatement);

  R visitGlobalStatement(GlobalStatement globalStatement);

  R visitLabelStatement(LabelStatement labelStatement);

  R visitMacroCallStatement(MacroCallStatement macroCallStatement);

  R visitEndStatement(EndStatement endStatement);
}
