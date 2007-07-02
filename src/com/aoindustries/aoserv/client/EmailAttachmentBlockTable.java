package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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

    public EmailAttachmentBlock get(Object pkey) {
	return getUniqueRow(EmailAttachmentBlock.COLUMN_PKEY, pkey);
    }

    public EmailAttachmentBlock get(int pkey) {
	return getUniqueRow(EmailAttachmentBlock.COLUMN_PKEY, pkey);
    }

    List<EmailAttachmentBlock> getEmailAttachmentBlocks(LinuxServerAccount lsa) {
        return getIndexedRows(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_ATTACHMENT_BLOCKS;
    }
}
