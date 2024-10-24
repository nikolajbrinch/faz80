package dk.nikolajbrinch.faz80.parser.statements;

public interface StatementProcessorAdapter<R> extends StatementProcessor<R> {

  default R processExpressionStatement(ExpressionStatement statement) { return null; }

  default R processInstructionStatement(InstructionStatement statement) { return null; }

  default R processDataByteStatement(DataByteStatement statement) { return null; }

  default R processDataWordStatement(DataWordStatement statement) { return null; }

  default R processDataLongStatement(DataLongStatement statement) { return null; }

  default R processDataTextStatement(DataTextStatement statement) { return null; }

  default R processOriginStatement(OriginStatement statement) { return null; }

  default R processAlignStatement(AlignStatement statement) { return null; }

  default R processBlockStatement(BlockStatement statement) { return null; }

  default R processLocalStatement(LocalStatement statement) { return null; }

  default R processMacroStatement(MacroStatement statement) { return null; }

  default R processPhaseStatement(PhaseStatement statement) { return null; }

  default R processRepeatStatement(RepeatStatement statement) { return null; }

  default R processConditionalStatement(ConditionalStatement statement) { return null; }

  default R processAssertStatement(AssertStatement statement) { return null; }

  default R processGlobalStatement(GlobalStatement statement) { return null; }

  default R processMacroCallStatement(MacroCallStatement statement) { return null; }

  default R processEmptyStatement(EmptyStatement statement) { return null; }

  default R processInsertStatement(InsertStatement statement) { return null; }

  default R processIncludeStatement(IncludeStatement statement) { return null; }

  default R processAssignStatement(AssignStatement statement) { return null; }

  default R processCommentStatement(CommentStatement statement) { return null; }

  default R processEndStatement(EndStatement endStatement) { return null; }
}
