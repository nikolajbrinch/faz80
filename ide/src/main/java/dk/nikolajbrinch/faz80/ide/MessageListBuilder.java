package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.assembler.AssembleMessage;
import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.faz80.linker.LinkMessage;
import dk.nikolajbrinch.faz80.parser.base.BaseMessage;
import dk.nikolajbrinch.faz80.parser.base.MessageType;
import dk.nikolajbrinch.faz80.parser.statements.Statement;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.parser.ParseMessage;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.io.File;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageListBuilder {

  private final Logger logger = LoggerFactory.getLogger();

  public ObservableList<MessageProperty> build(List<BaseMessage> messages, File file) {
    long currentTime = System.currentTimeMillis();

    ObservableList<MessageProperty> data = FXCollections.observableArrayList();

    messages.forEach(
        message -> {
          data.add(
              switch (message) {
                case ParseMessage(MessageType type, String text, AssemblerToken token) ->
                    new MessageProperty(
                        type.getText(),
                        messageFile(file, token.sourceInfo()),
                        token.line().number(),
                        "Parse",
                        token.text(),
                        text);
                case AssembleMessage(MessageType type, String text, Statement statement) ->
                    new MessageProperty(
                        type.getText(),
                        messageFile(file, statement.sourceInfo()),
                        statement.line().number(),
                        "Assemble",
                        "",
                        text);
                case LinkMessage(MessageType type, String text) ->
                    new MessageProperty(type.getText(), "", -1, "Link", "", text);
                default -> null;
              });
        });

    logger.debug(
        "Building message list took: " + (System.currentTimeMillis() - currentTime) + "ms");

    return data;
  }

  private String messageFile(File sourceFile, SourceInfo sourceInfo) {
    File file = new File(sourceInfo.name());

    if (!file.exists()) {
      file = sourceFile;
    }

    return file.exists()
        ? sourceFile.getParentFile().toPath().relativize(file.toPath()).toString()
        : "";
  }
}
