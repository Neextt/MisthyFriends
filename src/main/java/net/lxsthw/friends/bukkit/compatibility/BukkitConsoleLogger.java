package net.lxsthw.friends.bukkit.compatibility;

import net.lxsthw.friends.compatibility.ConsoleLogger;
import org.bukkit.Bukkit;

public class BukkitConsoleLogger implements ConsoleLogger {
  
  @Override
  public void sendMessage(String message) {
    Bukkit.getConsoleSender().sendMessage(message);
  }
}
