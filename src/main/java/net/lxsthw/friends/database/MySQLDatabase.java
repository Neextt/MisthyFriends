package net.lxsthw.friends.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import net.lxsthw.friends.Language;
import net.lxsthw.friends.profile.container.DataContainer;
import net.lxsthw.friends.utils.StringUtils;

public class MySQLDatabase extends Database {

  private String host;
  private String port;
  private String dbname;
  private String username;
  private String password;

  private Connection connection;
  private ExecutorService executor;

  public MySQLDatabase() {
    this.host = Language.database$mysql$host;
    this.port = Language.database$mysql$porta;
    this.dbname = Language.database$mysql$nome;
    this.username = Language.database$mysql$usuario;
    this.password = Language.database$mysql$senha;

    this.openConnection();
    this.executor = Executors.newCachedThreadPool();

    this.update("CREATE TABLE IF NOT EXISTS `hfriends_perfis` (name VARCHAR(32) NOT NULL,"
        + "friends TEXT, blacklist TEXT, requests TEXT, PRIMARY KEY(name)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
  }

  @Override
  public Map<String, DataContainer> load(String name) {
    Map<String, DataContainer> containerMap = new LinkedHashMap<>();
    CachedRowSet rs = this.query("SELECT * FROM `hfriends_perfis` WHERE LOWER(`name`) = ?", name.toLowerCase());
    if (rs != null) {
      try {
        for (int collumn = 2; collumn <= rs.getMetaData().getColumnCount(); collumn++) {
          containerMap.put(rs.getMetaData().getColumnName(collumn), new DataContainer(rs.getObject(collumn)));
        }
      } catch (SQLException ex) {LOGGER.log(Level.SEVERE, "Nao foi possível carregar os dados do perfil: ", ex);
      }
      
      return containerMap;
    }
    
    containerMap.put("friends", new DataContainer("[]"));
    containerMap.put("blacklist", new DataContainer("[]"));
    containerMap.put("requests", new DataContainer("[]"));
    List<Object> list = new ArrayList<>();
    list.add(name);
    list.addAll(containerMap.values().stream().map(dc -> dc.get()).collect(Collectors.toList()));
    this.execute("INSERT INTO `hfriends_perfis` VALUES (?, " + StringUtils.repeat("?, ", containerMap.size() - 1) + "?)", list.toArray(new Object[list.size()]));
    list.clear();
    list = null;
    return containerMap;
  }

  @Override
  public void save(String name, Map<String, DataContainer> containerMap) {
    StringBuilder sb = new StringBuilder("UPDATE `hfriends_perfis` SET ");
    List<String> keys = new ArrayList<>(containerMap.keySet());
    for (int slot = 0; slot < keys.size(); slot++) {
      String key = keys.get(slot);
      sb.append("`" + key + "` = ?");
      if (slot + 1 == keys.size()) {
        continue;
      }

      sb.append(", ");
    }

    sb.append(" WHERE LOWER(`name`) = ?");

    List<Object> values = new ArrayList<>();
    values.addAll(containerMap.values().stream().map(dc -> dc.get()).collect(Collectors.toList()));
    values.add(name.toLowerCase());
    this.execute(sb.toString(), values.toArray(new Object[values.size()]));

    keys.clear();
    values.clear();
    keys = null;
    values = null;
  }
  
  @Override
  public String exists(String name) {
    try {
      return this.query("SELECT * FROM `hfriends_perfis` WHERE LOWER(`name`) = ?", name.toLowerCase()).getString("name");
    } catch (Exception ex) {
      return null;
    }
  }
  
  @Override
  public CachedRowSet getRequests(String name) {
    return this.query("SELECT `name` FROM `hfriends_perfis` WHERE `requests` LIKE '%" + name + "%'");
  }

  public void openConnection() {
    try {
      boolean reconnected = true;
      if (this.connection == null) {
        reconnected = false;
      }
      this.connection = DriverManager.getConnection(
          "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8", username, password);
      if (reconnected) {
        LOGGER.info("Reconectado ao MySQL!");
        return;
      }

      LOGGER.info("§a[MySQL]§a conexão com o MySQL efetuada com sucesso, criando tabelas...");
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possivel se conectar ao MySQL: ", ex);
    }
  }

  public void closeConnection() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        LOGGER.log(Level.WARNING, "Nao foi possivel fechar a conexao com o MySQL: ", e);
      }
    }
  }

  public Connection getConnection() {
    if (!isConnected()) {
      this.openConnection();
    }

    return connection;
  }

  public boolean isConnected() {
    try {
      return !(connection == null || connection.isClosed() || !connection.isValid(5));
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Nao foi possivel verificar a conexao com o MySQL: ", ex);
      return false;
    }
  }

  public void update(String sql, Object... vars) {
    try {
      PreparedStatement ps = prepareStatement(sql, vars);
      ps.execute();
      ps.close();
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
    }
  }

  public void execute(String sql, Object... vars) {
    executor.execute(() -> {
      update(sql, vars);
    });
  }

  public int updateWithInsertId(String sql, Object... vars) {
    int id = -1;
    try {
      PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      ps.execute();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
      rs.close();
      ps.close();
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel executar um SQL: ", ex);
    }

    return id;
  }

  public PreparedStatement prepareStatement(String query, Object... vars) {
    try {
      PreparedStatement ps = getConnection().prepareStatement(query);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      return ps;
    } catch (SQLException ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel preparar um SQL: ", ex);
    }

    return null;
  }

  public CachedRowSet query(String query, Object... vars) {
    CachedRowSet rowSet = null;
    try {
      Future<CachedRowSet> future = executor.submit(new Callable<CachedRowSet>() {

        @Override
        public CachedRowSet call() {
          try {
            PreparedStatement ps = prepareStatement(query, vars);

            ResultSet rs = ps.executeQuery();
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            rs.close();
            ps.close();

            if (crs.next()) {
              return crs;
            }
          } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Nao foi possivel executar um Requisicao: ", ex);
          }

          return null;
        }
      });

      if (future.get() != null) {
        rowSet = future.get();
      }
    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, "Nao foi possivel chamar uma Futura Tarefa: ", ex);
    }

    return rowSet;
  }
}
