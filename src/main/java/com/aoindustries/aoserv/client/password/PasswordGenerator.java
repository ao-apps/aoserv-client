/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2011, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.Strings;
import com.aoapps.security.Identifier;
import com.aoapps.security.SmallIdentifier;
import com.aoindustries.aoserv.client.AOServConnector;
import java.io.IOException;
import java.security.SecureRandom;

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
public final class PasswordGenerator {

	/** Make no instances. */
	private PasswordGenerator() {throw new AssertionError();}

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

	/**
	 * Generates a password using a default {@link SecureRandom} instance, which is not a
	 * {@linkplain SecureRandom#getInstanceStrong() strong instance} to avoid blocking.
	 */
	public static String generatePassword() throws IOException {
		return generatePassword(AOServConnector.getSecureRandom());
	}

	/**
	 * Generates a password using the provided {@link SecureRandom} source.
	 */
	public static String generatePassword(SecureRandom secureRandom) throws IOException {
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
				int template = secureRandom.nextInt(3);
				entropy*=3;
				switch (template) {
					case 0: {
						temp1 = secureRandom.nextBoolean()?321:412;
						temp2 = secureRandom.nextBoolean()?321:412;
						entropy*=4;
						break;
					}
					case 1: {
						if (secureRandom.nextBoolean()) {
							temp1 = secureRandom.nextBoolean()?361:412;
							temp2 = secureRandom.nextBoolean()?4161:3612;
						} else {
							temp2 = secureRandom.nextBoolean()?361:412;
							temp1 = secureRandom.nextBoolean()?4161:3612;
						}
						entropy*=8;
						break;
					}
					case 2: {
						temp1 = secureRandom.nextBoolean()?416161:361612;
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
								currWord.append(VOWS[secureRandom.nextInt(VOWS.length)]);
								entropy*=VOWS.length;
								break;
							}
							case 2: {
								currWord.append(CONS[secureRandom.nextInt(CONS.length)]);
								entropy*=CONS.length;
								break;
							}
							case 3: {
								currWord.append(TERM_VOWS[secureRandom.nextInt(TERM_VOWS.length)]);
								entropy*=TERM_VOWS.length;
								break;
							}
							case 4: {
								currWord.append(TERM_CONS[secureRandom.nextInt(TERM_CONS.length)]);
								entropy*=TERM_CONS.length;
								break;
							}
							case 6: {
								boolean a = secureRandom.nextBoolean();
								currWord.append(a?CONS[secureRandom.nextInt(CONS.length)]:TERM_CONS[secureRandom.nextInt(TERM_CONS.length)]);
								entropy*=(a?CONS:TERM_CONS).length;
								break;
							}
						}
						digit = currTemp % 10;
					}
					// post-processing checks
					if (currWord.length()>0) {
						String ppWord = currWord.toString();
						ppWord = Strings.replace(ppWord, "uu", "ui");
						ppWord = Strings.replace(ppWord, "iw", "u");
						ppWord = Strings.replace(ppWord, "yy", "y");
						ppWord = Strings.replace(ppWord, "lal", secureRandom.nextBoolean()?"ral":"lar");
						ppWord = Strings.replace(ppWord, "rar", "ral");
						ppWord = Strings.replace(ppWord, "lel", secureRandom.nextBoolean()?"rel":"ler");
						ppWord = Strings.replace(ppWord, "rer", "rel");
						ppWord = Strings.replace(ppWord, "lol", secureRandom.nextBoolean()?"rol":"lor");
						ppWord = Strings.replace(ppWord, "ror", "rol");
						ppWord = Strings.replace(ppWord, "lul", secureRandom.nextBoolean()?"rul":"lur");
						ppWord = Strings.replace(ppWord, "rur", "rul");
						ppWord = Strings.replace(ppWord, "lil", secureRandom.nextBoolean()?"ril":"lir");
						ppWord = Strings.replace(ppWord, "rir", "ril");
						ppWord = Strings.replace(ppWord, "lyl", secureRandom.nextBoolean()?"ryl":"lyr");
						ppWord = Strings.replace(ppWord, "ryr", "ryl");
						if (ppWord.indexOf("rve")<ppWord.length()-3) ppWord = Strings.replace(ppWord, "rve", "rv");
						if (ppWord.indexOf("lve")<ppWord.length()-3) ppWord = Strings.replace(ppWord, "lve", "lv");

						currWord.setLength(0);
						currWord.append(ppWord);
					}
				}

				int dig1 = secureRandom.nextInt(8)+2;
				int dig2 = secureRandom.nextInt(8)+2;
				entropy*=64;
				int dig1pos = secureRandom.nextInt(3);
				int dig2pos = secureRandom.nextInt(3);
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
			for(int c=1;c<len;c++) {
				to.append(from.charAt(c));
			}
		}
	}
}
