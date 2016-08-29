/*
 * Copyright 2004-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
	Object getColumnImpl(int i) {
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
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(is_default_block);
	}
}
