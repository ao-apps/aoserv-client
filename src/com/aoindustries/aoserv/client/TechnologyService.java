package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.technologies)
public interface TechnologyService extends AOServService<Integer,Technology> {

    /* TODO
    List<Technology> getTechnologies(TechnologyName techName) throws IOException, SQLException {
        return getIndexedRows(Technology.COLUMN_NAME, techName.pkey);
    }*/
}
