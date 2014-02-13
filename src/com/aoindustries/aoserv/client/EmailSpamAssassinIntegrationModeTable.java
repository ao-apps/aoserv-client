package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

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

    public EmailSpamAssassinIntegrationMode get(String name) throws IOException, SQLException {
        return getUniqueRow(EmailSpamAssassinIntegrationMode.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
    }
}