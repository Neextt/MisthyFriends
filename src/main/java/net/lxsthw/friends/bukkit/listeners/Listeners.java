package net.lxsthw.friends.bukkit.listeners;

import java.util.logging.Level;
import javax.sql.rowset.CachedRowSet;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.utils.HLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import net.lxsthw.friends.bukkit.Main;
import net.lxsthw.friends.database.Database;
import net.lxsthw.friends.profile.Profile;
import tk.slicecollections.maxteer.player.role.Role;

public class Listeners implements Listener {
    public static final HLogger LOGGER;

    public Listeners() {
    }

    public static void setupListeners() {
      Bukkit.getPluginManager().registerEvents(new Listeners(), Main.getInstance());
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerLogin(PlayerLoginEvent evt) {
      LOGGER.run(Level.SEVERE, "Could not pass PlayerLoginEvent for ${n} v${v}", () -> {
        if (evt.getResult() == Result.ALLOWED) {
          Profile.createOrLoadProfile(evt.getPlayer().getName());
        }

      });
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerJoin(PlayerJoinEvent evt) {
      LOGGER.run(Level.SEVERE, "Could not pass PlayerJoinEvent for ${n} v${v}", () -> {
        Player player = evt.getPlayer();
        int pending = 0;
        CachedRowSet rs = Database.getInstance().getRequests(player.getName());
        if (rs != null) {
          pending = rs.size();
        }

        if (pending > 0) {
          player.sendMessage("");
          player.sendMessage(" §aVocê possui §f" + pending + " §asolicitaç" + (pending > 1 ? "ões" : "ão") + " de amizade pendente" + (pending > 1 ? "s" : "") + "!\n Utilize o comando §d/amigo pedidos §epara visualizar a" + (pending > 1 ? "s" : "") + " solicitaç" + (pending > 1 ? "ões" : "ão") + ".");
          player.sendMessage("");
        }

        Profile.listProfiles().stream().filter((profile) -> {
          return profile.getFriends().isFriend(player.getName());
        }).forEach((profile) -> {
          profile.sendMessage("§7" + Role.getColored(player.getName()) + " §eentrou!");
        });
      });
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerQuit(PlayerQuitEvent evt) {
      LOGGER.run(Level.SEVERE, "Could not pass PlayerQuitEvent for ${n} v${v}", () -> {
        Profile profile = Profile.unloadProfile(evt.getPlayer().getName());
        if (profile != null) {
          profile.save();
          profile.destroy();
        }

        Profile.listProfiles().stream().filter((pf) -> {
          return pf.getFriends().isFriend(evt.getPlayer().getName());
        }).forEach((pf) -> {
          profile.sendMessage("§c" + Role.getColored(evt.getPlayer().getName()) + " §esaiu!");
            if (profile != null) {
                profile.save();
                profile.destroy();
            }
        });
      });
    }

    static {
      LOGGER = Core.LOGGER.getModule("Listeners");
    }
  }
