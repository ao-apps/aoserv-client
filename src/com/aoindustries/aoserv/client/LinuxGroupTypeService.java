/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  LinuxGroupType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.linux_group_types)
public interface LinuxGroupTypeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,LinuxGroupType> {
}
