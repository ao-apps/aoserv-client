/*
 * Copyright 2013, 2016 by AO Industries, Inc.,
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
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class DistroReportType extends GlobalObjectStringKey<DistroReportType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	private String display;

	/**
	 * The different report types.
	 */
	public static final String
		BIG_DIRECTORY  = "BD",
		EXTRA          = "EX",
		GROUP_MISMATCH = "GR",
		HIDDEN         = "HI",
		LENGTH         = "LN",
		MD5            = "M5",
		MISSING        = "MI",
		OWNER_MISMATCH = "OW",
		NO_OWNER       = "NO",
		NO_GROUP       = "NG",
		PERMISSIONS    = "PR",
		SETUID         = "SU",
		SYMLINK        = "SY",
		TYPE           = "TY"
	;

	@Override
	String toStringImpl() {
		return display;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return display;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISTRO_REPORT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		display = result.getString(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		display=in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
	}
}
