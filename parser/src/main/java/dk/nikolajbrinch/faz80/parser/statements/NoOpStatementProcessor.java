package dk.nikolajbrinch.faz80.parser.statements;

public interface NoOpStatementProcessor extends StatementProcessor<Statement> {

  @Override
  default Statement processSectionStatement(SectionStatement statement) {
    return statement;
  }

  @Override
  default Statement processExpressionStatement(ExpressionStatement statement) {
    return statement;
  }

  @Override
  default Statement processInstructionStatement(InstructionStatement statement) {
    return statement;
  }

  @Override
  default Statement processDataByteStatement(DataByteStatement statement) {
    return statement;
  }

  @Override
  default Statement processDataWordStatement(DataWordStatement statement) {
    return statement;
  }

  @Override
  default Statement processDataLongStatement(DataLongStatement statement) {
    return statement;
  }

  @Override
  default Statement processDataTextStatement(DataTextStatement statement) {
    return statement;
  }

  @Override
  default Statement processOriginStatement(OriginStatement statement) {
    return statement;
  }

  @Override
  default Statement processAlignStatement(AlignStatement statement) {
    return statement;
  }

  @Override
  default Statement processBlockStatement(BlockStatement statement) {
    return statement;
  }

  @Override
  default Statement processLocalStatement(LocalStatement statement) {
    return statement;
  }

  @Override
  default Statement processMacroStatement(MacroStatement statement) {
    return statement;
  }

  @Override
  default Statement processPhaseStatement(PhaseStatement statement) {
    return statement;
  }

  @Override
  default Statement processRepeatStatement(RepeatStatement statement) {
    return statement;
  }

  @Override
  default Statement processConditionalStatement(ConditionalStatement statement) {
    return statement;
  }

  @Override
  default Statement processAssertStatement(AssertStatement statement) {
    return statement;
  }

  @Override
  default Statement processGlobalStatement(GlobalStatement statement) {
    return statement;
  }

  @Override
  default Statement processMacroCallStatement(MacroCallStatement statement) {
    return statement;
  }

  @Override
  default Statement processEmptyStatement(EmptyStatement statement) {
    return statement;
  }

  @Override
  default Statement processInsertStatement(InsertStatement statement) {
    return statement;
  }

  @Override
  default Statement processIncludeStatement(IncludeStatement statement) {
    return statement;
  }

  @Override
  default Statement processAssignStatement(AssignStatement statement) {
    return statement;
  }

  @Override
  default Statement processCommentStatement(CommentStatement statement) {
    return statement;
  }

  @Override
  default Statement processEndStatement(EndStatement statement) {
    return statement;
  }
}
