/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2011, 2016, 2017, 2018  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.password;

import com.aoindustries.security.Identifier;
import com.aoindustries.security.SmallIdentifier;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Generates easily remembered random passwords of at least 38 bits of entropy.
 *
 * @see  SmallIdentifier  Stronger, but less memorable, passwords may be generated from SmallIdentifiers.
 *                        They are eleven characters long with an unambiguous character set.
 *
 * @see  Identifier  And even stronger passwords may be generated from Identifiers.
 *                   These are twenty-two characters long with an unambiguous character set.
 *
 * @author  AO Industries, Inc.
 */
public class PasswordGenerator {

	private PasswordGenerator() {
	}

	/**
	 * The minimum randomness of the generated password.
	 * This is currently around 38.6 bits of entropy.
	 */
	private static final long MINIMUM_ENTROPY = 413000000000L;

	private static final String[] CONS = {
		"b",            "bl",   "br",
		"c",    "cl",   "cr",
		"ch",
		"d",                    "dr",   "dw",
		"f",            "fl",   "fr",
		"g",            "gl",   "gr",   "gw",
		"h",
		"j",
		"k",            "kl",   "kr",
		"l",
		"m",
		"n",
		"p",    "ph",   "pl",   "pr",
		"qu",
		"r",
		"s",    "sc",   "scr",  "sk",   "sl",   "sm",   "sn",   "sp",   "spl",  "spr",  "st",  "str",  "sw",
		"sh",   
		"t",    "tr",   "tw",
		"th",   "thr",
		"v",
		"w",    "wh",
		"y",
		"z",
	};

	private static final String[] TERM_CONS = {
		"b",
		"ch",   "tch",  "ck",
		"d",
		"f",    "ff",
		"g",
		"k",
		"l",	"lch",  "ld",	"lf",	"lk",	"lm",	"lp",   "lsh",  "lt",	"lth",  "lve",    "ll",
		"m",	"mp",
		"n",	"nd",	"ng",	"nk",	"nt",
		"p",																
		"r",	"rch", "rd",	"rf",	"rg",	"rk",	"rm",	"rn",	"rp",	"rsh",  "rt",	"rth",  "rve", 
	"sk",	"sp",	"ss",	"st",
		"sh",
		"t",	"tt",
		"th",   
		"ve",																
		"x",
		"z",    "zz",
	};

	private static final String[] VOWS = {
		"a",   
		"e",   
		"i",  
		"o",  
		"u"
	};

	private static final String[] TERM_VOWS = {
		"a",    "ay",   "ya",   "ah",   "ar",   "al",
		"ey",   "ee",   "er",   "el",
		"i",    "io",   "yo",
		"o",    "oi",   "oy",   "oh",   "or",   "ol",
		"uh",   "ul",
		"y"
	};

	private static final SecureRandom secureRandom = new SecureRandom();

	public static String generatePassword() throws IOException {
		return generatePassword(secureRandom);
	}

