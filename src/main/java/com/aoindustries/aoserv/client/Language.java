/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  AO Industries, Inc.
 */
final public class Language extends GlobalObjectStringKey<Language> {

	static final int COLUMN_CODE = 0;
	static final String COLUMN_CODE_name = "code";

	public static final String
		EN="en",
		JA="ja"
	;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_CODE: return pkey;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("Language."+pkey+".toString");
	}

	public String getCode() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LANGUAGES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}
}
