/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>EmailAttachmentType</code> represents one extension that may
 * be blocked by virus filters.
 *
 * @see  EmailAttachmentBlock
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentType extends GlobalObjectStringKey<EmailAttachmentType> {

	static final int COLUMN_EXTENSION=0;
	static final String COLUMN_EXTENSION_name = "extension";

	private String description;
	private boolean is_default_block;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_EXTENSION: return pkey;
			case 1: return description;
			case 2: return is_default_block;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getExtension() {
		return pkey;
	}

	public String getDescription() {
		return description;
	}

	public boolean isDefaultBlock() {
		return is_default_block;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_ATTACHMENT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
		description=result.getString(2);
		is_default_block=result.getBoolean(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
		is_default_block=in.readBoolean();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(is_default_block);
	}
}
