package net.lxsthw.friends.profile.container;

import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;

@SuppressWarnings("unchecked")
public class FriendsContainer {

  private DataContainer dataContainer;

  protected FriendsContainer(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public void addFriend(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.add(playerName);
    this.dataContainer.set(array.toString());
  }

  public void removeFriend(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.remove(playerName);
    this.dataContainer.set(array.toString());
  }

  public boolean isFriend(String playerName) {
    return this.dataContainer.getAsJsonArray().contains(playerName);
  }

  public List<String> listFriends() {
    return (List<String>) this.dataContainer.getAsJsonArray().stream().collect(Collectors.toList());
  }
}
