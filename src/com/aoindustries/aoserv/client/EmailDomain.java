/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>EmailDomain</code> is one hostname/domain of email
 * addresses hosted on a <code>Server</code>.  Multiple, unique
 * email addresses may be hosted within the <code>EmailDomain</code>.
 * In order for mail to be routed to the <code>Server</code>, a
 * <code>DNSRecord</code> entry of type <code>MX</code> must
 * point to the <code>Server</code>.
 *
 * @see  EmailAddress
 * @see  DNSRecord
 * @see  DNSType#MX
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
public final class EmailDomain extends CachedObjectIntegerKey<EmailDomain> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_PACKAGE=3
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_DOMAIN_name = "domain";

    private DomainName domain;
    int ao_server;
    String packageName;

    public int addEmailAddress(String address) throws SQLException, IOException {
	return table.connector.getEmailAddresses().addEmailAddress(address, this);
    }

    public void addMajordomoServer(
        LinuxServerAccount linuxServerAccount,
        LinuxServerGroup linuxServerGroup,
        MajordomoVersion majordomoVersion
    ) throws IOException, SQLException {
        table.connector.getMajordomoServers().addMajordomoServer(
            this,
            linuxServerAccount,
            linuxServerGroup,
            majordomoVersion
        );
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return domain;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case COLUMN_PACKAGE: return packageName;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DomainName getDomain() {
	return domain;
    }

    public EmailAddress getEmailAddress(String address) throws IOException, SQLException {
	return table.connector.getEmailAddresses().getEmailAddress(address, this);
    }

    public List<EmailAddress> getEmailAddresses() throws IOException, SQLException {
	return table.connector.getEmailAddresses().getEmailAddresses(this);
    }

    public MajordomoServer getMajordomoServer() throws IOException, SQLException {
	return table.connector.getMajordomoServers().get(pkey);
    }

    public Package getPackage() throws SQLException, IOException {
	Package packageObject = table.connector.getPackages().get(packageName);
	if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
	return packageObject;
    }

    public AOServer getAOServer() throws SQLException, IOException {
	AOServer ao=table.connector.getAoServers().get(ao_server);
	if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
	return ao;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_DOMAINS;
    }

    public void init(ResultSet result) throws SQLException {
        try {
            pkey=result.getInt(1);
            domain=DomainName.valueOf(result.getString(2));
            ao_server=result.getInt(3);
            packageName=result.getString(4);
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    /**
     * @deprecated  Use DomainName.validate instead.
     */
    @Deprecated
    public static boolean isValidFormat(String name) {
        if("localhost".equals(name)) return false;

        // Must contain at least one period
	int pos = name.indexOf('.');
	// Must have something before
	if (pos > 0) {
            // The first character must not be -
            if (name.charAt(0) == '-') return false;
            // Must have something afterwards
            int len = name.length();
            if (pos < (len - 1)) {
                // Must not end with .
                if (name.charAt(len - 1) != '.') {
                    // Must not have ..
                    if (name.indexOf("..") == -1) {
                        // All remaining characters must be [a-z,0-9,.,-]
                        for (int c = 0; c < len; c++) {
                            char ch = name.charAt(c);
                            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '.' && ch != '-') return false;
                        }
                        return true;
                    }
                }
            }
	}
	return false;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            domain=DomainName.valueOf(in.readUTF());
            ao_server=in.readCompressedInt();
            packageName=in.readUTF().intern();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        MajordomoServer ms=getMajordomoServer();
        if(ms!=null) {
            EmailDomain ed=ms.getDomain();
            reasons.add(new CannotRemoveReason<MajordomoServer>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
        }

        for(EmailAddress ea : getEmailAddresses()) reasons.addAll(ea.getCannotRemoveReasons());

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_DOMAINS,
            pkey
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(domain.toString());
        out.writeCompressedInt(ao_server);
        out.writeUTF(packageName);
    }
}