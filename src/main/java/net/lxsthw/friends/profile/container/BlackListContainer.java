package net.lxsthw.friends.profile.container;

import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;

@SuppressWarnings("unchecked")
public class BlackListContainer {

  private DataContainer dataContainer;

  protected BlackListContainer(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public void addToBlackList(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.add(playerName);
    this.dataContainer.set(array.toString());
  }

  public void removeFromBlackList(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.remove(playerName);
    this.dataContainer.set(array.toString());
  }

  public boolean isInBlackList(String playerName) {
    return this.dataContainer.getAsJsonArray().contains(playerName);
  }

  public List<String> listBlackListed() {
    return (List<String>) this.dataContainer.getAsJsonArray().stream().collect(Collectors.toList());
  }
}
