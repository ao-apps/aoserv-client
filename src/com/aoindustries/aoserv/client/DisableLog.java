package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLog extends CachedObjectIntegerKey<DisableLog> {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_PKEY_name = "pkey";

    private long time;
    private String accounting;
    private String disabled_by;
    private String disable_reason;
    
    /**
     * Determines if the current <code>AOServConnector</code> can enable
     * things disabled by this <code>DisableLog</code>.
     */
    public boolean canEnable() throws SQLException, IOException {
        BusinessAdministrator disabledBy=getDisabledBy();
        return disabledBy!=null && table
            .connector
            .getThisBusinessAdministrator()
            .getUsername()
            .getBusiness()
            .isBusinessOrParentOf(
                disabledBy
                .getUsername()
                .getBusiness()
            )
        ;
    }

    Object getColumnImpl(int i) {
        if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
        if(i==1) return new java.sql.Date(time);
        if(i==2) return accounting;
        if(i==3) return disabled_by;
        if(i==4) return disable_reason;
    	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public Business getBusiness() throws SQLException, IOException {
        Business bu=table.connector.getBusinesses().get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }

    public long getTime() {
        return time;
    }

    public String getDisabledByUsername() {
        return disabled_by;
    }

    public BusinessAdministrator getDisabledBy() throws IOException, SQLException {
        // May be filtered
        return table.connector.getBusinessAdministrators().get(disabled_by);
    }

    public String getDisableReason() {
        return disable_reason;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISABLE_LOG;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        time=result.getTimestamp(2).getTime();
        accounting=result.getString(3);
        disabled_by=result.getString(4);
        disable_reason=result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        time=in.readLong();
        accounting=in.readUTF().intern();
        disabled_by=in.readUTF().intern();
        disable_reason=in.readNullUTF();
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getDisabledBy()
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting);
        out.writeUTF(disabled_by);
        out.writeNullUTF(disable_reason);
    }

    public List<Resource> getResources() throws IOException, SQLException {
        return table.connector.getResources().getResources(this);
    }
}