package dk.nikolajbrinch.faz80.parser.statements;

public interface StatementVisitor<R> {

  R visitExpressionStatement(ExpressionStatement statement);

  R visitInstructionStatement(InstructionStatement statement);

  R visitDataByteStatement(DataByteStatement statement);

  R visitDataWordStatement(DataWordStatement statement);

  R visitDataLongStatement(DataLongStatement statement);

  R visitDataTextStatement(DataTextStatement statement);

  R visitOriginStatement(OriginStatement statement);

  R visitAlignStatement(AlignStatement statement);

  R visitBlockStatement(BlockStatement statement);

  R visitLocalStatement(LocalStatement statement);

  R visitMacroStatement(MacroStatement statement);

  R visitPhaseStatement(PhaseStatement statement);

  R visitRepeatStatement(RepeatStatement statement);

  R visitConditionalStatement(ConditionalStatement statement);

  R visitAssertStatement(AssertStatement statement);

  R visitGlobalStatement(GlobalStatement statement);

  R visitMacroCallStatement(MacroCallStatement statement);

  R visitEmptyStatement(EmptyStatement statement);

  R visitInsertStatement(InsertStatement statement);

  R visitIncludeStatement(IncludeStatement statement);

  R visitAssignStatement(AssignStatement statement);

  R visitCommentStatement(CommentStatement statement);

  R visitEndStatement(EndStatement endStatement);
}
