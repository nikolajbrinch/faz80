package dk.nikolajbrinch.faz80.parser.statements;

public interface StatementProcessor<R> {

  default R process(Statement statement) {
    return statement == null ? null : switch (statement) {
      case SectionStatement s -> processSectionStatement(s);
      case ExpressionStatement s -> processExpressionStatement(s);
      case InstructionStatement s -> processInstructionStatement(s);
      case DataByteStatement s -> processDataByteStatement(s);
      case DataWordStatement s -> processDataWordStatement(s);
      case DataLongStatement s -> processDataLongStatement(s);
      case DataTextStatement s -> processDataTextStatement(s);
      case OriginStatement s -> processOriginStatement(s);
      case AlignStatement s -> processAlignStatement(s);
      case BlockStatement s -> processBlockStatement(s);
      case LocalStatement s -> processLocalStatement(s);
      case MacroStatement s -> processMacroStatement(s);
      case PhaseStatement s -> processPhaseStatement(s);
      case RepeatStatement s -> processRepeatStatement(s);
      case ConditionalStatement s -> processConditionalStatement(s);
      case AssertStatement s -> processAssertStatement(s);
      case GlobalStatement s -> processGlobalStatement(s);
      case MacroCallStatement s -> processMacroCallStatement(s);
      case EmptyStatement s -> processEmptyStatement(s);
      case InsertStatement s -> processInsertStatement(s);
      case IncludeStatement s -> processIncludeStatement(s);
      case AssignStatement s -> processAssignStatement(s);
      case CommentStatement s -> processCommentStatement(s);
      case EndStatement s -> processEndStatement(s);
    };
  }

  R processSectionStatement(SectionStatement statement);

  R processExpressionStatement(ExpressionStatement statement);

  R processInstructionStatement(InstructionStatement statement);

  R processDataByteStatement(DataByteStatement statement);

  R processDataWordStatement(DataWordStatement statement);

  R processDataLongStatement(DataLongStatement statement);

  R processDataTextStatement(DataTextStatement statement);

  R processOriginStatement(OriginStatement statement);

  R processAlignStatement(AlignStatement statement);

  R processBlockStatement(BlockStatement statement);

  R processLocalStatement(LocalStatement statement);

  R processMacroStatement(MacroStatement statement);

  R processPhaseStatement(PhaseStatement statement);

  R processRepeatStatement(RepeatStatement statement);

  R processConditionalStatement(ConditionalStatement statement);

  R processAssertStatement(AssertStatement statement);

  R processGlobalStatement(GlobalStatement statement);

  R processMacroCallStatement(MacroCallStatement statement);

  R processEmptyStatement(EmptyStatement statement);

  R processInsertStatement(InsertStatement statement);

  R processIncludeStatement(IncludeStatement statement);

  R processAssignStatement(AssignStatement statement);

  R processCommentStatement(CommentStatement statement);

  R processEndStatement(EndStatement endStatement);
}
