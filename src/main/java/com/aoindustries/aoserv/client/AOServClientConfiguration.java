/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoapps.hodgepodge.io.AOPool;
import com.aoapps.lang.Strings;
import com.aoapps.lang.exception.ConfigurationException;
import com.aoapps.lang.util.PropertiesUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.account.User;
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
public abstract class AOServClientConfiguration {

	/** Make no instances. */
	private AOServClientConfiguration() {throw new AssertionError();}

	private static class PropsLock {/* Empty lock class to help heap profile */}
	private static final PropsLock propsLock = new PropsLock();
	private static Properties props;

	private static String getProperty(String name) throws ConfigurationException {
		try {
			synchronized (propsLock) {
				if (props == null) {
					props = PropertiesUtils.loadFromResource(
						AOServClientConfiguration.class,
						"/com/aoindustries/aoserv/client/aoserv-client.properties"
					);
				}
				return props.getProperty(name);
			}
		} catch(IOException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the list of protocols in preferred order.
	 */
	static List<String> getProtocols() throws ConfigurationException {
		return Strings.splitCommaSpace(getProperty("aoserv.client.protocols"));
	}

	/**
	 * Gets the non-SSL hostname.
	 */
	static HostAddress getTcpHostname() throws ConfigurationException {
		try {
			return HostAddress.valueOf(getProperty("aoserv.client.tcp.hostname"));
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the non-SSL local IP to connect from or {@code null} if not configured.
	 */
	static InetAddress getTcpLocalIp() throws ConfigurationException {
		String s = getProperty("aoserv.client.tcp.local_ip");
		if(
			s == null
			|| (s = s.trim()).length() == 0
		) return null;
		try {
			return InetAddress.valueOf(s);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the non-SSL port.
	 */
	static Port getTcpPort() throws ConfigurationException {
		try {
			return Port.valueOf(
				Integer.parseInt(getProperty("aoserv.client.tcp.port")),
				com.aoapps.net.Protocol.TCP
			);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the non-SSL pool size.
	 */
	static int getTcpConnectionPoolSize() throws ConfigurationException {
		return Integer.parseInt(getProperty("aoserv.client.tcp.connection.pool.size"));
	}

	/**
	 * Gets the non-SSL connection max age in milliseconds.
	 */
	static long getTcpConnectionMaxAge() throws ConfigurationException {
		String s = getProperty("aoserv.client.tcp.connection.max_age");
		return s == null || s.length() == 0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(s);
	}

	/**
	 * Gets the SSL hostname to connect to.
	 */
	static HostAddress getSslHostname() throws ConfigurationException {
		try {
			return HostAddress.valueOf(getProperty("aoserv.client.ssl.hostname"));
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the SSL local IP to connect from or {@code null} if not configured.
	 */
	static InetAddress getSslLocalIp() throws ConfigurationException {
		String s = getProperty("aoserv.client.ssl.local_ip");
		if(
			s == null
			|| (s = s.trim()).length() == 0
		) return null;
		try {
			return InetAddress.valueOf(s);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the SSL port to connect to.
	 */
	static Port getSslPort() throws ConfigurationException {
		try {
			return Port.valueOf(
				Integer.parseInt(getProperty("aoserv.client.ssl.port")),
				com.aoapps.net.Protocol.TCP
			);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the SSL connection pool size.
	 */
	static int getSslConnectionPoolSize() throws ConfigurationException {
		return Integer.parseInt(getProperty("aoserv.client.ssl.connection.pool.size"));
	}

	/**
	 * Gets the SSL connection max age in milliseconds.
	 */
	static long getSslConnectionMaxAge() throws ConfigurationException {
		String s = getProperty("aoserv.client.ssl.connection.max_age");
		return s == null || s.length() == 0 ? AOPool.DEFAULT_MAX_CONNECTION_AGE : Long.parseLong(s);
	}

	/**
	 * Gets the optional SSL truststore path.
	 *
	 * For use by aoserv-daemon and aoserv-backup only.
	 */
	public static String getSslTruststorePath() throws ConfigurationException {
		return getProperty("aoserv.client.ssl.truststore.path");
	}

	/**
	 * Gets the optional SSL truststore password.
	 *
	 * For use by aoserv-daemon and aoserv-backup only.
	 */
	public static String getSslTruststorePassword() throws ConfigurationException {
		return getProperty("aoserv.client.ssl.truststore.password");
	}

	/**
	 * Gets the optional default username.
	 */
	public static User.Name getUsername() throws ConfigurationException {
		String username = getProperty("aoserv.client.username");
		if(
			username == null
			|| (username = username.trim()).isEmpty()
		) return null;
		try {
			return User.Name.valueOf(username);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Gets the optional default password.
	 */
	public static String getPassword() throws ConfigurationException {
		return getProperty("aoserv.client.password");
	}

	/**
	 * Gets the hostname of this daemon for daemon-specific locking.  Leave
	 * this blank for non-AOServDaemon connections.
	 */
	static DomainName getDaemonServer() throws ConfigurationException {
		String domainServer = getProperty("aoserv.client.daemon.server");
		if(
			domainServer == null
			|| (domainServer = domainServer.trim()).isEmpty()
		) return null;
		try {
			return DomainName.valueOf(domainServer);
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}
}
