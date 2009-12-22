package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PackageCategory;
import com.aoindustries.aoserv.client.PackageCategoryService;

/**
 * @author  AO Industries, Inc.
 */
final class RmiPackageCategoryService extends RmiServiceStringKey<PackageCategory> implements PackageCategoryService<RmiConnector,RmiConnectorFactory> {

    RmiPackageCategoryService(RmiConnector connector) {
        super(connector, PackageCategory.class);
    }
}
