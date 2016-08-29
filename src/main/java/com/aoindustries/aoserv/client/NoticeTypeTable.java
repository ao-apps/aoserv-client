/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  NoticeType
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeTypeTable extends GlobalTableStringKey<NoticeType> {

	NoticeTypeTable(AOServConnector connector) {
		super(connector, NoticeType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NoticeType.COLUMN_TYPE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NoticeType get(String type) throws IOException, SQLException {
		return getUniqueRow(NoticeType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NOTICE_TYPES;
	}
}
