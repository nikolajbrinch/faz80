package dk.nikolajbrinch.assembler.ide;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import static javafx.scene.input.KeyCombination.META_DOWN;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;

import dk.nikolajbrinch.parser.Logger;
import dk.nikolajbrinch.parser.impl.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.wellbehaved.event.Nodes;

public class CodeEditor extends CodeArea {

  private final Logger logger = LoggerFactory.getLogger();

  private final KeyboardHandlerDelegate keyboardHandlerDelegate;

  private int tabStop = 2;

  public CodeEditor() {
    super();
    setLineHighlighterFill(Paint.valueOf("lightblue"));
    setParagraphGraphicFactory(LineNumberFactory.get(this));
    setParagraphGraphicFactory(LineNumberFactory.get(this, digits -> "%1$5s"));
    setOnMouseClicked(event -> setLineHighlighterOn(false));
    textProperty().addListener((obs, oldText, newText) -> setLineHighlighterOn(false));
    this.keyboardHandlerDelegate = new KeyboardHandlerDelegate(this);
    Nodes.addInputMap(this, consume(keyPressed(TAB, SHIFT_DOWN), event -> keyboardHandlerDelegate.untab(tabStop)));
    Nodes.addInputMap(this, consume(keyPressed(TAB), event -> keyboardHandlerDelegate.tab(tabStop)));
    Nodes.addInputMap(this, consume(keyPressed(LEFT, META_DOWN), event -> keyboardHandlerDelegate.moveToLineStart()));
    Nodes.addInputMap(this, consume(keyPressed(RIGHT, META_DOWN), event -> keyboardHandlerDelegate.moveToLineEnd()));
    Nodes.addInputMap(this, consume(keyPressed(UP, META_DOWN), event -> keyboardHandlerDelegate.moveToDocumentStart()));
    Nodes.addInputMap(this, consume(keyPressed(DOWN, META_DOWN), event -> keyboardHandlerDelegate.moveToDocumentEnd()));
    Nodes.addInputMap(this, consume(keyPressed(LEFT, SHIFT_DOWN, META_DOWN), event -> keyboardHandlerDelegate.selectToLineStart()));
    Nodes.addInputMap(this, consume(keyPressed(RIGHT, SHIFT_DOWN, META_DOWN), event -> keyboardHandlerDelegate.selectToLineEnd()));
    Nodes.addInputMap(this, consume(keyPressed(UP, SHIFT_DOWN, META_DOWN), event -> keyboardHandlerDelegate.selectToDocumentStart()));
    Nodes.addInputMap(this, consume(keyPressed(DOWN, SHIFT_DOWN, META_DOWN), event -> keyboardHandlerDelegate.selectToDocumentEnd()));
  }

  public void highlightLine(int lineNumber) {
    moveTo(position(lineNumber - 1, 0).toOffset());
    requestFollowCaret();
    int currentParagraph = getCurrentParagraph();
    int scrollToParagraph =
        Math.max(0, currentParagraph - 10 < 0 ? currentParagraph : currentParagraph - 10);
    showParagraphAtTop(scrollToParagraph);
    setLineHighlighterOn(true);
    requestFocus();
  }

  public void newText(File file) throws IOException {
    String content = Files.readString(file.toPath());

    replaceText(content);
    moveTo(0);
    requestFollowCaret();
  }


}
