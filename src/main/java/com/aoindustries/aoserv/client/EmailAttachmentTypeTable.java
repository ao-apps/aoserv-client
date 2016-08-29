/*
 * Copyright 2004-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  EmailAttachmentType
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

	@Override
	public EmailAttachmentType get(String extension) throws IOException, SQLException {
		return getUniqueRow(EmailAttachmentType.COLUMN_EXTENSION, extension);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_ATTACHMENT_TYPES;
	}
}
