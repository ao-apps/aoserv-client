package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * All of the operating systems referenced from other tables.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.operating_systems)
public interface OperatingSystemService extends AOServService<String,OperatingSystem> {
}
