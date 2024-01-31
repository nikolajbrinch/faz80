package dk.nikolajbrinch.assembler.ide;

import static org.fxmisc.richtext.model.TwoDimensional.Bias.Forward;

import org.fxmisc.richtext.model.TwoDimensional;

public class KeyboardHandlerDelegate {

  private final CodeEditor editor;

  public KeyboardHandlerDelegate(CodeEditor codeEditor) {
    this.editor = codeEditor;
  }

  void tab(int tabSize) {
    int start = getSelectionStart();
    int end = getSelectionEnd();
    int startLine = getStartLine(start);
    int endLine = getEndLine(end);

    editor.getUndoManager().preventMerge();

    if (startLine == endLine) {
      /*
       * Tab a single line
       */
      int caretPosition = editor.getCaretPosition();
      editor.insertText(
          editor.getAbsolutePosition(startLine, getCaretLinePosition()), " ".repeat(tabSize));
      editor.moveTo(caretPosition + tabSize);
    } else {
      /*
       * Tab a selection
       */
      if (getCaretLinePosition() == 0) {
        endLine--;
        end--;
      }
      StringBuilder builder = new StringBuilder();
      int added = 0;
      for (int line = startLine; line <= endLine; line++) {
        builder.append(" ".repeat(tabSize));
        builder.append(getLineText(line));
        added += tabSize;
      }
      replaceTextBlock(startLine, endLine, builder.toString());

      int offset = 0;
      if (editor.offsetToPosition(start, TwoDimensional.Bias.Forward).getMinor() > 0) {
        offset = tabSize;
      }
      editor.selectRange(start + offset, end + added);
    }

    editor.getUndoManager().preventMerge();
  }

  void untab(int tabSize) {
    int start = getSelectionStart();
    int end = getSelectionEnd();
    int startLine = getStartLine(start);
    int endLine = getEndLine(end);

    editor.getUndoManager().preventMerge();

    if (startLine == endLine) {
      /*
       * Untab a single line
       */
      int caretPosition = editor.getCaretPosition();
      int lineStartOffset = getLineColumnPosition(startLine);
      int toRemove = Math.min(tabSize, leadingSpaces(getLineText(startLine, lineStartOffset)));
      if (toRemove > 0) {
        editor.deleteText(lineStartOffset, lineStartOffset + toRemove);
        editor.moveTo(caretPosition - toRemove);
        editor.requestFollowCaret();
      }
    } else {
      /*
       * Untab a selection
       */
      if (getCaretLinePosition() == 0) {
        endLine--;
        end--;
      }
      StringBuilder builder = new StringBuilder();
      int removed = 0;
      int offset = 0;

      for (int line = startLine; line <= endLine; line++) {
        String lineText = getLineText(line);
        int toRemove = Math.min(tabSize, leadingSpaces(lineText));
        if (line == startLine
            && editor.offsetToPosition(start, TwoDimensional.Bias.Forward).getMinor() > 0) {
          offset = toRemove;
        }
        removed += toRemove;
        builder.append(toRemove > 0 ? lineText.substring(toRemove) : lineText);
      }

      if (removed > 0) {
        replaceTextBlock(startLine, endLine, builder.toString());
        editor.selectRange(start - offset, end - removed);
      }
    }

    editor.getUndoManager().preventMerge();
  }

  void moveToLineStart() {
    int line = getCaretLineNumber();
    int lineStartOffset = getLineColumnPosition(line);
    int leadingSpaces = leadingSpaces(getLineText(line, lineStartOffset));
    int position = lineStartOffset;

    if (getCaretLinePosition() > leadingSpaces) {
      position = editor.getAbsolutePosition(line, leadingSpaces);
    }

    editor.moveTo(position);
    editor.requestFollowCaret();
  }

  void moveToLineEnd() {
    int line = getCaretLineNumber();
    int lineStartOffset = getLineColumnPosition(line);
    int leadingSpaces = leadingSpaces(getLineText(line, lineStartOffset));
    int position = editor.getAbsolutePosition(line, leadingSpaces);

    if (getCaretLinePosition() >= leadingSpaces) {
      position = editor.getAbsolutePosition(line, editor.getParagraph(line).length());
    }

    editor.moveTo(position);
    editor.requestFollowCaret();
  }

