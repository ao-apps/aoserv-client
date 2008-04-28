package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  EmailSpamAssassinIntegrationMode
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSpamAssassinIntegrationModeTable extends GlobalTableStringKey<EmailSpamAssassinIntegrationMode> {

    EmailSpamAssassinIntegrationModeTable(AOServConnector connector) {
	super(connector, EmailSpamAssassinIntegrationMode.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailSpamAssassinIntegrationMode.COLUMN_SORT_ORDER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EmailSpamAssassinIntegrationMode get(Object pkey) {
	return getUniqueRow(EmailSpamAssassinIntegrationMode.COLUMN_NAME, pkey);
    }
    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
    }
}