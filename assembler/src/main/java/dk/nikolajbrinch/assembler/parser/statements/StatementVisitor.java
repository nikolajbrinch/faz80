package dk.nikolajbrinch.assembler.parser.statements;

public interface StatementVisitor<R> {

  R visitExpressionStatement(ExpressionStatement statement);

  R visitInstructionStatement(InstructionStatement opcodeStatement);

  R visitDataByteStatement(DataByteStatement byteStatement);

  R visitDataWordStatement(DataWordStatement wordStatement);

  R visitDataLongStatement(DataLongStatement longStatement);

  R visitDataTextStatement(DataTextStatement statement);

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

  R visitMacroCallStatement(MacroCallStatement macroCallStatement);

  R visitEmptyStatement(EmptyStatement emptyStatement);

  R visitInsertStatement(InsertStatement insertStatement);

  R visitIncludeStatement(IncludeStatement includeStatement);

  R visitAssignStatement(AssignStatement assignStatement);
}
