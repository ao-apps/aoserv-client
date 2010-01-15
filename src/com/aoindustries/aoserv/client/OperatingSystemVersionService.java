package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * All of the operating system versions referenced from other tables.
 *
 * @see OperatingSystemVersion
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.operating_system_versions)
public interface OperatingSystemVersionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,OperatingSystemVersion> {

    /* TODO
    OperatingSystemVersion getOperatingSystemVersion(OperatingSystem os, String version, Architecture architecture) throws IOException, SQLException {
        String name=os.pkey;
        String arch=architecture.pkey;
        for(OperatingSystemVersion osv : getRows()) {
            if(
                osv.version_name.equals(name)
                && osv.version_number.equals(version)
                && osv.architecture.equals(arch)
            ) return osv;
        }
        return null;
    }
     */
}
