package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * <code>BackupPartition</code> stores backup data.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupPartition extends CachedObjectIntegerKey<BackupPartition> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1
    ;

    int ao_server;
    String device;
    String path;
    private long minimum_free_space;
    private long desired_free_space;
    private boolean enabled;
    private int fill_order;

    public IntsAndLongs getBackupDataPKeys(boolean hasDataOnly) {
        try {
            IntList pkeys=new SortedIntArrayList();
            LongArrayList sizes=new LongArrayList();
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.GET_BACKUP_DATAS_FOR_BACKUP_PARTITION.ordinal());
                out.writeCompressedInt(pkey);
                out.writeBoolean(hasDataOnly);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    int lastpkey=0;
                    int pkey;
                    while((pkey=in.readCompressedInt())!=-1) {
                        lastpkey+=pkey;
                        // This works because the rows are sorted by the database
                        pkeys.add(lastpkey);
                        sizes.add(in.readLong());
                    }
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            return new IntsAndLongs(pkeys, sizes);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return device;
            case 3: return path;
            case 4: return Long.valueOf(minimum_free_space);
            case 5: return Long.valueOf(desired_free_space);
            case 6: return enabled?Boolean.TRUE:Boolean.FALSE;
            case 7: return Integer.valueOf(fill_order);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getDiskTotalSize() {
        return table.connector.requestLongQuery(AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_TOTAL_SIZE, pkey);
    }

    public long getDiskUsedSize() {
        return table.connector.requestLongQuery(AOServProtocol.CommandID.GET_BACKUP_PARTITION_DISK_USED_SIZE, pkey);
    }

    public AOServer getAOServer() {
        AOServer ao=table.connector.aoServers.get(ao_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return ao;
    }

    public String getDevice() {
        return device;
    }
    
    public String getPath() {
        return path;
    }
    
    public long getMinimumFreeSpace() {
        return minimum_free_space;
    }
    
    public long getDesiredFreeSpace() {
        return desired_free_space;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BACKUP_PARTITIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        ao_server=result.getInt(2);
        device=result.getString(3);
        path=result.getString(4);
        minimum_free_space=result.getLong(5);
        desired_free_space=result.getLong(6);
        enabled=result.getBoolean(7);
        fill_order=result.getInt(8);
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Backup partitions with the lowest fill order are used first.
     */
    public int getFillOrder() {
        return fill_order;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ao_server=in.readCompressedInt();
        device=in.readUTF().intern();
        path=in.readUTF().intern();
        minimum_free_space=in.readLong();
        desired_free_space=in.readLong();
        enabled=in.readBoolean();
        fill_order=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
        out.writeUTF(device);
        out.writeUTF(path);
        out.writeLong(minimum_free_space);
        out.writeLong(desired_free_space);
        out.writeBoolean(enabled);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_117)>=0) out.writeCompressedInt(fill_order);
    }
}