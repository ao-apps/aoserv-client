/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  EmailSmtpRelayType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.email_smtp_relay_types)
public interface EmailSmtpRelayTypeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,EmailSmtpRelayType> {
}