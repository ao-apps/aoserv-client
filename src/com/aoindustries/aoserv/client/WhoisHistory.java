package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
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
 * Logs the whois history for each account and domain combination.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistory extends CachedObjectIntegerKey<WhoisHistory> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=2
    ;

    private long time;
    String accounting;
    private String zone;

    /**
     * Note: this is loaded in a separate call to the master as needed to conserve heap space, and it is null to begin with.
     */
    private String whois_output;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return new java.sql.Date(time);
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return zone;
            case 4: {
                //try {
                    return getWhoisOutput();
                //} catch(IOException err) {
                //    throw new WrappedException(err);
                //} catch(SQLException err) {
                //    throw new WrappedException(err);
                //}
            }
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPkey() {
        return pkey;
    }
    
    public long getTime() {
        return time;
    }
    
    public Business getBusiness() {
	Business business = table.connector.businesses.get(accounting);
	if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
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
    public String getWhoisOutput() {
        if(whois_output==null) whois_output = table.connector.requestStringQuery(AOServProtocol.CommandID.GET_WHOIS_HISTORY_WHOIS_OUTPUT, pkey);
        return whois_output;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.WHOIS_HISTORY;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
        time = result.getTimestamp(2).getTime();
        accounting = result.getString(3);
        zone = result.getString(4);
        // Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey = in.readCompressedInt();
        time = in.readLong();
        accounting = in.readUTF().intern();
        zone = in.readUTF().intern();
        // Note: this is loaded in a separate call to the master as needed to conserve heap space: whois_output = in.readUTF();
    }

    String toStringImpl() {
	return pkey+"|"+accounting+'|'+zone+'|'+new java.sql.Date(time);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeLong(time);
        out.writeUTF(accounting);
        out.writeUTF(zone);
        // Note: this is loaded in a separate call to the master as needed to conserve heap space: out.writeUTF(whois_output);
    }
}