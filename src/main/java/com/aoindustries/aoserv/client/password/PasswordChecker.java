/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.password;

import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.IoUtils;
import com.aoapps.lang.zip.CorrectedGZIPInputStream;
import com.aoindustries.aoserv.client.account.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Performs password checking for all password protected
 * services.
 *
 * @author  AO Industries, Inc.
 */
public final class PasswordChecker {

  /** Make no instances. */
  private PasswordChecker() {
    throw new AssertionError();
  }

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, PasswordChecker.class);

  /**
   * The different ways months may be represented in the English.
   */
  private static final String[] months = {
      "jan", "january",
      "feb", "february",
      "mar", "march",
      "apr", "april",
      "may",
      "jun", "june",
      "jul", "july",
      "aug", "august",
      "sep", "september",
      "nov", "november",
      "dec", "december"
  };

  private static final String GOOD_KEY = "good";

  /**
   * The categories that are checked.
   */
  private static final String[] categoryKeys = {
      "category.length",
      "category.characters",
      "category.case",
      "category.dates",
      "category.dictionary"
  };
  public static final int NUM_CATEGORIES = categoryKeys.length;

  private static byte[] cachedWords;

  public static class Result {
    private final String category;
    private String result;

    private Result(String category) {
      this.category = category;
      this.result = RESOURCES.getMessage(GOOD_KEY);
    }

    public String getCategory() {
      return category;
    }

    public String getResult() {
      return result;
    }

    @Override
    public String toString() {
      return category + ": " + result;
    }
  }

  public static List<Result> getAllGoodResults() {
    List<Result> results = new ArrayList<>(NUM_CATEGORIES);
    for (int c = 0; c < NUM_CATEGORIES; c++) {
      results.add(new Result(RESOURCES.getMessage(categoryKeys[c])));
    }
    return results;
  }

  public enum PasswordStrength {
    SUPER_LAX,
    MODERATE,
    STRICT
  }

  public static List<Result> checkPassword(User.Name username, String password, PasswordStrength strength) throws IOException {
    if (strength == null) {
      throw new IllegalArgumentException("strength == null");
    }
    List<Result> results = getAllGoodResults();
    int passwordLen = password.length();
    if (passwordLen > 0) {
      /*
       * Check the length of the password
       *
       * Must be at least eight characters
       */
      if (passwordLen < (strength == PasswordStrength.SUPER_LAX ? 6 : 8)) {
        results.get(0).result = RESOURCES.getMessage(strength == PasswordStrength.SUPER_LAX ? "length.atLeastSix" : "length.atLeastEight");
      }

      /*
       * Gather password stats
       */
      int lowercount = 0;
      int uppercount = 0;
      int numbercount = 0;
      int specialcount = 0;
      int ch;
      for (int c = 0; c < passwordLen; c++) {
        if ((ch = password.charAt(c)) >= 'A' && ch <= 'Z') {
          uppercount++;
        } else if (ch >= 'a' && ch <= 'z') {
          lowercount++;
        } else if (ch >= '0' && ch <= '9') {
          numbercount++;
        } else if (ch <= ' ') {
          specialcount++;
        }
      }

      /*
       * Must use numbers and/or punctuation
       *
       * 1) Must contain numbers/punctuation
       * 2) Must not be all numbers
       * 3) Must not contain a space
       */
      if ((numbercount + specialcount) == passwordLen) {
        results.get(1).result = RESOURCES.getMessage("characters.notOnlyNumbers");
      } else if (strength != PasswordStrength.SUPER_LAX && (lowercount + uppercount + specialcount) == passwordLen) {
        results.get(1).result = RESOURCES.getMessage("characters.numbersAndPunctuation");
      } else if (password.indexOf(' ') != -1) {
        results.get(1).result = RESOURCES.getMessage("characters.notContainSpace");
      }

      /*
       * Must use different cases
       *
       * If more than one letter exists, force different case
       */
      if (
          strength != PasswordStrength.SUPER_LAX
              && (
              (lowercount > 1 && uppercount == 0)
                  || (uppercount > 1 && lowercount == 0)
                  || (lowercount == 0 && uppercount == 0)
            )
      ) {
        results.get(2).result = RESOURCES.getMessage("case.capitalAndLower");
      }

      /*
       * Generate the backwards version of the password
       */
      String backwards;
      {
        char[] backwardsChars = new char[passwordLen];
        for (int c = 0; c < passwordLen; c++) {
          backwardsChars[c] = password.charAt(passwordLen - c - 1);
        }
        backwards = new String(backwardsChars);
      }

      /*
       * Must not be the same as your username
       */
      if (username != null && username.toString().equalsIgnoreCase(password)) {
        results.get(4).result = RESOURCES.getMessage("dictionary.notSameAsUsername");
      }

      /*
       * Must not contain a date
       *
       * Does not contain, forwards or backwards and case insensitive (FBCI), jan ... dec<br>
       * Removed -> Does not contain the two digit representation of the current year, last year, or
       * Removed -> next year, and a month 1-12 (strict mode only)
       */
      if (strength != PasswordStrength.SUPER_LAX) {
        String lowerf = password.toLowerCase();
        String lowerb = backwards.toLowerCase();
        boolean goodb = true;
        int len = months.length;
        for (int c = 0; c < len; c++) {
          String month = months[c].toLowerCase();
          if (lowerf.contains(month) || lowerb.contains(month)) {
            goodb = false;
            break;
          }
        }
        if (!goodb) {
          results.get(3).result = RESOURCES.getMessage("dates.noDate");
        }

        if (results.get(4).result.equals(RESOURCES.getMessage(GOOD_KEY))) {
          /*
           * Dictionary check
           *
           * Must not contain a dictionary word, forward or backword and case insensitive,
           * that is longer than half the length of the password if strict or 2 characters if not strict.
           */
          byte[] words = getDictionary();

          int maxAllowedDictLen = strength == PasswordStrength.STRICT ? 3 : (passwordLen / 2);

          // Search through each dictionary word
          int wordslen = words.length;
          int pos = 0;
          String longest = "";
          boolean longestForwards = true;
          while (pos < wordslen) {
            // Find the beginning of the next word
            while (pos < wordslen && words[pos] <= ' ') {
              pos++;
            }

            // Search to the end of the word
            int startpos = pos;
            while (pos < wordslen && words[pos] > ' ') {
              pos++;
            }

            // Get the word
            int wordlen = pos - startpos;
            if (wordlen > maxAllowedDictLen) {
              if (indexOfIgnoreCase(password, words, startpos, wordlen) != -1) {
                if (longestForwards ? (wordlen > longest.length()) : (wordlen >= longest.length())) {
                  longest = new String(words, startpos, wordlen);
                  longestForwards = true;
                }
              } else if (indexOfIgnoreCase(backwards, words, startpos, wordlen) != -1) {
                if (wordlen > longest.length()) {
                  longest = new String(words, startpos, wordlen);
                  longestForwards = false;
                }
              }
            }
          }
          if (longest.length() > 0) {
            results.get(4).result = RESOURCES.getMessage("dictionary.basedOnWord", longest);
          }
        }
      }
    } else {
      results.get(0).result = RESOURCES.getMessage("length.noPassword");
    }
    return results;
  }

  /*
   * TODO: Need to pull the values from ApplicationResources here based on locales.
   *
    public static String checkPasswordDescribe(String username, String password, boolean strict, boolean superLax) {
    String[] results=checkPassword(username, password, strict, superLax);
    StringBuilder sb = new StringBuilder();
    for (int c=0;c<NUM_CATEGORIES;c++) {
        String desc=results[c];
        if (desc != null) {
          if (sb.length()>0) {
            sb.append('\n');
          }
          sb.append(categories[c]).append(": ").append(desc);
        }
    }
    return sb.length() == 0 ? null : sb.toString();
    }
  */

  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  private static synchronized byte[] getDictionary() throws IOException {
    if (cachedWords == null) {
      try (
          InputStream in = new CorrectedGZIPInputStream(PasswordChecker.class.getResourceAsStream("linux.words.gz"));
          ByteArrayOutputStream bout = new ByteArrayOutputStream()
          ) {
        IoUtils.copy(in, bout);
        cachedWords = bout.toByteArray();
      }
    }
    return cachedWords;
  }

  public static boolean hasResults(List<Result> results) {
    if (results == null) {
      return false;
    }
    String good = RESOURCES.getMessage(GOOD_KEY);
    for (Result result : results) {
      if (!result.result.equals(good)) {
        return true;
      }
    }
    return false;
  }

  public static int indexOfIgnoreCase(String string, byte[] buffer, int wordstart, int wordlen) {
    int endpos = string.length() - wordlen;
    int wordend = wordstart + wordlen;
    Loop:
    for (int c = 0; c <= endpos; c++) {
      int spos = c;
      for (int wpos = wordstart; wpos < wordend; wpos++) {
        int ch1 = string.charAt(spos++);
        int ch2 = buffer[wpos];
        if (ch1 >= 'A' && ch1 <= 'Z') {
          ch1 += 'a' - 'A';
        }
        if (ch2 >= 'A' && ch2 <= 'Z') {
          ch2 += 'a' - 'A';
        }
        if (ch1 != ch2) {
          continue Loop;
        }
      }
      return c;
    }
    return -1;
  }

  public static String yearOf(int year) {
    if (year >= 0 && year <= 9) {
      return "0" + year;
    }
    return String.valueOf(year);
  }

  private static final String EOL = System.lineSeparator();

  /**
   * Prints the results.
   */
  public static void printResults(List<Result> results, Appendable out) throws IOException {
    for (Result result : results) {
      out.append(result.getCategory());
      out.append(": ");
      out.append(result.getResult());
      out.append(EOL);
    }
  }

  /**
   * Prints the results in HTML format.
   */
  @SuppressWarnings("deprecation")
  public static void printResultsHtml(List<Result> results, Appendable out, boolean isXhtml) throws IOException {
    out.append("    <table style=\"border:0px\" cellspacing=\"0\" cellpadding=\"4\">\n"
        + "      <tbody>\n");
    for (Result result : results) {
      out.append("        <tr><td style=\"white-space:nowrap\">");
      com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(result.getCategory(), out, isXhtml);
      out.append(":</td><td style=\"white-space:nowrap\">");
      com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(result.getResult(), out, isXhtml);
      out.append("</td></tr>\n");
    }
    out.append("      </tbody>\n"
        + "    </table>\n");
  }

  /**
   * Gets the results in HTML format.
   */
  public static String getResultsHtml(List<Result> results, boolean isXhtml) throws IOException {
    StringBuilder out = new StringBuilder();
    printResultsHtml(results, out, isXhtml);
    return out.toString();
  }

  /**
   * Gets the results as a String.
   */
  public static String getResultsString(List<Result> results) {
    StringBuilder sb = new StringBuilder();
    for (Result result : results) {
      sb
          .append(result.getCategory())
          .append(": ")
          .append(result.getResult())
          .append('\n');
    }
    return sb.toString();
  }
}
