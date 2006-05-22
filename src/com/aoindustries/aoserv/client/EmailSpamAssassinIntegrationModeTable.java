package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    public EmailSpamAssassinIntegrationMode get(Object pkey) {
	return getUniqueRow(EmailSpamAssassinIntegrationMode.COLUMN_NAME, pkey);
    }
    int getTableID() {
        return SchemaTable.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
    }
}