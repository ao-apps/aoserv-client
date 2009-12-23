package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  PackageCategory
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.package_categories)
public interface PackageCategoryService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>>extends AOServServiceStringKey<C,F,PackageCategory> {
}
