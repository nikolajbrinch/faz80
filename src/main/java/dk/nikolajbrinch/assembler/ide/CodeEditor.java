package dk.nikolajbrinch.assembler.ide;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class CodeEditor extends CodeArea {

  public CodeEditor() {
    super();
    setLineHighlighterFill(Paint.valueOf("lightblue"));
    setParagraphGraphicFactory(LineNumberFactory.get(this));
    setParagraphGraphicFactory(
        lineNumber -> {
          Label lineNo = new Label(String.format("% 5d ", lineNumber + 1));
          lineNo.applyCss();

          return lineNo;
        });
    setOnMouseClicked(event -> setLineHighlighterOn(false));
    textProperty().addListener((obs, oldText, newText) -> setLineHighlighterOn(false));
  }

  public void highlightLine(int lineNumber) {
    moveTo(position(lineNumber - 1, 0).toOffset());
    requestFollowCaret();
    int currentParagraph = getCurrentParagraph();
    int scrollToParagraph =
        Math.max(0, currentParagraph - 10 < 0 ? currentParagraph : currentParagraph - 10);
    showParagraphAtTop(scrollToParagraph);
    setLineHighlighterOn(true);
  }

  public void newText(File file) throws IOException {
    String content = Files.readString(file.toPath());

    replaceText(content);
    moveTo(0);
    requestFollowCaret();
  }
}
