package net.lxsthw.friends.bukkit.compatibility;

import java.util.logging.Level;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.utils.HLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.BaseComponent;
import net.lxsthw.friends.bukkit.Main;

public class BukkitCore extends Core {

  @Override
  public void dL(HLogger logger, Level level, String message) {
    logger.log(level, message);
  }

  @Override
  public void sM(String playerName, String message) {
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null) {
      player.sendMessage(message);
    }
  }

  @Override
  public void sM(String playerName, BaseComponent... components) {
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null) {
      player.spigot().sendMessage(components);
    }
  }

  @Override
  public String gS() {
    return Main.getInstance().getDescription().getVersion();
  }
}
