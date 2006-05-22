package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import com.aoindustries.util.zip.*;
import java.io.*;
import java.util.*;

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

    /**
     * The catergories that are checked.
     */
    private static final String[] categories={
        "Length",
        "Characters",
        "Case",
        "Dates",
        "Dictionary"
    };
    public static final int NUM_CATEGORIES=categories.length;

    private static byte[] cachedWords;

    private PasswordChecker() {}

    public static String[] checkPassword(String username, String password, boolean strict, boolean superLax) {
        try {
            String[] results = new String[5];
            int passwordLen = password.length();
            if (passwordLen > 0) {
                /*
                 * Check the length of the password
                 *
                 * Must be at least eight characters
                 */
                if (passwordLen < (superLax?6:8)) results[0] = superLax ? "The password should be at least six characters." : "The password should be at least eight characters.";

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
                if ((numbercount + specialcount) == passwordLen) results[1] = "The password should not be only numbers";
                else if (!superLax && (lowercount + uppercount + specialcount) == passwordLen) results[1] = "The password should contain numbers and/or punctuation";
                if (password.indexOf(' ')!=-1) {
                    if(results[2]==null) results[2]="The password cannot contain a space character.";
                    else results[2]=results[2]+" and cannot contain a space character.";
                } else if(results[2]!=null) results[2]=results[2]+'.';

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
                ) results[2] = "The password should have both capital and lower case letters.";

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
                    results[4]="The password cannot be the same as the username.";
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
                    if (!goodb) results[3] = "The password must not contain a date.";

                    if(results[4]==null) {
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
                        if (longest.length() > 0) results[4] = "The password is based on a dictionary word: " + longest;
                    }
                }
            } else results[0] = "No password entered.";
            return results;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

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

    public static String getCategory(int index) {
	return categories[index];
    }

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

    public static boolean hasResults(String[] results) {
        if(results==null) return false;
	for(int c=0;c<NUM_CATEGORIES;c++) {
            if(results[c]!=null) return true;
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
}