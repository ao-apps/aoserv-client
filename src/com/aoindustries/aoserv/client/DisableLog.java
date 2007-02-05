package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLog extends CachedObjectIntegerKey<DisableLog> {

    static final int COLUMN_PKEY=0;

    private long time;
    private String accounting;
    private String disabled_by;
    private String disable_reason;
    
    /**
     * Determines if the current <code>AOServConnector</code> can enable
     * things disabled by this <code>DisableLog</code>.
     */
    public boolean canEnable() {
        BusinessAdministrator disabledBy=getDisabledBy();
        return disabledBy!=null && table
            .connector
            .getThisBusinessAdministrator()
            .getUsername()
            .getPackage()
            .getBusiness()
            .isBusinessOrParent(
                disabledBy
                .getUsername()
                .getPackage()
                .getBusiness()
            )
        ;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==1) return new java.sql.Date(time);
        if(i==2) return accounting;
        if(i==3) return disabled_by;
        if(i==4) return disable_reason;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public Business getBusiness() {
        Business bu=table.connector.businesses.get(accounting);
        if(bu==null) throw new WrappedException(new SQLException("Unable to find Business: "+accounting));
        return bu;
    }

    public long getTime() {
        return time;
    }

    public String getDisabledByUsername() {
        return disabled_by;
    }

    public BusinessAdministrator getDisabledBy() {
        // May be filtered
        return table.connector.businessAdministrators.get(disabled_by);
    }

    public String getDisableReason() {
        return disable_reason;
    }

    protected int getTableIDImpl() {
	return SchemaTable.DISABLE_LOG;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        time=result.getTimestamp(2).getTime();
        accounting=result.getString(3);
        disabled_by=result.getString(4);
        disable_reason=result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        time=in.readLong();
        accounting=in.readUTF();
        disabled_by=in.readUTF();
        disable_reason=readNullUTF(in);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting);
        out.writeUTF(disabled_by);
        writeNullUTF(out, disable_reason);
    }
}