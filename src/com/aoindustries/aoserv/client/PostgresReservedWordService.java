/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PostgresReservedWord
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.postgres_reserved_words)
public interface PostgresReservedWordService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,PostgresReservedWord> {
}
