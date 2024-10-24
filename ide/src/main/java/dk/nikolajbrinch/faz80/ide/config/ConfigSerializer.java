package dk.nikolajbrinch.faz80.ide.config;

import jakarta.json.stream.JsonParsingException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.johnzon.core.JsonReaderImpl.NothingToRead;

public class ConfigSerializer {

  private JsonUtil jsonUtil = new JsonUtil();

  public ConfigValues load() throws FileNotFoundException {
    File configFile = getConfigFile();

    ConfigValues config;

    if (!configFile.exists()) {
      config = resetConfig();
    } else {
      try {
        config = jsonUtil.fromJson(configFile, ConfigValues.class);
      } catch (JsonParsingException | NothingToRead e) {
        config = resetConfig();
      }
    }

    return config;
  }

  private ConfigValues resetConfig() throws FileNotFoundException {
    ConfigValues config = new ConfigValues();
    save(config);

    return config;
  }

  public void save(ConfigValues config) throws FileNotFoundException {
    File configFile = getConfigFile();
    jsonUtil.toJson(configFile, config);
  }

  private File getConfigFile() {
    File location = getConfigLocation();

    return new File(location, "config.json");
  }

  private File getConfigLocation() {
    String home = System.getProperty("user.home");
    File location = new File(home, ".faz80");

    if (!location.exists()) {
      location.mkdirs();
    }

    return location;
  }
}
