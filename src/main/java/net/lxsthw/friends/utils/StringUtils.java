package net.lxsthw.friends.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)(§)[0-9A-FK-OR]");

  public static String stripColors(final String input) {
    if (input == null) {
      return null;
    }

    return COLOR_PATTERN.matcher(input).replaceAll("");
  }

  public static String formatColors(String textToFormat) {
    return translateAlternateColorCodes('&', textToFormat);
  }

  public static String deformatColors(String textToFormat) {
    Matcher matcher = COLOR_PATTERN.matcher(textToFormat);
    while (matcher.find()) {
      String color = matcher.group();
      textToFormat = textToFormat.replaceFirst(Pattern.quote(color), Matcher.quoteReplacement("&" + color.substring(1)));
    }

    return textToFormat;
  }

  public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
    Pattern pattern = Pattern.compile("(?i)(" + String.valueOf(altColorChar) + ")[0-9A-FK-OR]");

    Matcher matcher = pattern.matcher(textToTranslate);
    while (matcher.find()) {
      String color = matcher.group();
      textToTranslate = textToTranslate.replaceFirst(Pattern.quote(color), Matcher.quoteReplacement("§" + color.substring(1)));
    }

    return textToTranslate;
  }

  public static String getFirstColor(String input) {
    Matcher matcher = COLOR_PATTERN.matcher(input);
    String first = "";
    if (matcher.find()) {
      first = matcher.group();
    }

    return first;
  }

  public static String getLastColor(String input) {
    Matcher matcher = COLOR_PATTERN.matcher(input);
    String last = "";
    while (matcher.find()) {
      last = matcher.group();
    }

    return last;
  }

  public static String capitalise(String toCapitalise) {
    return capitalise(toCapitalise, ' ');
  }

  public static String capitalise(String toCapitalise, char delimiter) {
    StringBuilder append = new StringBuilder();
    String[] splitter = toCapitalise.split(String.valueOf(delimiter));
    for (String splitted : splitter) {
      append.append(splitted.substring(0, 1).toUpperCase() + splitted.substring(1).toLowerCase());
      append.append(delimiter);
    }

    append.deleteCharAt(append.length() - 1);
    return append.toString();
  }

  public static String repeat(String repeat, int amount) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < amount; i++) {
      sb.append(repeat);
    }

    return sb.toString();
  }

  public static <T> String join(T[] array, int index, String separator) {
    StringBuilder joined = new StringBuilder();
    for (int slot = index; slot < array.length; slot++) {
      joined.append(array[slot].toString() + (slot + 1 == array.length ? "" : separator));
    }

    return joined.toString();
  }

  public static <T> String join(T[] array, String separator) {
    return join(array, 0, separator);
  }

  public static <T> String join(Collection<T> collection, String separator) {
    return join(collection.toArray(new Object[collection.size()]), separator);
  }

  public static String[] split(String toSplit, int length) {
    return split(toSplit, length, false);
  }

  public static String[] split(String toSplit, int length, boolean ignoreIncompleteWords) {
    StringBuilder result = new StringBuilder(), current = new StringBuilder();

    char[] arr = toSplit.toCharArray();
    for (int charId = 0; charId < arr.length; charId++) {
      char character = arr[charId];
      if (current.length() == length) {
        if (!ignoreIncompleteWords) {
          List<Character> removedChars = new ArrayList<>();
          for (int l = current.length() - 1; l > 0; l--) {
            if (current.charAt(l) == ' ') {
              current.deleteCharAt(l);
              result.append(current.toString() + "\n");
              Collections.reverse(removedChars);
              current = new StringBuilder(join(removedChars, ""));
              break;
            }

            removedChars.add(current.charAt(l));
            current.deleteCharAt(l);
          }

          removedChars.clear();
          removedChars = null;
          if (character == ' ') {
            continue;
          }
        } else {
          result.append(current.toString() + "\n");
          current = new StringBuilder();
        }
      }

      current.append(current.length() == 0 && character == ' ' ? "" : character);
      if (charId + 1 == arr.length) {
        result.append(current.toString() + "\n");
      }
    }

    return result.toString().split("\n");
  }
}
