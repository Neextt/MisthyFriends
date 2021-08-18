package net.lxsthw.friends.profile.container;

import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;

@SuppressWarnings("unchecked")
public class RequestsContainer {

  private DataContainer dataContainer;

  protected RequestsContainer(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public void addRequest(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.add(playerName);
    this.dataContainer.set(array.toString());
  }

  public void removeRequest(String playerName) {
    JSONArray array = this.dataContainer.getAsJsonArray();
    array.remove(playerName);
    this.dataContainer.set(array.toString());
  }

  public boolean isRequested(String playerName) {
    return this.dataContainer.getAsJsonArray().contains(playerName);
  }

  public List<String> listRequests() {
    return (List<String>) this.dataContainer.getAsJsonArray().stream().collect(Collectors.toList());
  }
}
