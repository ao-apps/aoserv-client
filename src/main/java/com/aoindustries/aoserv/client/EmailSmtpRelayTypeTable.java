/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  EmailSmtpRelayType
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

	@Override
	public EmailSmtpRelayType get(String name) throws IOException, SQLException {
		return getUniqueRow(EmailSmtpRelayType.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_SMTP_RELAY_TYPES;
	}
}
