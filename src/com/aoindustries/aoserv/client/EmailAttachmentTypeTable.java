package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailAttachmentType.COLUMN_EXTENSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EmailAttachmentType get(Object pkey) {
	return getUniqueRow(EmailAttachmentType.COLUMN_EXTENSION, pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_ATTACHMENT_TYPES;
    }
}