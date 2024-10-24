package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public sealed interface Statement
    permits AlignStatement, AssertStatement, AssignStatement, BlockStatement, CommentStatement, ConditionalStatement, EmptyStatement,
    EndStatement, ExpressionStatement, GlobalStatement, IncludeStatement, InsertStatement, InstructionStatement, LocalStatement,
    MacroCallStatement, MacroStatement, OriginStatement, PhaseStatement, RepeatStatement, SectionStatement, ValuesStatement {

  SourceInfo sourceInfo();

  Line line();
}
