package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.technologies)
public interface TechnologyService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,Technology> {

    /* TODO
    List<Technology> getTechnologies(TechnologyName techName) throws IOException, SQLException {
        return getIndexedRows(Technology.COLUMN_NAME, techName.pkey);
    }*/
}
