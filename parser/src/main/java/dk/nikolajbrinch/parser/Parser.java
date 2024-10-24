package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.faz80.parser.base.TaskResult;
import java.io.File;
import java.io.IOException;

public interface Parser<S, R extends TaskResult> {

  TaskResult parse(File file) throws IOException;

  default TaskResult parse(String filename) throws IOException {
    return parse(new File(filename));
  }
}
