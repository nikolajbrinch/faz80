package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.assembler.AssembleException;
import dk.nikolajbrinch.faz80.linker.LinkException;
import dk.nikolajbrinch.faz80.base.errors.BaseError;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.scanner.SourceInfo;
import java.io.File;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ErrorListBuilder {

  public ObservableList<ErrorProperty> build(List<BaseError<?>> errors, File file) {
    ObservableList<ErrorProperty> data = FXCollections.observableArrayList();

    errors.forEach(
        error -> {
          Exception exception = error.exception();
          data.add(
              switch (exception) {
                case ParseException parseException ->
                    new ErrorProperty(
                        errorFile(file, parseException.getToken().sourceInfo()),
                        "Error",
                        parseException.getToken().line().number(),
                        "Parse",
                        parseException.getToken().text(),
                        parseException.getMessage());
                case AssembleException assembleException ->
                    new ErrorProperty(
                        errorFile(file, assembleException.getStatement().sourceInfo()),
                        "Error",
                        assembleException.getStatement().line().number(),
                        "Assemble",
                        "",
                        assembleException.getMessage());
                case LinkException linkException ->
                    new ErrorProperty("", "Error", -1, "Link", "", linkException.getMessage());
                default -> null;
              });
        });

    return data;
  }
  ;

  private String errorFile(File sourceFile, SourceInfo sourceInfo) {
    File file = new File(sourceInfo.name());

    if (!file.exists()) {
      file = sourceFile;
    }

    return file.exists()
        ? sourceFile.getParentFile().toPath().relativize(file.toPath()).toString()
        : "";
  }
}
