/*
 * Copyright 2005-2009, 2016 by AO Industries, Inc.,
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
 * A <code>PackageCategory</code> represents one type of service
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class PackageCategory extends GlobalObjectStringKey<PackageCategory> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		AOSERV="aoserv",
		APPLICATION="application",
		BACKUP="backup",
		COLOCATION="colocation",
		DEDICATED="dedicated",
		MANAGED="managed",
		RESELLER="reseller",
		SYSADMIN="sysadmin",
		VIRTUAL="virtual",
		VIRTUAL_DEDICATED="virtual_dedicated",
		VIRTUAL_MANAGED="virtual_managed"
	;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGE_CATEGORIES;
	}

	@Override
	public void init(ResultSet results) throws SQLException {
		pkey = results.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("PackageCategory."+pkey+".toString");
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_60)<=0) out.writeUTF(toString()); // display
	}
}