  void selectToLineStart() {
    int start = getSelectionStart();
    int end = getSelectionEnd();
    int line = getCaretLineNumber();
    int lineStartOffset = getLineColumnPosition(line);
    int leadingSpaces = leadingSpaces(getLineText(line, lineStartOffset));

    int position = lineStartOffset;
    int anchor = editor.getCaretPosition();

    if (getCaretLinePosition() > leadingSpaces) {
      position = editor.getAbsolutePosition(line, leadingSpaces);
    }

    if (start != end) {
      if (editor.getCaretPosition() > start) {
        anchor = start;
      } else {
        anchor = end;
      }
    }

    editor.selectRange(anchor, position);
    editor.requestFollowCaret();
  }

  void selectToLineEnd() {
    int start = getSelectionStart();
    int end = getSelectionEnd();
    int line = getCaretLineNumber();
    int lineLength = editor.getParagraph(line).length();
    int endOfLinePosition = editor.getAbsolutePosition(line, lineLength);

    int anchor = editor.getCaretPosition();

    if (start != end) {
      anchor = end;
    }

    editor.selectRange(anchor, endOfLinePosition);
    editor.requestFollowCaret();
  }

  void moveToDocumentStart() {
    editor.moveTo(0);
    editor.requestFollowCaret();
  }

  void moveToDocumentEnd() {
    editor.moveTo(editor.getLength());
    editor.requestFollowCaret();
  }

  void selectToDocumentStart() {
    int start = getSelectionStart();

    int anchor = editor.getCaretPosition();

    if (editor.getCaretPosition() > start) {
      anchor = start;
    }

    editor.selectRange(anchor, 0);
    editor.requestFollowCaret();
  }

  void selectToDocumentEnd() {
    int end = getSelectionEnd();

    int anchor = editor.getCaretPosition();

    if (editor.getCaretPosition() < end) {
      anchor = end;
    }

    editor.selectRange(anchor, editor.getLength());
    editor.requestFollowCaret();
  }

  private void replaceTextBlock(int startLine, int endLine, String text) {
    int endOfLinePosition = getLineColumnPosition(endLine + 1) - 1;
    editor.replaceText(getLineColumnPosition(startLine), endOfLinePosition + 1, text);
  }

  private int getCaretLinePosition() {
    return editor.offsetToPosition(editor.getCaretPosition(), Forward).getMinor();
  }

  private int getCaretLineNumber() {
    return editor.offsetToPosition(editor.getCaretPosition(), Forward).getMajor();
  }

  private int getLineColumnPosition(int lineNumber) {
    return editor.getAbsolutePosition(lineNumber, 0);
  }

  private String getLineText(int lineNumber) {
    return getLineText(lineNumber, getLineColumnPosition(lineNumber));
  }

  private String getLineText(int lineNumber, int lineColumnPosition) {
    if (lineNumber == editor.getParagraphs().size() - 1) {
      return editor.getText(
          lineColumnPosition,
          editor.getAbsolutePosition(lineNumber, editor.getParagraph(lineNumber).length()));
    }

    return editor.getText(lineColumnPosition, editor.getAbsolutePosition(lineNumber + 1, 0));
  }

  private int leadingSpaces(String text) {
    int spaces = 0;
    while (spaces < text.length() && text.charAt(spaces) == ' ') {
      spaces++;
    }
    return spaces;
  }

  private int getSelectionStart() {
    return editor.getSelection().getStart();
  }

  private int getSelectionEnd() {
    return editor.getSelection().getEnd();
  }

  private int getStartLine(int start) {
    return editor.offsetToPosition(start, TwoDimensional.Bias.Forward).getMajor();
  }

  private int getEndLine(int end) {
    return editor.offsetToPosition(end, TwoDimensional.Bias.Backward).getMajor();
  }
}
