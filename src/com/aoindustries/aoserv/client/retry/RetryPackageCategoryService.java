package com.aoindustries.aoserv.client.retry;

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
final class RetryPackageCategoryService extends RetryServiceStringKey<PackageCategory> implements PackageCategoryService<RetryConnector,RetryConnectorFactory> {

    RetryPackageCategoryService(RetryConnector connector) {
        super(connector, PackageCategory.class);
    }
}
