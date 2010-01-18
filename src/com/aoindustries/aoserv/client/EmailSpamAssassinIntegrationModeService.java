/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  EmailSpamAssassinIntegrationMode
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.email_sa_integration_modes)
public interface EmailSpamAssassinIntegrationModeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,EmailSpamAssassinIntegrationMode> {
}