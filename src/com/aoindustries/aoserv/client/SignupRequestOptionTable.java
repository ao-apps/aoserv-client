package com.aoindustries.aoserv.client;
/*
 * Copyright 2007-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  SignupRequestOption
 *
 * @author  AO Industries, Inc.
 */
final public class SignupRequestOptionTable extends CachedTableIntegerKey<SignupRequestOption> {

    SignupRequestOptionTable(AOServConnector connector) {
	super(connector, SignupRequestOption.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(SignupRequestOption.COLUMN_REQUEST_name+'.'+SignupRequest.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(SignupRequestOption.COLUMN_REQUEST_name+'.'+SignupRequest.COLUMN_TIME_name, ASCENDING),
        new OrderBy(SignupRequestOption.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SignupRequestOption get(Object pkey) {
	return getUniqueRow(SignupRequestOption.COLUMN_PKEY, pkey);
    }

    public SignupRequestOption get(int pkey) {
	return getUniqueRow(SignupRequestOption.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SIGNUP_REQUEST_OPTIONS;
    }
}
