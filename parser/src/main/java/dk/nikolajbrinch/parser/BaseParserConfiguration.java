package dk.nikolajbrinch.parser;


public class BaseParserConfiguration<E> {

  private final boolean debug = false;
  private final boolean ignoreComments;

  private final E[] eofTypes;

  private final E[] commentTypes;

  public BaseParserConfiguration(boolean ignoreComments, E[] eofTypes, E[] commentTypes) {
    this.ignoreComments = ignoreComments;
    this.eofTypes = eofTypes;
    this.commentTypes = commentTypes;
  }

  public boolean isIgnoreComments() {
    return ignoreComments;
  }

  public E[] getEofTypes() {
    return eofTypes;
  }

  public E[] getCommentTypes() {
    return commentTypes;
  }
}
