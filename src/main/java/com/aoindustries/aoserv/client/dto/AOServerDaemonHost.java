/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2010-2013, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class AOServerDaemonHost extends AOServObject {

	private int pkey;
	private int aoServer;
	private InetAddress host;

	public AOServerDaemonHost() {
	}

	public AOServerDaemonHost(
		int pkey,
		int aoServer,
		InetAddress host
	) {
		this.pkey = pkey;
		this.aoServer = aoServer;
		this.host = host;
	}

	public int getPkey() {
		return pkey;
	}

	public void setPkey(int pkey) {
		this.pkey = pkey;
	}

	public int getAoServer() {
		return aoServer;
	}

	public void setAoServer(int aoServer) {
		this.aoServer = aoServer;
	}

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}
}
