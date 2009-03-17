package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailSmtpRelayType.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EmailSmtpRelayType get(Object pkey) {
        try {
            return getUniqueRow(EmailSmtpRelayType.COLUMN_NAME, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_SMTP_RELAY_TYPES;
    }
}