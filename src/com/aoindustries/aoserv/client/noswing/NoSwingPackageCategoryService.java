package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingPackageCategoryService extends NoSwingServiceStringKey<PackageCategory> implements PackageCategoryService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingPackageCategoryService(NoSwingConnector connector, PackageCategoryService<?,?> wrapped) {
        super(connector, PackageCategory.class, wrapped);
    }
}
