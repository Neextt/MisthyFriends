package net.lxsthw.friends.bungee.compatibility;

import java.util.logging.Level;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.utils.HLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.lxsthw.friends.bungee.Main;

public class BungeeCore extends Core {

  @Override
  public void dL(HLogger logger, Level level, String message) {
    logger.log(level, message);
  }

  @Override
  public void sM(String playerName, String message) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
    if (player != null) {
      player.sendMessage(TextComponent.fromLegacyText(message));
    }
  }

  @Override
  public void sM(String playerName, BaseComponent... components) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
    if (player != null) {
      player.sendMessage(components);
    }
  }
  
  @Override
  public String gS() {
    return Main.getInstance().getDescription().getVersion();
  }
}
