package net.lxsthw.friends.profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.lxsthw.friends.Core;
import net.lxsthw.friends.database.Database;
import net.lxsthw.friends.profile.container.BlackListContainer;
import net.lxsthw.friends.profile.container.DataContainer;
import net.lxsthw.friends.profile.container.FriendsContainer;
import net.lxsthw.friends.profile.container.RequestsContainer;
import net.md_5.bungee.api.chat.BaseComponent;

public class Profile {

  private String playerName;

  private Map<String, DataContainer> containerMap;

  private Profile() {}
  
  public void sendMessage(String message) {
    Core.sendMessage(this.playerName, message);
  }
  
  public void sendMessage(BaseComponent... components) {
    Core.sendMessage(this.playerName, components);
  }
  
  public void save() {
    Database.getInstance().save(this.playerName, this.containerMap);
  }

  public void destroy() {
    this.playerName = null;
    this.containerMap.values().forEach(dc -> dc.set(null));
    this.containerMap.clear();
    this.containerMap = null;
  }

  public String getPlayerName() {
    return this.playerName;
  }
  
  public boolean isCurrentOnline() {
    return profiles.containsKey(playerName.toLowerCase());
  }

  public FriendsContainer getFriends() {
    return this.containerMap.get("friends").getFriendsContainer();
  }

  public BlackListContainer getBlackList() {
    return this.containerMap.get("blacklist").getBlackListContainer();
  }

  public RequestsContainer getRequests() {
    return this.containerMap.get("requests").getRequestsContainer();
  }

  private static Map<String, Profile> profiles = new HashMap<>();

  public static Profile createOrLoadProfile(String playerName) {
    Profile profile = profiles.get(playerName.toLowerCase());
    if (profile == null) {
      profile = new Profile();
      profile.playerName = playerName;
      profile.containerMap = Database.getInstance().load(playerName);
      profiles.put(playerName.toLowerCase(), profile);
    }

    return profile;
  }

  public static Profile loadIfExists(String playerName) {
    Profile profile = profiles.get(playerName.toLowerCase());
    if (profile == null) {
      playerName = Database.getInstance().exists(playerName);
      if (playerName != null) {
        profile = new Profile();
        profile.playerName = playerName;
        profile.containerMap = Database.getInstance().load(playerName);
      }
    }

    return profile;
  }

  public static Profile unloadProfile(String playerName) {
    return profiles.remove(playerName.toLowerCase());
  }
  
  public static boolean isOnline(String playerName) {
    return profiles.containsKey(playerName.toLowerCase());
  }
  
  public static Collection<Profile> listProfiles() {
    return profiles.values();
  }
}
