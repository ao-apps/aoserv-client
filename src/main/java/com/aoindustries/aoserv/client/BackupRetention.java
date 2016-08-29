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
 * The possible backup retention values allowed in the system.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetention extends GlobalObject<Short,BackupRetention> {

	static final int COLUMN_DAYS=0;
	static final String COLUMN_DAYS_name = "days";

	// public static final short DEFAULT_BACKUP_RETENTION=7;

	short days;
	private String display;

	@Override
	boolean equalsImpl(Object O) {
		return
			O instanceof BackupRetention
			&& ((BackupRetention)O).days==days
		;
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_DAYS) return days;
		if(i==1) return display;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public short getDays() {
		return days;
	}

	public String getDisplay() {
		return display;
	}

	@Override
	public Short getKey() {
		return days;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BACKUP_RETENTIONS;
	}

	@Override
	int hashCodeImpl() {
		return days;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		days=result.getShort(1);
		display=result.getString(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		days=in.readShort();
		display=in.readUTF();
	}

	@Override
	String toStringImpl() {
		return display;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeShort(days);
		out.writeUTF(display);
	}
}
