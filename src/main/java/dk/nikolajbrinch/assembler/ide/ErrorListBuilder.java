package dk.nikolajbrinch.assembler.ide;

import dk.nikolajbrinch.assembler.compiler.AssembleException;
import dk.nikolajbrinch.assembler.linker.LinkException;
import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.ParseException;
import dk.nikolajbrinch.parser.SourceInfo;
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
                        parseException.getToken().line().number(),
                        "Parse",
                        parseException.getToken().text(),
                        parseException.getMessage());
                case AssembleException assembleException ->
                    new ErrorProperty(
                        errorFile(file, assembleException.getStatement().sourceInfo()),
                        assembleException.getStatement().line().number(),
                        "Assemble",
                        "",
                        assembleException.getMessage());
                case LinkException linkException ->
                    new ErrorProperty("", -1, "Link", "", linkException.getMessage());
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
