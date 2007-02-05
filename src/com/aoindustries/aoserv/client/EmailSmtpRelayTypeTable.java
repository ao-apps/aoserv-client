package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailSmtpRelayType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSmtpRelayTypeTable extends GlobalTableStringKey<EmailSmtpRelayType> {

    EmailSmtpRelayTypeTable(AOServConnector connector) {
	super(connector, EmailSmtpRelayType.class);
    }

    public EmailSmtpRelayType get(Object pkey) {
	return getUniqueRow(EmailSmtpRelayType.COLUMN_NAME, pkey);
    }

    int getTableID() {
	return SchemaTable.EMAIL_SMTP_RELAY_TYPES;
    }
}