package dk.nikolajbrinch.faz80.parser.statements;

import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.scanner.Line;
import dk.nikolajbrinch.scanner.SourceInfo;

public record CommentStatement(AssemblerToken comment) implements Statement {

  @Override
  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitCommentStatement(this);
  }

  @Override
  public SourceInfo sourceInfo() {
    return comment.sourceInfo();
  }

  @Override
  public Line line() {
    return comment.line();
  }
}
