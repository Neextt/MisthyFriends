package net.lxsthw.friends.bukkit;

import java.util.logging.Level;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.Language;
import net.lxsthw.friends.bukkit.cmd.Commands;
import net.lxsthw.friends.bukkit.listeners.Listeners;
import net.lxsthw.friends.database.Database;
import org.bukkit.plugin.java.JavaPlugin;
import net.lxsthw.friends.profile.Profile;

public class Main extends JavaPlugin {

  private static Main instance;
  private static boolean validInit;

  public Main() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    
    Language.setupSettings();
    Database.setupDatabase();

    Commands.setupCommands();
    Listeners.setupListeners();

    validInit = true;
    Core.LOGGER.info("Detected version: v1_8_R3");
    Core.LOGGER.log(Level.INFO, "O plugin foi ativado com sucesso.");
  }

  @Override
  public void onDisable() {
    if (validInit) {
      Profile.listProfiles().forEach(Profile::save);
    }

    Core.LOGGER.log(Level.INFO, "O plugin foi desativado com sucesso.");
  }

  public static Main getInstance() {
    return instance;
  }
}
