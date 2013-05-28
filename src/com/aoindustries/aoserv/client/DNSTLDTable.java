/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  DNSTLD
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTLDTable extends GlobalTableDomainNameKey<DNSTLD> {

    DNSTLDTable(AOServConnector connector) {
	super(connector, DNSTLD.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DNSTLD.COLUMN_DOMAIN_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    @Override
    public DNSTLD get(DomainName domain) throws IOException, SQLException {
        return getUniqueRow(DNSTLD.COLUMN_DOMAIN, domain);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_TLDS;
    }
}