package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * Devices referenced by <code>FileBackup</code>s.
 *
 * @see FileBackup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupDevice extends GlobalObject<Short,FileBackupDevice> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_DEVICE=1
    ;

    short pkey;
    private long device;
    private boolean can_backup;
    private String description;

    boolean equalsImpl(Object O) {
        Profiler.startProfile(Profiler.FAST, FileBackupDevice.class, "equals(Object)", null);
        try {
            return
                O instanceof FileBackupDevice
                && ((FileBackupDevice)O).pkey==pkey
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, FileBackupDevice.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Short.valueOf(pkey);
                case COLUMN_DEVICE: return Long.valueOf(device);
                case 2: return can_backup?Boolean.TRUE:Boolean.FALSE;
                case 3: return description;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean canBackup() {
        return can_backup;
    }

    public String getDescription() {
        return description;
    }

    public long getDevice() {
        return device;
    }

    public short getPKey() {
        return pkey;
    }

    public Short getKey() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.FILE_BACKUP_DEVICES;
    }

    int hashCodeImpl() {
        return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, FileBackupDevice.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getShort(1);
            device=result.getLong(2);
            can_backup=result.getBoolean(3);
            description=result.getString(4);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, FileBackupDevice.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readShort();
            device=in.readLong();
            can_backup=in.readBoolean();
            description=in.readUTF();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, FileBackupDevice.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeShort(pkey);
            out.writeLong(device);
            out.writeBoolean(can_backup);
            out.writeUTF(description);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}