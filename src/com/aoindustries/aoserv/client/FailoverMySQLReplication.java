package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents MySQL replication for one A <code>FailoverFileReplication</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends CachedObjectIntegerKey<FailoverMySQLReplication> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_REPLICATION=1,
        COLUMN_MYSQL_SERVER=2
    ;

    int replication;
    private int mysql_server;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case COLUMN_MYSQL_SERVER: return mysql_server;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public FailoverFileReplication getFailoverFileReplication() {
        FailoverFileReplication ffr=table.connector.failoverFileReplications.get(replication);
        if(ffr==null) throw new WrappedException(new SQLException("Unable to find FailoverFileReplication: "+replication));
        return ffr;
    }

    public MySQLServer getMySQLServer() {
        MySQLServer ms=table.connector.mysqlServers.get(mysql_server);
        if(ms==null) throw new WrappedException(new SQLException("Unable to find MySQLServer: "+mysql_server));
        return ms;
    }

    protected int getTableIDImpl() {
	return SchemaTable.FAILOVER_MYSQL_REPLICATIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        mysql_server=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        mysql_server=in.readCompressedInt();
    }

    String toStringImpl() {
        return getMySQLServer().getName()+", "+getFailoverFileReplication().toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(replication);
        out.writeCompressedInt(mysql_server);
    }
}
