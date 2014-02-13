package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  LinuxAccAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentBlockTable extends CachedTableIntegerKey<EmailAttachmentBlock> {

    EmailAttachmentBlockTable(AOServConnector connector) {
	super(connector, EmailAttachmentBlock.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(EmailAttachmentBlock.COLUMN_EXTENSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EmailAttachmentBlock get(int pkey) throws IOException, SQLException {
        return getUniqueRow(EmailAttachmentBlock.COLUMN_PKEY, pkey);
    }

    List<EmailAttachmentBlock> getEmailAttachmentBlocks(LinuxServerAccount lsa) throws IOException, SQLException {
        return getIndexedRows(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_ATTACHMENT_BLOCKS;
    }
}