package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PhoneNumberTable extends CachedTableIntegerKey<PhoneNumber> {

    PhoneNumberTable(AOServConnector connector) {
	super(connector, PhoneNumber.class);
    }

    public PhoneNumber get(Object pkey) {
	return getUniqueRow(PhoneNumber.COLUMN_PKEY, pkey);
    }

    public PhoneNumber get(int pkey) {
	return getUniqueRow(PhoneNumber.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PHONE_NUMBERS;
    }
}