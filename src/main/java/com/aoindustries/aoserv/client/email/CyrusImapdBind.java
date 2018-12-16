/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>CyrusImapdServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  A <code>CyrusImapdBind</code> ties
 * <code>CyrusImapdServer</code>s to <code>NetBinds</code>.
 *
 * @see  CyrusImapdServer
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
final public class CyrusImapdBind extends CachedObjectIntegerKey<CyrusImapdBind> {

	static final int
		COLUMN_NET_BIND = 0,
		COLUMN_CYRUS_IMAPD_SERVER = 1,
		COLUMN_SSL_CERTIFICATE = 3
	;
	static final String COLUMN_NET_BIND_name = "net_bind";

	private int cyrus_imapd_server;
	private DomainName servername;
	private int certificate;
	private Boolean allowPlaintextAuth;

	@Override
	public String toStringImpl() throws SQLException, IOException {
		CyrusImapdServer server = getCyrusImapdServer();
		Bind bind = getNetBind();
		return server.toStringImpl() + '|' + bind.toStringImpl();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NET_BIND: return pkey;
			case COLUMN_CYRUS_IMAPD_SERVER: return cyrus_imapd_server;
			case 2: return servername;
			case COLUMN_SSL_CERTIFICATE: return certificate == -1 ? null : certificate;
			case 4: return allowPlaintextAuth;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.CYRUS_IMAPD_BINDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			cyrus_imapd_server = result.getInt(pos++);
			servername = DomainName.valueOf(result.getString(pos++));
			certificate = result.getInt(pos++);
			if(result.wasNull()) certificate = -1;
			allowPlaintextAuth = result.getBoolean(pos++);
			if(result.wasNull()) allowPlaintextAuth = null;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			cyrus_imapd_server = in.readCompressedInt();
			servername = DomainName.valueOf(in.readNullUTF());
			certificate = in.readCompressedInt();
			allowPlaintextAuth = in.readNullBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(cyrus_imapd_server);
		out.writeNullUTF(ObjectUtils.toString(servername));
		out.writeCompressedInt(certificate);
		out.writeNullBoolean(allowPlaintextAuth);
	}

	public Bind getNetBind() throws SQLException, IOException {
		Bind obj = table.getConnector().getNet().getNetBinds().get(pkey);
		if(obj == null) throw new SQLException("Unable to find NetBind: " + pkey);
		return obj;
	}

	public CyrusImapdServer getCyrusImapdServer() throws SQLException, IOException {
		CyrusImapdServer obj = table.getConnector().getEmail().getCyrusImapdServers().get(cyrus_imapd_server);
		if(obj == null) throw new SQLException("Unable to find CyrusImapd: " + cyrus_imapd_server);
		return obj;
	}

	/**
	 * The fully qualified hostname for <code>servername</code>.
	 *
	 * When {@code null}, defaults to {@link CyrusImapdServer#getServername()}.
	 */
	public DomainName getServername() {
		return servername;
	}

	/**
	 * Gets the SSL certificate for this server.
	 *
	 * @return  the SSL certificate or {@code null} when filtered or defaulting to {@link CyrusImapdServer#getCertificate()}
	 */
	public Certificate getCertificate() throws SQLException, IOException {
		if(certificate == -1) return null;
		// May be filtered
		return table.getConnector().getPki().getSslCertificates().get(certificate);
	}

	/**
	 * Allows plaintext authentication (PLAIN/LOGIN) on non-TLS links.
	 *
	 * When {@code null}, defaults to {@link CyrusImapdServer#getAllowPlaintextAuth()}.
	 */
	public Boolean getAllowPlaintextAuth() {
		return allowPlaintextAuth;
	}
}
