/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  LinuxAccAddress
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentBlockTable extends CachedTableIntegerKey<EmailAttachmentBlock> {

	EmailAttachmentBlockTable(AOServConnector connector) {
		super(connector, EmailAttachmentBlock.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(EmailAttachmentBlock.COLUMN_EXTENSION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public EmailAttachmentBlock get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailAttachmentBlock.COLUMN_PKEY, pkey);
	}

	List<EmailAttachmentBlock> getEmailAttachmentBlocks(LinuxServerAccount lsa) throws IOException, SQLException {
		return getIndexedRows(EmailAttachmentBlock.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_ATTACHMENT_BLOCKS;
	}
}
