/*
 * Copyright 2005-2010 by AO Industries, Inc.,
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
public interface EmailSpamAssassinIntegrationModeService extends AOServService<String,EmailSpamAssassinIntegrationMode> {
}