package com.aoindustries.aoserv.client;
/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    public SignupRequestOption get(Object pkey) {
	return getUniqueRow(SignupRequestOption.COLUMN_PKEY, pkey);
    }

    public SignupRequestOption get(int pkey) {
	return getUniqueRow(SignupRequestOption.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.SIGNUP_REQUEST_OPTIONS;
    }
}
