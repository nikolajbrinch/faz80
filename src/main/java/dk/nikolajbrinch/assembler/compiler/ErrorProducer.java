package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.parser.BaseError;
import dk.nikolajbrinch.parser.BaseException;
import java.util.List;

public interface ErrorProducer<E extends BaseException, T extends BaseError<E>> {

  boolean hasErrors();

  List<T> getErrors();

}
