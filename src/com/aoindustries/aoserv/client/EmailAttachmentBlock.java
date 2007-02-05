package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
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

    int linux_server_account;
    String extension;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_LINUX_SERVER_ACCOUNT: return Integer.valueOf(linux_server_account);
            case 2: return extension;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public LinuxServerAccount getLinuxServerAccount() {
	LinuxServerAccount lsa=table.connector.linuxServerAccounts.get(linux_server_account);
	if(lsa==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: " + linux_server_account));
	return lsa;
    }

    public EmailAttachmentType getEmailAttachmentType() {
        EmailAttachmentType eat=table.connector.emailAttachmentTypes.get(extension);
        if(eat==null) throw new WrappedException(new SQLException("Unable to find EmailAttachmentType: " + extension));
	return eat;
    }

    protected int getTableIDImpl() {
	return SchemaTable.EMAIL_ATTACHMENT_BLOCKS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	linux_server_account=result.getInt(2);
	extension=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	linux_server_account=in.readCompressedInt();
	extension=in.readUTF();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.EMAIL_ATTACHMENT_BLOCKS,
            pkey
	);
    }

    String toStringImpl() {
        return getLinuxServerAccount().toString()+"->"+extension;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeCompressedInt(linux_server_account);
	out.writeUTF(extension);
    }
}