/*
 * Copyright 2002-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
    private AccountingCode accounting;
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
            .getPackage()
            .getBusiness()
            .isBusinessOrParentOf(
                disabledBy
                .getUsername()
                .getPackage()
                .getBusiness()
            )
        ;
    }

    Object getColumnImpl(int i) {
	if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==1) return getTime();
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

    public Timestamp getTime() {
        return new Timestamp(time);
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
        try {
            pkey=result.getInt(1);
            time=result.getTimestamp(2).getTime();
            accounting=AccountingCode.valueOf(result.getString(3));
            disabled_by=result.getString(4);
            disable_reason=result.getString(5);
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            time=in.readLong();
            accounting=AccountingCode.valueOf(in.readUTF()).intern();
            disabled_by=in.readUTF().intern();
            disable_reason=in.readNullUTF();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting.toString());
        out.writeUTF(disabled_by);
        out.writeNullUTF(disable_reason);
    }
}