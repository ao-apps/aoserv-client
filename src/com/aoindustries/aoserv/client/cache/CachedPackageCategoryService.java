package com.aoindustries.aoserv.client.cache;

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
final class CachedPackageCategoryService extends CachedServiceStringKey<PackageCategory> implements PackageCategoryService<CachedConnector,CachedConnectorFactory> {

    CachedPackageCategoryService(CachedConnector connector, PackageCategoryService<?,?> wrapped) {
        super(connector, PackageCategory.class, wrapped);
    }
}
