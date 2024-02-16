package dk.nikolajbrinch.faz80.parser.statements;

public interface StatementVisitorAdapter<R> extends StatementVisitor<R> {

  @Override
  default R visitExpressionStatement(ExpressionStatement statement) {
    return null;
  }

  @Override
  default R visitInstructionStatement(InstructionStatement statement) {
    return null;
  }

  @Override
  default R visitDataByteStatement(DataByteStatement statement) {
    return null;
  }

  @Override
  default R visitDataWordStatement(DataWordStatement statement) {
    return null;
  }

  @Override
  default R visitDataLongStatement(DataLongStatement statement) {
    return null;
  }

  @Override
  default R visitDataTextStatement(DataTextStatement statement) {
    return null;
  }

  @Override
  default R visitOriginStatement(OriginStatement statement) {
    return null;
  }

  @Override
  default R visitAlignStatement(AlignStatement statement) {
    return null;
  }

  @Override
  default R visitBlockStatement(BlockStatement statement) {
    return null;
  }

  @Override
  default R visitLocalStatement(LocalStatement statement) {
    return null;
  }

  @Override
  default R visitMacroStatement(MacroStatement statement) {
    return null;
  }

  @Override
  default R visitPhaseStatement(PhaseStatement statement) {
    return null;
  }

  @Override
  default R visitRepeatStatement(RepeatStatement statement) {
    return null;
  }

  @Override
  default R visitConditionalStatement(ConditionalStatement statement) {
    return null;
  }

  @Override
  default R visitAssertStatement(AssertStatement statement) {
    return null;
  }

  @Override
  default R visitGlobalStatement(GlobalStatement statement) {
    return null;
  }

  @Override
  default R visitMacroCallStatement(MacroCallStatement statement) {
    return null;
  }

  @Override
  default R visitEmptyStatement(EmptyStatement statement) {
    return null;
  }

  @Override
  default R visitInsertStatement(InsertStatement statement) {
    return null;
  }

  @Override
  default R visitIncludeStatement(IncludeStatement statement) {
    return null;
  }

  @Override
  default R visitAssignStatement(AssignStatement statement) {
    return null;
  }

  @Override
  default R visitCommentStatement(CommentStatement statement) {
    return null;
  }

  @Override
  default R visitEndStatement(EndStatement statement) {
    return null;
  }
}
