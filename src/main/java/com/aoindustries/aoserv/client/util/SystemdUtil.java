/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.util;

import com.aoapps.lang.Strings;
import com.aoindustries.aoserv.client.email.SendmailServer;
import com.aoindustries.aoserv.client.web.HttpdServer;
import java.nio.charset.StandardCharsets;

/**
 * Systemd utilities
 *
 * @author  AO Industries, Inc.
 */
public class SystemdUtil {

	/**
	 * Implements <a href="https://www.freedesktop.org/software/systemd/man/systemd.unit.html">systemd-encoded</a> encoding.
	 * Note: "." is only escaped when in the first position of the string.
	 *
	 * @see  HttpdServer#getSystemdEscapedName()
	 * @see  SendmailServer#getSystemdEscapedName()
	 */
	public static String encode(String value) {
		if(value == null) return null;
		byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
		StringBuilder escaped = new StringBuilder(utf8.length);
		for(int i = 0; i < utf8.length; i++) {
			byte b = utf8[i];
			if(b == '/') {
				// '/' to '-'
				escaped.append('-');
			} else if(
				b == '_'
				|| (b >= 'A' && b <= 'Z')
				|| (b >= 'a' && b <= 'z')
				|| (b >= '0' && b <= '9')
				|| (b == '.' && i > 0) // '.' only escaped at beginning of string
			) {
				// '_' or alphanumeric
				escaped.append((char)b);
			} else {
				if(b == 0) throw new IllegalStateException("Illegal null character in systemd encoding");
				// All others
				escaped.append('\\').append('x').append(Strings.getHexChar(b >>> 4)).append(Strings.getHexChar(b));
			}
		}
		return escaped.toString();
	}

	private SystemdUtil() {}
}
