package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailAttachmentType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentTypeTable extends GlobalTableStringKey<EmailAttachmentType> {

    EmailAttachmentTypeTable(AOServConnector connector) {
	super(connector, EmailAttachmentType.class);
    }

    public EmailAttachmentType get(Object pkey) {
	return getUniqueRow(EmailAttachmentType.COLUMN_EXTENSION, pkey);
    }

    int getTableID() {
        return SchemaTable.EMAIL_ATTACHMENT_TYPES;
    }
}