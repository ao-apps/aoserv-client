/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;

/**
 * @see  Shell
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.shells)
public interface ShellService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,UnixPath,Shell> {
}
