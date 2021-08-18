package net.lxsthw.friends.bungee.listeners;

import java.util.logging.Level;
import javax.sql.rowset.CachedRowSet;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.bungee.Main;
import net.lxsthw.friends.database.Database;
import net.lxsthw.friends.profile.Profile;
import net.lxsthw.friends.utils.HLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import tk.slicecollections.maxteer.player.role.Role;

public class Listeners implements Listener {

  public static final HLogger LOGGER = Core.LOGGER.getModule("Listeners");

  public static void setupListeners() {
    ProxyServer.getInstance().getPluginManager().registerListener(Main.getInstance(), new Listeners());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPostLogin(PostLoginEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PostLoginEvent for ${n} v${v}", () -> {
      Profile.createOrLoadProfile(evt.getPlayer().getName());

      Profile.listProfiles().stream().filter(pf -> pf.getFriends().isFriend(evt.getPlayer().getName()))
          .forEach(pf -> pf.sendMessage("§a" + Role.getColored(evt.getPlayer().getName()) + " §eentrou!"));
    });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerConnect(ServerConnectedEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass ServerConnectedEvent for ${n} v${v}", () -> {
      ProxiedPlayer player = evt.getPlayer();
      int pending = 0;
      CachedRowSet rs = Database.getInstance().getRequests(player.getName());
      if (rs != null) {
        pending = rs.size();
      }

      if (pending > 0) {
        player.sendMessage(TextComponent.fromLegacyText(""));
        player.sendMessage(
            TextComponent.fromLegacyText(" §eVocê possui §d" + pending + " §esolicitaç" + (pending > 1 ? "ões" : "ão") + " de amizade pendente" + (pending > 1 ? "s" : "")
                + "!\n Utilize o comando §d/amigo pedidos §epara visualizar a" + (pending > 1 ? "s" : "") + " solicitaç" + (pending > 1 ? "ões" : "ão") + "."));
        player.sendMessage(TextComponent.fromLegacyText(""));
      }
    });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDisconnect(PlayerDisconnectEvent evt) {
    LOGGER.run(Level.SEVERE, "Could not pass PlayerDisconnectEvent for ${n} v${v}", () -> {
      Profile profile = Profile.unloadProfile(evt.getPlayer().getName());
      if (profile != null) {
        profile.save();
        profile.destroy();
      }

      Profile.listProfiles().stream().filter(pf -> pf.getFriends().isFriend(evt.getPlayer().getName())).forEach(pf -> pf.sendMessage("§c" + Role.getColored(evt.getPlayer().getName()) + " §csaiu!"));
    });
  }
}
