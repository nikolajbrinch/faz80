package dk.nikolajbrinch.faz80.ide.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;

public class JsonUtil {

  final Mapper mapper = new MapperBuilder().build();

  public <T> T fromJson(File file, Class<T> clazz) throws FileNotFoundException {
    return mapper.readObject(new FileInputStream(file), clazz);
  }

  public <T> void toJson(File file, T value) throws FileNotFoundException {
    mapper.writeObject(value, new FileOutputStream(file));
  }
}
