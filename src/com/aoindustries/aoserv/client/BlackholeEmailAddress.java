package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Any email sent to a <code>BlackholeEmailAddress</code> is piped
 * directly to <code>/dev/null</code> - the bit bucket - the email
 * appears to have been delivered but is simply discarded.
 *
 * @see  EmailAddress#getBlackholeEmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BlackholeEmailAddress extends CachedObjectIntegerKey<BlackholeEmailAddress> implements Removable {

    static final int COLUMN_EMAIL_ADDRESS=0;
    static final String COLUMN_EMAIL_ADDRESS_name = "email_address";

    Object getColumnImpl(int i) {
	if(i==COLUMN_EMAIL_ADDRESS) return Integer.valueOf(pkey);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public EmailAddress getEmailAddress() throws SQLException, IOException {
        EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(pkey);
        if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + pkey);
        return emailAddressObject;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        return Collections.emptyList();
    }
    
    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.BLACKHOLE_EMAIL_ADDRESSES,
            pkey
    	);
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return getEmailAddress().toStringImpl(userLocale);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
    	out.writeCompressedInt(pkey);
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getEmailAddress()
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }
}
