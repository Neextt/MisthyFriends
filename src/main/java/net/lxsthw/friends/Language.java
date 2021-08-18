package net.lxsthw.friends;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.lxsthw.friends.utils.HConfig;
import net.lxsthw.friends.utils.HLogger;
import net.lxsthw.friends.utils.MWriter.YamlEntryInfo;
import net.lxsthw.friends.utils.StringUtils;

@SuppressWarnings({"rawtypes"})
public class Language {
  @YamlEntryInfo(annotation = "Tipos de bancos de dados disponíveis atualmente: MySQL")
  public static String database$tipo = "MySQL";
  @YamlEntryInfo(annotation = "Host para acessar o MySQL")
  public static String database$mysql$host = "localhost";
  @YamlEntryInfo(annotation = "Porta para acessar o MySQL")
  public static String database$mysql$porta = "3306";
  @YamlEntryInfo(annotation = "Nome do banco de dados no MySQL")
  public static String database$mysql$nome = "server";
  @YamlEntryInfo(annotation = "Usuário de acesso ao MySQL")
  public static String database$mysql$usuario = "root";
  @YamlEntryInfo(annotation = "Senha de acesso ao MySQL")
  public static String database$mysql$senha = "";

  public static final HLogger LOGGER = Core.LOGGER.getModule("CONFIG-MYSQL");
  private static final HConfig CONFIG = HConfig.getConfig("mysql-config");

  public static void setupSettings() {
    boolean save = false;
    for (Field field : Language.class.getDeclaredFields()) {
      if (field.isAnnotationPresent(YamlEntryInfo.class)) {
        YamlEntryInfo entryInfo = field.getAnnotation(YamlEntryInfo.class);
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = null;

          if (CONFIG.contains(nativeName)) {
            value = CONFIG.get(nativeName);
            if (value instanceof String) {
              value = StringUtils.formatColors((String) value).replace("\\n", "\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.formatColors((String) v).replace("\\n", "\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            field.set(null, value);
          } else {
            value = field.get(null);
            if (value instanceof String) {
              value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            save = true;
          }
        } catch (ReflectiveOperationException e) {
          LOGGER.log(Level.WARNING, "Unexpected error on settings file: ", e);
        }
      }
    }

    if (save) {
      CONFIG.reload();
      Core.delayedLog("§fA config §econfig.yml §ffoi modificada ou criada.");
    }
  }
}
