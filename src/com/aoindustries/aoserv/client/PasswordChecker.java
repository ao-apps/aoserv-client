package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.EncodingUtils;
import com.aoindustries.util.zip.CorrectedGZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Performs password checking for all password protected
 * services.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PasswordChecker {

    /**
     * The different ways months may be represented in the English
     */
    private static final String[] months={
        "jan","january",
        "feb","february",
        "mar","march",
        "apr","april",
        "may",
        "jun","june",
        "jul","july",
        "aug","august",
        "sep","september",
        "nov","november",
        "dec","december"
    };

    private static final String GOOD_KEY = "PasswordChecker.good";

    /**
     * The catergories that are checked.
     */
    private static final String[] categoryKeys={
        "PasswordChecker.category.length",
        "PasswordChecker.category.characters",
        "PasswordChecker.category.case",
        "PasswordChecker.category.dates",
        "PasswordChecker.category.dictionary"
    };
    public static final int NUM_CATEGORIES=categoryKeys.length;

    private static byte[] cachedWords;

    public static class Result {
        private String category;
        private String result;

        private Result(Locale userLocale, String category) {
            this.category = category;
            this.result = ApplicationResources.getMessage(userLocale, GOOD_KEY);
        }

        public String getCategory() {
            return category;
        }
        
        public String getResult() {
            return result;
        }
    }

    private PasswordChecker() {}

    public static Result[] getAllGoodResults(Locale userLocale) {
        Result[] results = new Result[NUM_CATEGORIES];
        for(int c=0;c<NUM_CATEGORIES;c++) results[c]=new Result(userLocale, ApplicationResources.getMessage(userLocale, categoryKeys[c]));
        return results;
    }

    public static Result[] checkPassword(Locale userLocale, String username, String password, boolean strict, boolean superLax) throws IOException {
        Result[] results = getAllGoodResults(userLocale);
        int passwordLen = password.length();
        if (passwordLen > 0) {
            /*
             * Check the length of the password
             *
             * Must be at least eight characters
             */
            if (passwordLen < (superLax?6:8)) results[0].result = ApplicationResources.getMessage(userLocale, superLax ? "PasswordChecker.length.atLeastSix" : "PasswordChecker.length.atLeastEight");

            /*
             * Gather password stats
             */
            int lowercount = 0;
            int uppercount = 0;
            int numbercount = 0;
            int specialcount = 0;
            int ch;
            for (int c = 0; c < passwordLen; c++) {
                if ((ch = password.charAt(c)) >= 'A' && ch <= 'Z') uppercount++;
                else if (ch >= 'a' && ch <= 'z') lowercount++;
                else if (ch >= '0' && ch <= '9') numbercount++;
                else if (ch <= ' ') specialcount++;
            }

            /*
             * Must use numbers and/or punctuation
             *
             * 1) Must contain numbers/punctuation
             * 2) Must not be all numbers
             * 3) Must not contain a space
             */
            if ((numbercount + specialcount) == passwordLen) results[1].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.characters.notOnlyNumbers");
            else if (!superLax && (lowercount + uppercount + specialcount) == passwordLen) results[1].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.characters.numbersAndPunctuation");
            else if (password.indexOf(' ')!=-1) results[1].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.characters.notContainSpace");

            /*
             * Must use different cases
             *
             * If more than one letter exists, force different case
             */
            if (
                !superLax
                && (
                    (lowercount > 1 && uppercount == 0)
                    || (uppercount > 1 && lowercount == 0)
                    || (lowercount == 0 && uppercount == 0)
                )
            ) results[2].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.case.capitalAndLower");

            /*
             * Generate the backwards version of the password
             */
            String backwards;
            {
                char[] backwards_ca = new char[passwordLen];
                for (int c = 0; c < passwordLen; c++) backwards_ca[c] = password.charAt(passwordLen - c - 1);
                backwards = new String(backwards_ca);
            }

            /*
             * Must not be the same as your username
             */
            if(username!=null && username.equalsIgnoreCase(password)) {
                results[4].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.dictionary.notSameAsUsername");
            }

            /*
             * Must not contain a date
             * <p>
             * Does not contain, forwards or backwards and case insensitive (FBCI), jan ... dec<br>
             * Removed -> Does not contain the two digit representation of the current year, last year, or
             * Removed -> next year, and a month 1-12 (strict mode only)
             */
            if (!superLax) {
                String lowerf = password.toLowerCase();
                String lowerb = backwards.toLowerCase();
                boolean goodb = true;
                int len = months.length;
                for (int c = 0; c < len; c++) {
                    String month = months[c].toLowerCase();
                    if (lowerf.indexOf(month) != -1 || lowerb.indexOf(month) != -1) {
                        goodb = false;
                        break;
                    }
                }
                if (!goodb) results[3].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.dates.noDate");

                if(results[4].result.equals(ApplicationResources.getMessage(userLocale, GOOD_KEY))) {
                    /*
                     * Dictionary check
                     *
                     * Must not contain a dictionary word, forward or backword and case insensitive,
                     * that is longer than half the length of the password if strict or 2 characters if not strict.
                     */
                    byte[] words = getDictionary();

                    int max_allowed_dict_len = strict ? 3 : (passwordLen / 2);

                    // Search through each dictionary word
                    int wordslen = words.length;
                    int pos = 0;
                    String longest = "";
                    boolean longest_forwards = true;
                Loop :
                    while (pos < wordslen) {
                        // Find the beginning of the next word
                        while (pos < wordslen && words[pos] <= ' ') pos++;

                        // Search to the end of the word
                        int startpos = pos;
                        while (pos < wordslen && words[pos] > ' ') pos++;

                        // Get the word
                        int wordlen = pos - startpos;
                        if (wordlen > max_allowed_dict_len) {
                            if (indexOfIgnoreCase(password, words, startpos, wordlen) != -1) {
                                if (longest_forwards ? (wordlen > longest.length()) : (wordlen >= longest.length())) {
                                    longest = new String(words, startpos, wordlen);
                                    longest_forwards = true;
                                }
                            } else if (indexOfIgnoreCase(backwards, words, startpos, wordlen) != -1) {
                                if (wordlen > longest.length()) {
                                    longest = new String(words, startpos, wordlen);
                                    longest_forwards = false;
                                }
                            }
                        }
                    }
                    if (longest.length() > 0) {
                        results[4].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.dictionary.basedOnWord", longest);
                    }
                }
            }
        } else results[0].result = ApplicationResources.getMessage(userLocale, "PasswordChecker.length.noPassword");
        return results;
    }
/**
 * TODO: Need to pull the values from ApplicationResources here based on locales.
 *
    public static String checkPasswordDescribe(String username, String password, boolean strict, boolean superLax) {
	String[] results=checkPassword(username, password, strict, superLax);
	StringBuilder SB=new StringBuilder();
	for(int c=0;c<NUM_CATEGORIES;c++) {
            String desc=results[c];
            if(desc!=null) {
                if(SB.length()>0) SB.append('\n');
                SB.append(categories[c]).append(": ").append(desc);
            }
	}
	return SB.length()==0?null:SB.toString();
    }
*/

    private synchronized static byte[] getDictionary() throws IOException {
	if(cachedWords==null) {
            InputStream in=new CorrectedGZIPInputStream(PasswordChecker.class.getResourceAsStream("linux.words.gz"));
            try {
                ByteArrayOutputStream bout=new ByteArrayOutputStream();
                try {
                    byte[] buff=BufferManager.getBytes();
                    try {
                        int ret;
                        while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) bout.write(buff, 0, ret);
                    } finally {
                        BufferManager.release(buff);
                    }
                } finally {
                    bout.flush();
                    bout.close();
                }
                cachedWords=bout.toByteArray();
            } finally {
                in.close();
            }
	}
	return cachedWords;
    }

    public static boolean hasResults(Locale userLocale, Result[] results) {
        if(results==null) return false;
        String good = ApplicationResources.getMessage(userLocale, GOOD_KEY);
	for(int c=0;c<NUM_CATEGORIES;c++) {
            if(!results[c].result.equals(good)) return true;
	}
	return false;
    }

    public static int indexOfIgnoreCase(String string,byte[] buffer,int wordstart,int wordlen) {
	int endpos=string.length()-wordlen;
	int wordend=wordstart+wordlen;
    Loop:
	for(int c=0;c<=endpos;c++) {
	    int spos=c;
	    for(int wpos=wordstart;wpos<wordend;wpos++) {
		int ch1=string.charAt(spos++);
		int ch2=buffer[wpos];
		if(ch1>='A'&&ch1<='Z') ch1+='a'-'A';
		if(ch2>='A'&&ch2<='Z') ch2+='a'-'A';
		if(ch1!=ch2) continue Loop;
	    }
	    return c;
	}
	return -1;
    }

    public static String yearOf(int year) {
	if(year>=0&&year<=9) return "0"+year;
	return String.valueOf(year);
    }
    
    private static final String EOL = System.getProperty("line.separator");

    /**
     * Prints the results in the provided locale.
     */
    public static void printResults(Result[] results, Appendable out) throws IOException {
        for(int c=0;c<NUM_CATEGORIES;c++) {
            out.append(results[c].getCategory());
            out.append(": ");
            out.append(results[c].getResult());
            out.append(EOL);
        }
    }

    /**
     * Prints the results in the provided locale in HTML format.
     */
    public static void printResultsHtml(Result[] results, Appendable out) throws IOException {
        out.append("    <table style='border:0px;' cellspacing='0' cellpadding='4'>\n");
        for(int c=0;c<NUM_CATEGORIES;c++) {
            out.append("      <tr><td style='white-space:nowrap'>");
            EncodingUtils.encodeHtml(results[c].getCategory(), out);
            out.append(":</td><td style='white-space:nowrap'>");
            EncodingUtils.encodeHtml(results[c].getResult(), out);
            out.append("</td></tr>\n");
        }
        out.append("    </table>\n");
    }

    /**
     * Gets the results in the provided locale in HTML format.
     */
    public static String getResultsHtml(Result[] results) throws IOException {
        StringBuilder out = new StringBuilder();
        printResultsHtml(results, out);
        return out.toString();
    }

    /**
     * Gets the results as a String.
     */
    public static String getResultsString(Result[] results) {
        StringBuilder SB = new StringBuilder();
        for(int c=0;c<NUM_CATEGORIES;c++) {
            SB
                .append(results[c].getCategory())
                .append(": ")
                .append(results[c].getResult())
                .append('\n');
        }
        return SB.toString();
    }
}
