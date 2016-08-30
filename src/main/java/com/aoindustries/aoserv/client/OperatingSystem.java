/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
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
 * One type of operating system.
 *
 * @see Server
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystem extends GlobalObjectStringKey<OperatingSystem> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	public static final String
		CENTOS="centos",
		DEBIAN="debian",
		GENTOO="gentoo",
		MANDRAKE="mandrake",
		MANDRIVA="mandriva",
		REDHAT="redhat",
		WINDOWS="windows"
	;

	public static final String DEFAULT_OPERATING_SYSTEM=MANDRAKE;

	private String display;
	private boolean is_unix;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return display;
			case 2: return is_unix;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getName() {
		return pkey;
	}

	public String getDisplay() {
		return display;
	}

	public boolean isUnix() {
		return is_unix;
	}

	public OperatingSystemVersion getOperatingSystemVersion(AOServConnector conn, String version, Architecture architecture) throws IOException, SQLException {
		return conn.getOperatingSystemVersions().getOperatingSystemVersion(this, version, architecture);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.OPERATING_SYSTEMS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
		display=result.getString(2);
		is_unix=result.getBoolean(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		display=in.readUTF();
		is_unix=in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return display;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
		out.writeBoolean(is_unix);
	}
}