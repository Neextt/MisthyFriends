package net.lxsthw.friends;

import java.io.InputStream;
import java.util.logging.Level;

import net.lxsthw.friends.bungee.Main;
import net.lxsthw.friends.utils.HLogger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.lxsthw.friends.bukkit.compatibility.BukkitConsoleLogger;
import net.lxsthw.friends.bukkit.compatibility.BukkitCore;
import net.lxsthw.friends.bungee.compatibility.BungeeConsoleLogger;
import net.lxsthw.friends.bungee.compatibility.BungeeCore;
import net.lxsthw.friends.compatibility.ConsoleLogger;

public abstract class Core {

  public static final boolean BUNGEE = hasBungeeClass();
  public static final HLogger LOGGER = new HLogger();
  private static final Core METHODS = BUNGEE ? new BungeeCore() : new BukkitCore();
  
  public abstract void dL(HLogger logger, Level level, String message);

  public abstract void sM(String playerName, String message);

  public abstract void sM(String playerName, BaseComponent... components);
  
  public abstract String gS();

  static boolean hasBungeeClass() {
    try {
      Class.forName("net.md_5.bungee.api.ProxyServer");
      return true;
    } catch (ClassNotFoundException ex) {
      return false;
    }
  }

  public static void delayedLog(String message) {
    METHODS.dL(LOGGER, Level.INFO, message);
  }
  
  public static void delayedLog(HLogger logger, Level level, String message) {
    METHODS.dL(logger, level, message);
  }

  public static void sendMessage(String playerName, String message) {
    METHODS.sM(playerName, message);
  }

  public static void sendMessage(String playerName, BaseComponent... components) {
    METHODS.sM(playerName, components);
  }
  
  public static String getVersion() {
    return METHODS.gS();
  }

  public static InputStream getResource(String name) {
    if (BUNGEE) {
      return Main.getInstance().getResourceAsStream(name);
    }

    return net.lxsthw.friends.bukkit.Main.getInstance().getResource(name);
  }

  public static ConsoleLogger getConsoleLogger() {
    if (BUNGEE) {
      return new BungeeConsoleLogger();
    }

    return new BukkitConsoleLogger();
  }
}
