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
 * Any number of <code>EmailAddress</code>es may be directed to
 * an <code>EmailPipe</code>.  An <code>EmailPipeAddress</code>
 * makes this connection.
 *
 * @see  EmailPipe
 * @see  EmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipeAddress extends CachedObjectIntegerKey<EmailPipeAddress> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_EMAIL_ADDRESS=1
    ;
    static final String COLUMN_EMAIL_ADDRESS_name = "email_address";
    static final String COLUMN_EMAIL_PIPE_name = "email_pipe";

    int email_address;
    int email_pipe;

    Object getColumnImpl(int i) {
        if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==COLUMN_EMAIL_ADDRESS) return Integer.valueOf(email_address);
	if(i==2) return Integer.valueOf(email_pipe);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public EmailAddress getEmailAddress() throws SQLException, IOException {
	EmailAddress emailAddressObject = table.connector.emailAddresses.get(email_address);
	if (emailAddressObject == null) throw new SQLException("Unable to find EmailAddress: " + email_address);
	return emailAddressObject;
    }

    public EmailPipe getEmailPipe() throws SQLException, IOException {
	EmailPipe emailPipeObject = table.connector.emailPipes.get(email_pipe);
	if (emailPipeObject == null) throw new SQLException("Unable to find EmailPipe: " + email_pipe);
	return emailPipeObject;
    }
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_PIPE_ADDRESSES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	email_address=result.getInt(2);
	email_pipe=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	email_address=in.readCompressedInt();
	email_pipe=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Cannot be used as any part of a majordomo list
        for(MajordomoList ml : table.connector.majordomoLists.getRows()) {
            if(
                ml.getListPipeAddress().pkey==pkey
                || ml.getListRequestPipeAddress().pkey==pkey
            ) {
                EmailDomain ed=ml.getMajordomoServer().getDomain();
                reasons.add(new CannotRemoveReason<MajordomoList>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
            }
        }

        // Cannot be used as any part of a majordomo server
        for(MajordomoServer ms : table.connector.majordomoServers.getRows()) {
            if(ms.getMajordomoPipeAddress().pkey==pkey) {
                EmailDomain ed=ms.getDomain();
                reasons.add(new CannotRemoveReason("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname()));
            }
        }
        
        return reasons;
    }

    public void remove() throws IOException, SQLException {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_PIPE_ADDRESSES,
            pkey
	);
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        return getEmailAddress()+"->"+getEmailPipe().getPath();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeCompressedInt(email_address);
	out.writeCompressedInt(email_pipe);
    }
}