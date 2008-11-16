package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * The possible backup retention values allowed in the system.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetention extends GlobalObject<Short,BackupRetention> {

    static final int COLUMN_DAYS=0;
    static final String COLUMN_DAYS_name = "days";

    // public static final short DEFAULT_BACKUP_RETENTION=7;

    short days;
    private String display;

    boolean equalsImpl(Object O) {
	return
            O instanceof BackupRetention
            && ((BackupRetention)O).days==days
	;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_DAYS) return Short.valueOf(days);
	if(i==1) return display;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public short getDays() {
	return days;
    }

    public String getDisplay() {
	return display;
    }

    public Short getKey() {
	return days;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BACKUP_RETENTIONS;
    }

    int hashCodeImpl() {
	return days;
    }

    public void init(ResultSet result) throws SQLException {
	days=result.getShort(1);
	display=result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	days=in.readShort();
	display=in.readUTF();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeShort(days);
	out.writeUTF(display);
    }
}