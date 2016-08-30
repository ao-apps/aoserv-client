/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.AOPool;
import com.aoindustries.util.PropertiesUtils;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * The default client configuration is stored in a properties resource named
 * <code>/com/aoindustries/aoserv/client/aoesrv-client.properties</code>.
 *
 * @see  AOServConnector#getConnector()
 *
 * @author  AO Industries, Inc.
 */
final public class AOServClientConfiguration {

	private static final Object propsLock = new Object();
	private static Properties props;

	private static String getProperty(String name) throws IOException {
		synchronized (propsLock) {
			if (props == null) props = PropertiesUtils.loadFromResource(AOServClientConfiguration.class, "aoserv-client.properties");
			return props.getProperty(name);
		}
	}

	/**
	 * Gets the list of protocols in preferred order.
	 */
	static List<String> getProtocols() throws IOException {
		return StringUtility.splitStringCommaSpace(getProperty("aoserv.client.protocols"));
	}

	/**
	 * Gets the non-SSL hostname.
	 */
	static String getTcpHostname() throws IOException {
		return getProperty("aoserv.client.tcp.hostname");
	}

	/**
	 * Gets the non-SSL local IP to connect from or <code>null</code> if not configured.
	 */
	static String getTcpLocalIp() throws IOException {
		String S = getProperty("aoserv.client.tcp.local_ip");
		if(
			S==null
			|| (S=S.trim()).length()==0
		) return null;
		return S;
	}

	/**
	 * Gets the non-SSL port.
	 */
	static int getTcpPort() throws IOException {
		return Integer.parseInt(getProperty("aoserv.client.tcp.port"));
	}

	/**
	 * Gets the non-SSL pool size.
	 */
	static int getTcpConnectionPoolSize() throws IOException {
		return Integer.parseInt(getProperty("aoserv.client.tcp.connection.pool.size"));
	}

	/**
	 * Gets the non-SSL connection max age in milliseconds.
	 */
	static long getTcpConnectionMaxAge() throws IOException {
		String S = getProperty("aoserv.client.tcp.connection.max_age");
		return S==null || S.length()==0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(S);
	}

	/**
	 * Gets the SSL hostname to connect to.
	 */
	static String getSslHostname() throws IOException {
		return getProperty("aoserv.client.ssl.hostname");
	}

	/**
	 * Gets the SSL local IP to connect from or <code>null</code> if not configured.
	 */
	static String getSslLocalIp() throws IOException {
		String S = getProperty("aoserv.client.ssl.local_ip");
		if(
			S==null
			|| (S=S.trim()).length()==0
		) return null;
		return S;
	}

	/**
	 * Gets the SSL port to connect to.
	 */
	static int getSslPort() throws IOException {
		return Integer.parseInt(getProperty("aoserv.client.ssl.port"));
	}

	/**
	 * Gets the SSL connection pool size.
	 */
	static int getSslConnectionPoolSize() throws IOException {
		return Integer.parseInt(getProperty("aoserv.client.ssl.connection.pool.size"));
	}

	/**
	 * Gets the SSL connection max age in milliseconds.
	 */
	static long getSslConnectionMaxAge() throws IOException {
		String S = getProperty("aoserv.client.ssl.connection.max_age");
		return S==null || S.length()==0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(S);
	}

	/**
	 * Gets the optional SSL truststore path.
	 *
	 * For use by aoserv-daemon and aoserv-backup only.
	 */
	public static String getSslTruststorePath() throws IOException {
		return getProperty("aoserv.client.ssl.truststore.path");
	}

	/**
	 * Gets the optional SSL truststore password.
	 *
	 * For use by aoserv-daemon and aoserv-backup only.
	 */
	public static String getSslTruststorePassword() throws IOException {
		return getProperty("aoserv.client.ssl.truststore.password");
	}

	/**
	 * Gets the optional default username.
	 */
	public static String getUsername() throws IOException {
		return getProperty("aoserv.client.username");
	}

	/**
	 * Gets the optional default password.
	 */
	public static String getPassword() throws IOException {
		return getProperty("aoserv.client.password");
	}

	/**
	 * Gets the hostname of this daemon for daemon-specific locking.  Leave
	 * this blank for non-AOServDaemon connections.
	 */
	static String getDaemonServer() throws IOException {
		return getProperty("aoserv.client.daemon.server");
	}

	/**
	 * Make no instances.
	 */
	private AOServClientConfiguration() {}
}
