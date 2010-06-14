package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UserId;

/**
 * @see  MasterUser
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.master_users)
public interface MasterUserService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,UserId,MasterUser> {
}