/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * <code>InboxAttributes</code> stores all the details of a mail inbox.
 *
 * @author  AO Industries, Inc.
 */
final public class InboxAttributes implements AOServStreamable {

	final private AOServConnector connector;
	final private int linuxServerAccount;
	private long systemTime;
	private long fileSize;
	private long lastModified;

	public InboxAttributes(AOServConnector connector, LinuxServerAccount lsa) {
		this.connector=connector;
		this.linuxServerAccount=lsa.getPkey();
	}

	public InboxAttributes(
		long fileSize,
		long lastModified
	) {
		this.connector=null;
		this.linuxServerAccount=-1;
		this.systemTime=System.currentTimeMillis();
		this.fileSize=fileSize;
		this.lastModified=lastModified;
	}

	public AOServConnector getAOServConnector() {
		return connector;
	}

	public LinuxServerAccount getLinuxServerAccount() throws IOException, SQLException {
		return connector.getLinuxServerAccounts().get(linuxServerAccount);
	}

	public long getSystemTime() {
		return systemTime;
	}

	public long getFileSize() {
		return fileSize;
	}

	/**
	 * Gets the last modified time or <code>0L</code> if unknown.
	 */
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		systemTime=in.readLong();
		fileSize=in.readLong();
		lastModified=in.readLong();
	}

	/**
	 * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
	 * 
	 * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
		write(out, AOServProtocol.Version.getVersion(protocolVersion));
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeLong(systemTime);
		out.writeLong(fileSize);
		out.writeLong(lastModified);
	}
}
