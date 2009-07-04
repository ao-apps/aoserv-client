package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>EmailList</code> may receive email on multiple addresses, and
 * then forward those emails to the list of destinations.  An
 * <code>EmailListAddress</code> directs incoming emails to the email list.
 *
 * @see  EmailList
 * @see  EmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailListAddress extends CachedObjectIntegerKey<EmailListAddress> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_EMAIL_ADDRESS=1,
        COLUMN_EMAIL_LIST=2
    ;
    static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
    static final String COLUMN_EMAIL_LIST_name = "email_list";

    int email_address;
    int email_list;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_EMAIL_ADDRESS: return Integer.valueOf(email_address);
            case COLUMN_EMAIL_LIST: return Integer.valueOf(email_list);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public EmailAddress getEmailAddress() throws SQLException, IOException {
	EmailAddress emailAddressObject = table.connector.getEmailAddresses().get(email_address);
	if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
	return emailAddressObject;
    }

    public EmailList getEmailList() throws SQLException, IOException {
	EmailList emailListObject = table.connector.getEmailLists().get(email_list);
	if (emailListObject == null) throw new SQLException("Unable to find EmailList: " + email_list);
	return emailListObject;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_LIST_ADDRESSES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	email_address=result.getInt(2);
	email_list=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	email_address=in.readCompressedInt();
	email_list=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Cannot be used as the list for a majordomo list
        for(MajordomoList ml : table.connector.getMajordomoLists().getRows()) {
            if(ml.getListListAddress().pkey==pkey) {
                EmailDomain ed=ml.getMajordomoServer().getDomain();
                reasons.add(new CannotRemoveReason<MajordomoList>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
            }
        }

        return reasons;
    }
    
    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_LIST_ADDRESSES,
            pkey
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return getEmailAddress().toStringImpl(userLocale)+"->"+getEmailList().getPath();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeCompressedInt(email_address);
	out.writeCompressedInt(email_list);
    }
}