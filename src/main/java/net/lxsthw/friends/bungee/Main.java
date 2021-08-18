package net.lxsthw.friends.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.Language;
import net.lxsthw.friends.database.Database;
import net.lxsthw.friends.utils.FileUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.lxsthw.friends.bungee.cmd.Commands;
import net.lxsthw.friends.bungee.listeners.Listeners;
import net.lxsthw.friends.profile.Profile;

public class Main extends Plugin {
  
  private static Main instance;
  private static boolean validInit;
  
  public Main() {
    instance = this;
  }
  
  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    
    Language.setupSettings();
    Database.setupDatabase();
    
    Commands.setupCommands();
    Listeners.setupListeners();
    
    validInit = true;
    Core.LOGGER.log(Level.INFO, "O plugin foi ativado com sucesso.");
  }
  
  @Override
  public void onDisable() {
    if (validInit) {
      Profile.listProfiles().forEach(Profile::save);
    }

    Core.LOGGER.log(Level.INFO, "O plugin foi desativado.");
  }
  
  private Configuration config;

  public void saveDefaultConfig() {
    File file = new File("plugins/MisthyFriends/config.yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      FileUtils.copyFile(this.getResourceAsStream("config.yml"), file);
    }

    try {
      this.config = YamlConfiguration.getProvider(YamlConfiguration.class)
          .load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  public Configuration getConfig() {
    return config;
  }
  
  public static Main getInstance() {
    return instance;
  }
}
