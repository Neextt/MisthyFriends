package net.lxsthw.friends.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.lxsthw.friends.Core;
import net.lxsthw.friends.compatibility.ConsoleLogger;

public class HLogger extends Logger {

  private String prefix;
  private ConsoleLogger sender;

  public HLogger() {
    this("[MisthyFriends] ");
  }

  public HLogger(String prefix) {
    super(prefix, null);
    this.prefix = prefix;
    this.sender = Core.getConsoleLogger();
  }

  public void run(Level level, String method, Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception ex) {
      this.log(level, method.replace("${n}", "MisthyFriends").replace("${v}", Core.getVersion()), ex);
    }
  }

  public void info(String message) {
    this.log(Level.INFO, message);
  }

  public void warning(String message) {
    this.log(Level.WARNING, message);
  }

  public void severe(String message) {
    this.log(Level.SEVERE, message);
  }

  @Override
  public void log(Level level, String message) {
    this.hackLog(level, message, null);
  }

  @Override
  public void log(Level level, String message, Throwable throwable) {
    this.hackLog(level, message, throwable);
  }

  private void hackLog(Level level, String message, Throwable throwable) {
    StringBuilder result = new StringBuilder(this.prefix + message);
    if (throwable != null) {
      result.append("\n" + throwable.getLocalizedMessage());
      for (StackTraceElement ste : throwable.getStackTrace()) {
        if (ste.toString().contains("com.hylekdev.flexxsz")) {
          result.append("\n" + ste.toString());
          continue;
        }
      }
    }

    this.sender.sendMessage(MLevel.valueOf(level.getName()).format(result.toString()));
  }

  public HLogger getModule(String module) {
    return new HLogger(this.prefix + "[" + module + "] ");
  }

  public static enum MLevel {
      INFO("§3"),
      WARNING("§e"),
      SEVERE("§c");

    private String color;

    MLevel(String color) {
      this.color = color;
    }

    public String format(String message) {
      return this.color + message;
    }
  }
}
