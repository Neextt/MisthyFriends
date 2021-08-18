package net.lxsthw.friends.bukkit.compatibility;

import static net.lxsthw.friends.utils.HConfig.LOGGER;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import net.lxsthw.friends.compatibility.YamlConfiguration;

public class BukkitYamlConfiguration extends YamlConfiguration {

  private FileConfiguration config;

  public BukkitYamlConfiguration(File file) throws IOException {
    super(file);
    this.config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), "UTF-8"));
  }

  @Override
  public boolean createSection(String path) {
    this.config.createSection(path);
    return save();
  }

  @Override
  public boolean set(String path, Object obj) {
    this.config.set(path, obj);
    return save();
  }

  @Override
  public boolean contains(String path) {
    return this.config.contains(path);
  }

  @Override
  public Object get(String path) {
    return this.config.get(path);
  }

  @Override
  public int getInt(String path) {
    return this.getInt(path, 0);
  }

  @Override
  public int getInt(String path, int def) {
    return this.config.getInt(path, def);
  }

  @Override
  public double getDouble(String path) {
    return this.getDouble(path, 0);
  }

  @Override
  public double getDouble(String path, double def) {
    return this.config.getDouble(path, def);
  }

  @Override
  public String getString(String path) {
    return this.config.getString(path);
  }

  @Override
  public boolean getBoolean(String path) {
    return this.config.getBoolean(path);
  }

  @Override
  public List<String> getStringList(String path) {
    return this.config.getStringList(path);
  }

  @Override
  public Set<String> getKeys(boolean flag) {
    return this.getKeys(flag);
  }

  @Override
  public boolean reload() {
    try {
      this.config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), "UTF-8"));
      return true;
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Unexpected error ocurred reloading file " + file.getName() + ": ", ex);
      return false;
    }
  }

  @Override
  public boolean save() {
    try {
      config.save(file);
      return true;
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Unexpected error ocurred saving file " + file.getName() + ": ", ex);
      return false;
    }
  }
}
