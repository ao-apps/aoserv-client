package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * <code>SendmailSmtpStat</code>s keep track of daily SMTP use on a per-package basis.
 *
 * @see  Package#getDailySmtpInLimit()
 * @see  Package#getDailySmtpInBandwidthLimit()
 * @see  Package#getDailySmtpOutLimit()
 * @see  Package#getDailySmtpOutBandwidthLimit()
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SendmailSmtpStat extends AOServObject<Integer,SendmailSmtpStat> implements SingleTableObject<Integer,SendmailSmtpStat> {

    static final int COLUMN_PKEY=0;

    /**
     * The maximum number of days that statistics will be maintained.  This is roughly one year.
     */
    public static final int MAX_STATISTICS_AGE=366;

    private int pkey;
    String packageName;
    private long date;
    private int ao_server;
    private int email_in_count;
    private int excess_in_count;
    private long email_in_bandwidth;
    private long excess_in_bandwidth;
    private int email_out_count;
    private int excess_out_count;
    private long email_out_bandwidth;
    private long excess_out_bandwidth;

    protected AOServTable<Integer,SendmailSmtpStat> table;

    boolean equalsImpl(Object O) {
	return
            O instanceof SendmailSmtpStat
            && ((SendmailSmtpStat)O).pkey==pkey
	;
    }

    public Object getColumn(int i) {
        switch(i) {
            case 0: return Integer.valueOf(pkey);
            case 1: return packageName;
            case 2: return new Date(date);
            case 3: return Integer.valueOf(ao_server);
            case 4: return Integer.valueOf(email_in_count);
            case 5: return Integer.valueOf(excess_in_count);
            case 6: return Long.valueOf(email_in_bandwidth);
            case 7: return Long.valueOf(excess_in_bandwidth);
            case 8: return Integer.valueOf(email_out_count);
            case 9: return Integer.valueOf(excess_out_count);
            case 10: return Long.valueOf(email_out_bandwidth);
            case 11: return Long.valueOf(excess_out_bandwidth);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPKey() {
        return pkey;
    }

    public Package getPackage() {
        Package pk=table.connector.packages.get(packageName);
        if(pk==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageName));
        return pk;
    }

    public long getDate() {
        return date;
    }

    public AOServer getAOServer() {
        AOServer ao=table.connector.aoServers.get(ao_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return ao;
    }

    public int getEmailInCount() {
        return email_in_count;
    }
    
    public int getExcessInCount() {
        return excess_in_count;
    }

    public long getEmailInBandwidth() {
        return email_in_bandwidth;
    }
    
    public long getExcessInBandwidth() {
        return excess_in_bandwidth;
    }

    public int getEmailOutCount() {
        return email_out_count;
    }
    
    public int getExcessOutCount() {
        return excess_out_count;
    }

    public long getEmailOutBandwidth() {
        return email_out_bandwidth;
    }
    
    public long getExcessOutBandwidth() {
        return excess_out_bandwidth;
    }

    public Integer getKey() {
	return pkey;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Integer,SendmailSmtpStat> getTable() {
        return table;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SENDMAIL_SMTP_STATS;
    }

    int hashCodeImpl() {
	return pkey;
    }

    public void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        packageName=result.getString(2);
        date=result.getDate(3).getTime();
        ao_server=result.getInt(4);
        email_in_count=result.getInt(5);
        excess_in_count=result.getInt(6);
        email_in_bandwidth=result.getLong(7);
        excess_in_bandwidth=result.getLong(8);
        email_out_count=result.getInt(9);
        excess_out_count=result.getInt(10);
        email_out_bandwidth=result.getLong(11);
        excess_out_bandwidth=result.getLong(12);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        packageName=in.readUTF();
        date=in.readLong();
        ao_server=in.readCompressedInt();
        email_in_count=in.readCompressedInt();
        excess_in_count=in.readCompressedInt();
        email_in_bandwidth=in.readLong();
        excess_in_bandwidth=in.readLong();
        email_out_count=in.readCompressedInt();
        excess_out_count=in.readCompressedInt();
        email_out_bandwidth=in.readLong();
        excess_out_bandwidth=in.readLong();
    }

    public void setTable(AOServTable<Integer,SendmailSmtpStat> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeUTF(packageName);
        out.writeLong(date);
        out.writeCompressedInt(ao_server);
        out.writeCompressedInt(email_in_count);
        out.writeCompressedInt(excess_in_count);
        out.writeLong(email_in_bandwidth);
        out.writeLong(excess_in_bandwidth);
        out.writeCompressedInt(email_out_count);
        out.writeCompressedInt(excess_out_count);
        out.writeLong(email_out_bandwidth);
        out.writeLong(excess_out_bandwidth);
    }
}
