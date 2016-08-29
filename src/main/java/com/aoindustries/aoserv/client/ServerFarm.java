/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
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
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends CachedObjectStringKey<ServerFarm> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	private String description;
	private int owner;
	private boolean use_restricted_smtp_port;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case 1: return description;
			case 2: return owner;
			case 3: return use_restricted_smtp_port;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Package getOwner() throws IOException, SQLException {
		// May be filtered
		return table.connector.getPackages().get(owner);
	}

	public boolean useRestrictedSmtpPort() {
		return use_restricted_smtp_port;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SERVER_FARMS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
		owner = result.getInt(3);
		use_restricted_smtp_port = result.getBoolean(4);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
		owner=in.readCompressedInt();
		use_restricted_smtp_port = in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return description;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeUTF("192.168.0.0/16");
			out.writeBoolean(false);
			out.writeUTF("mob");
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_102)>=0) out.writeCompressedInt(owner);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_26)>=0) out.writeBoolean(use_restricted_smtp_port);
	}
}