	public static String generatePassword(Random r) throws IOException {
		StringBuilder pw = new StringBuilder();
		String password;
		do {
			long entropy;
			do {
				pw.setLength(0);
				entropy = 1;

				int temp1 = 0;
				int temp2 = 0;

				// determine which template to use
				int template = r.nextInt(3);
				entropy*=3;
				switch (template) {
					case 0: {
						temp1 = r.nextBoolean()?321:412;
						temp2 = r.nextBoolean()?321:412;
						entropy*=4;
						break;
					}
					case 1: {
						if (r.nextBoolean()) {
							temp1 = r.nextBoolean()?361:412;
							temp2 = r.nextBoolean()?4161:3612;
						} else {
							temp2 = r.nextBoolean()?361:412;
							temp1 = r.nextBoolean()?4161:3612; 
						}
						entropy*=8;
						break;
					}
					case 2: {
						temp1 = r.nextBoolean()?416161:361612;
						entropy*=2;
						break;
					}
				}
				// parse the word templates
				StringBuilder word1 = new StringBuilder();
				StringBuilder word2 = new StringBuilder();
				for (int i = 0; i<2; i++) {

					StringBuilder currWord = (i==0)?word1:word2;
					int currTemp = (i==0)?temp1:temp2;
					int digit = currTemp % 10;

					while (digit>0) {
						currTemp /= 10;
						switch (digit) {
							case 1: {
								currWord.append(VOWS[r.nextInt(VOWS.length)]);
								entropy*=VOWS.length;
								break;
							}
							case 2: {
								currWord.append(CONS[r.nextInt(CONS.length)]);
								entropy*=CONS.length;
								break;
							}
							case 3: {
								currWord.append(TERM_VOWS[r.nextInt(TERM_VOWS.length)]);
								entropy*=TERM_VOWS.length;
								break;
							}
							case 4: {
								currWord.append(TERM_CONS[r.nextInt(TERM_CONS.length)]);
								entropy*=TERM_CONS.length;
								break;
							}
							case 6: {
								boolean a = r.nextBoolean();
								currWord.append(a?CONS[r.nextInt(CONS.length)]:TERM_CONS[r.nextInt(TERM_CONS.length)]);
								entropy*=(a?CONS:TERM_CONS).length;
								break;
							}
						}
						digit = currTemp % 10;
					}
					// post-processing checks
					if (currWord.length()>0) {
						String ppWord = currWord.toString();
						ppWord = StringUtility.replace(ppWord, "uu", "ui");
						ppWord = StringUtility.replace(ppWord, "iw", "u");
						ppWord = StringUtility.replace(ppWord, "yy", "y");
						ppWord = StringUtility.replace(ppWord, "lal", r.nextBoolean()?"ral":"lar");
						ppWord = StringUtility.replace(ppWord, "rar", "ral");
						ppWord = StringUtility.replace(ppWord, "lel", r.nextBoolean()?"rel":"ler");
						ppWord = StringUtility.replace(ppWord, "rer", "rel");
						ppWord = StringUtility.replace(ppWord, "lol", r.nextBoolean()?"rol":"lor");
						ppWord = StringUtility.replace(ppWord, "ror", "rol");
						ppWord = StringUtility.replace(ppWord, "lul", r.nextBoolean()?"rul":"lur");
						ppWord = StringUtility.replace(ppWord, "rur", "rul");
						ppWord = StringUtility.replace(ppWord, "lil", r.nextBoolean()?"ril":"lir");
						ppWord = StringUtility.replace(ppWord, "rir", "ril");
						ppWord = StringUtility.replace(ppWord, "lyl", r.nextBoolean()?"ryl":"lyr");
						ppWord = StringUtility.replace(ppWord, "ryr", "ryl");
						if (ppWord.indexOf("rve")<ppWord.length()-3) ppWord = StringUtility.replace(ppWord, "rve", "rv");
						if (ppWord.indexOf("lve")<ppWord.length()-3) ppWord = StringUtility.replace(ppWord, "lve", "lv");

						currWord.setLength(0);
						currWord.append(ppWord);
					}
				}

				int dig1 = r.nextInt(8)+2;
				int dig2 = r.nextInt(8)+2;
				entropy*=64;
				int dig1pos = r.nextInt(3);
				int dig2pos = r.nextInt(3);
				entropy*=6;
				if (dig1pos==0) pw.append(dig1);
				if (dig2pos==0) pw.append(dig2);
				appendCapped(pw, word1);
				if (dig1pos==1) pw.append(dig1);
				if (dig2pos==1) pw.append(dig2);
				appendCapped(pw, word2);
				if (dig1pos==2) pw.append(dig1);
				if (dig2pos==2) pw.append(dig2);
				//pw.append(" - ").append(entropy/1000000000L);

			} while(entropy < MINIMUM_ENTROPY);
			password=pw.toString();
		} while(PasswordChecker.hasResults(PasswordChecker.checkPassword(null, password, PasswordChecker.PasswordStrength.STRICT)));
		return password;
	}

	private static void appendCapped(StringBuilder to, StringBuilder from) {
		int len=from.length();
		if(len>0) {
			int ch=from.charAt(0);
			if(ch>='a' && ch<='z') ch -= ('a'-'A');
			to.append((char)ch);
			for(int c=1;c<len;c++) to.append(from.charAt(c));
		}
	}
}
