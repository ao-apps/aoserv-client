/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017  AO Industries, Inc.
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
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>EmailSpamAssassinIntegrationMode</code> is a simple wrapper for the types
 * of SpamAssassin integration modes.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSpamAssassinIntegrationMode extends GlobalObjectStringKey<EmailSpamAssassinIntegrationMode> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	public static final String
		NONE="none",
		POP3="pop3",
		IMAP="imap"
	;

	public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE = NONE;

	private String display;
	private int sort_order;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return display;
			case 2: return sort_order;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getName() {
		return pkey;
	}

	public String getDisplay() {
		return display;
	}

	public int getSortOrder() {
		return sort_order;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
	}

	@Override
	public void init(ResultSet results) throws SQLException {
		pkey=results.getString(1);
		display=results.getString(2);
		sort_order=results.getInt(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		display=in.readUTF();
		sort_order=in.readCompressedInt();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
		out.writeCompressedInt(sort_order);
	}
}
