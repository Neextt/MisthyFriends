package net.lxsthw.friends.bungee.compatibility;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.lxsthw.friends.compatibility.ConsoleLogger;

public class BungeeConsoleLogger implements ConsoleLogger {
  
  @Override
  public void sendMessage(String message) {
    ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(message));
  }
}
