package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  MySQLReservedWord
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.mysql_reserved_words)
public interface MySQLReservedWordService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,MySQLReservedWord> {
}
