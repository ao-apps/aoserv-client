package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * An <code>EmailAttachmentBlock</code> restricts one attachment type on one email inbox.
 *
 * @see  EmailAttachmentType
 * @see  LinuxServerAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentBlock extends CachedObjectIntegerKey<EmailAttachmentBlock> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_LINUX_SERVER_ACCOUNT=1
    ;
    static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
    static final String COLUMN_EXTENSION_name = "extension";

    int linux_server_account;
    String extension;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_LINUX_SERVER_ACCOUNT: return Integer.valueOf(linux_server_account);
            case 2: return extension;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
	LinuxServerAccount lsa=table.connector.getLinuxServerAccounts().get(linux_server_account);
	if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
	return lsa;
    }

    public EmailAttachmentType getEmailAttachmentType() throws SQLException {
        EmailAttachmentType eat=table.connector.getEmailAttachmentTypes().get(extension);
        if(eat==null) throw new SQLException("Unable to find EmailAttachmentType: " + extension);
	return eat;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_ATTACHMENT_BLOCKS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	linux_server_account=result.getInt(2);
	extension=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	linux_server_account=in.readCompressedInt();
	extension=in.readUTF().intern();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws SQLException, IOException {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_ATTACHMENT_BLOCKS,
            pkey
	);
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        return getLinuxServerAccount().toString()+"->"+extension;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeCompressedInt(linux_server_account);
	out.writeUTF(extension);
    }
}