package net.lxsthw.friends.database;

import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import net.lxsthw.friends.Core;
import net.lxsthw.friends.profile.container.DataContainer;
import net.lxsthw.friends.utils.HLogger;

public abstract class Database {
  
  public abstract Map<String, DataContainer> load(String name);
  
  public abstract void save(String name, Map<String, DataContainer> containerMap);
  
  public abstract String exists(String name);
  
  public abstract CachedRowSet getRequests(String name);
  public static final HLogger LOGGER = Core.LOGGER.getModule("DATABASE");
  private static Database instance;
  
  public static void setupDatabase() {
    /*if (Settings.database$tipo.equalsIgnoreCase("mysql")) {
      instance = new MySQLDatabase();
    } else {
      instance = new FileDatabase();
    }*/
    
    instance = new MySQLDatabase();
  }
  
  public static Database getInstance() {
    return instance;
  }
}
