package net.lxsthw.friends.bungee.cmd;

import net.lxsthw.friends.bungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public abstract class Commands extends Command {
  
  public Commands(String name, String... aliases) {
    super(name, null, aliases);
    ProxyServer.getInstance().getPluginManager().registerCommand(Main.getInstance(), this);
  }
  
  public static void setupCommands() {
    new FriendsCommand();
  }
}
