/*
 * Copyright 2007-2013 by AO Industries, Inc.,
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
 * Logs the whois history for each account and domain combination.
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistory extends CachedObjectIntegerKey<WhoisHistory> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=2,
        COLUMN_WHOIS_OUTPUT=4
    ;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_ZONE_name = "zone";
    static final String COLUMN_TIME_name = "time";

    private long time;
    private AccountingCode accounting;
    private String zone;

    /**
     * Note: this is loaded in a separate call to the master as needed to conserve heap space, and it is null to begin with.
     */
    private String whois_output;

    Object getColumnImpl(int i) throws IOException, SQLException {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return getTime();
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return zone;
            case COLUMN_WHOIS_OUTPUT: {
                return getWhoisOutput();
            }
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    @Override
    public int getPkey() {
        return pkey;
    }
    
    public Timestamp getTime() {
        return new Timestamp(time);
    }
    
    public Business getBusiness() throws SQLException, IOException {
	Business business = table.connector.getBusinesses().get(accounting);
	if (business == null) throw new SQLException("Unable to find Business: " + accounting);
	return business;
    }
    
    /**
     * Gets the top level domain that was queried in the whois system.
     */
    public String getZone() {
        return zone;
    }

    /**
     * Gets the whois output from the database.  The first access to this for a specific object instance
     * will query the master server for the information and then cache the results.  This is done
     * to conserve heap space while still yielding high performance through the caching of the rest of the fields.
     *
     * From an outside point of view, the object is still immutable and will yield constant return
     * values per instance.
     */
    public String getWhoisOutput() throws IOException, SQLException {
        if(whois_output==null) whois_output = table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_WHOIS_HISTORY_WHOIS_OUTPUT, pkey);
        return whois_output;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.WHOIS_HISTORY;
    }

    public void init(ResultSet result) throws SQLException {
        try {
            pkey = result.getInt(1);
            time = result.getTimestamp(2).getTime();
            accounting = AccountingCode.valueOf(result.getString(3));
            zone = result.getString(4);
            // Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = result.getString(5);
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey = in.readCompressedInt();
            time = in.readLong();
            accounting = AccountingCode.valueOf(in.readUTF()).intern();
            zone = in.readUTF().intern();
            // Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = in.readUTF();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    @Override
    String toStringImpl() {
	return pkey+"|"+accounting+'|'+zone+'|'+getTime();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting.toString());
        out.writeUTF(zone);
        // Note: this is loaded in a separate call to the master as needed to conserve heap space: out.writeUTF(whois_output);
    }
}