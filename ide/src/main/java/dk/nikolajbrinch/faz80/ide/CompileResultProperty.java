package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.assembler.AssembleResult;
import dk.nikolajbrinch.faz80.ide.ast.AstTreeValue;
import dk.nikolajbrinch.faz80.linker.LinkResult;
import dk.nikolajbrinch.faz80.parser.AssemblerParseResult;
import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

public class CompileResultProperty {

  private final SimpleBooleanProperty hasErrors = new SimpleBooleanProperty();

  private final SimpleListProperty<BaseMessage> messages =
      new SimpleListProperty<>(FXCollections.emptyObservableList());

  private final SimpleObjectProperty<AssemblerParseResult> parseResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<AssembleResult> assembleResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<LinkResult> linkResult = new SimpleObjectProperty<>(null);

  private final SimpleObjectProperty<TreeItem<AstTreeValue>> astTree =
      new SimpleObjectProperty<>(null);

  public CompileResultProperty() {
    this(false, List.of(), null, null, null, null);
  }

  public CompileResultProperty(
      boolean hasErrors,
      List<BaseMessage> messages,
      AssemblerParseResult parseResult,
      AssembleResult assembleResult,
      LinkResult linkResult,
      TreeItem<AstTreeValue> astTree) {
    setHasErrors(hasErrors);
    setMessages(messages);
    setParseResult(parseResult);
    setAssembleResult(assembleResult);
    setLinkResult(linkResult);
    setAstTree(astTree);
  }

  public boolean hasErrors() {
    return hasErrors.get();
  }

  public void setHasErrors(boolean value) {
    hasErrors.set(value);
  }

  public SimpleBooleanProperty hasErrorsProperty() {
    return hasErrors;
  }

  public List<BaseMessage> getMessages() {
    return messages.get();
  }

  public void setMessages(List<BaseMessage> value) {
    messages.set(FXCollections.observableList(value));
  }

  public SimpleListProperty<BaseMessage> messagesProperty() {
    return messages;
  }

  public AssemblerParseResult getParseResult() {
    return parseResult.get();
  }

  public void setParseResult(AssemblerParseResult value) {
    parseResult.set(value);
  }

  public SimpleObjectProperty<AssemblerParseResult> parseResultProperty() {
    return parseResult;
  }

  public AssembleResult getAssembleResult() {
    return assembleResult.get();
  }

  public void setAssembleResult(AssembleResult value) {
    assembleResult.set(value);
  }

  public SimpleObjectProperty<LinkResult> linkResultProperty() {
    return linkResult;
  }

  public LinkResult getLinkResult() {
    return linkResult.get();
  }

  public void setLinkResult(LinkResult value) {
    linkResult.set(value);
  }

  public SimpleObjectProperty<AssembleResult> assemblResulteProperty() {
    return assembleResult;
  }

  public TreeItem<AstTreeValue> getAstTree() {
    return astTree.get();
  }

  public void setAstTree(TreeItem<AstTreeValue> value) {
    astTree.set(value);
  }

  public SimpleObjectProperty<TreeItem<AstTreeValue>> astTreeProperty() {
    return astTree;
  }
}
