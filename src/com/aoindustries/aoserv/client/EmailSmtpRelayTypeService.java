/*
 * Copyright 2003-2011 by AO Industries, Inc.,
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
public interface EmailSmtpRelayTypeService extends AOServService<String,EmailSmtpRelayType> {
}