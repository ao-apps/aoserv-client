package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * The possible backup paranoia levels.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupLevel extends GlobalObject<Short,BackupLevel> {

    static final int COLUMN_LEVEL=0;

    /**
     * The different backup levels.
     */
    public static final short
        DO_NOT_BACKUP=0,
        BACKUP_PRIMARY=1,
        BACKUP_PRIMARY_AND_SECONDARY=2
    ;

    public static final short DEFAULT_BACKUP_LEVEL=BACKUP_PRIMARY_AND_SECONDARY;

    short level;
    private String display;

    boolean equalsImpl(Object O) {
	return
            O instanceof BackupLevel
            && ((BackupLevel)O).level==level
	;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_LEVEL) return Short.valueOf(level);
	if(i==1) return display;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public short getLevel() {
	return level;
    }

    public String getDisplay() {
	return display;
    }

    public Short getKey() {
	return level;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BACKUP_LEVELS;
    }

    int hashCodeImpl() {
	return level;
    }

    void initImpl(ResultSet result) throws SQLException {
	level=result.getShort(1);
	display=result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	level=in.readShort();
	display=in.readUTF();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeShort(level);
	out.writeUTF(display);
    }
}