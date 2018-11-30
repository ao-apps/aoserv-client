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
package com.aoindustries.aoserv.client.pki;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  AO Industries, Inc.
 */
final public class SslCertificateOtherUse extends CachedObjectIntegerKey<SslCertificateOtherUse> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_SSL_CERTIFICATE = 1
	;
	static final String COLUMN_SSL_CERTIFICATE_name = "ssl_certificate";
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	private int sslCertificate;
	private short sortOrder;
	private int count;
	private String display;

	@Override
	public String toStringImpl() {
		return count + " " + display;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SSL_CERTIFICATE: return sslCertificate;
			case 2: return sortOrder;
			case 3: return count;
			case 4: return display;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SSL_CERTIFICATE_OTHER_USES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey           = result.getInt(pos++);
		sslCertificate = result.getInt(pos++);
		sortOrder      = result.getShort(pos++);
		count          = result.getInt(pos++);
		display        = result.getString(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey           = in.readCompressedInt();
		sslCertificate = in.readCompressedInt();
		sortOrder      = in.readShort();
		count          = in.readCompressedInt();
		display        = in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sslCertificate);
		out.writeShort(sortOrder);
		out.writeCompressedInt(count);
		out.writeUTF(display);
	}

	public SslCertificate getSslCertificate() throws SQLException, IOException {
		SslCertificate obj = table.getConnector().getSslCertificates().get(sslCertificate);
		if(obj == null) throw new SQLException("Unable to find SslCertificate: " + sslCertificate);
		return obj;
	}

	public short getSortOrder() {
		return sortOrder;
	}

	public int getCount() {
		return count;
	}

	public String getDisplay() {
		return display;
	}
}
