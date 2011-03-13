/*
 * Copyright 2004-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  EmailAttachmentType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.email_attachment_types)
public interface EmailAttachmentTypeService extends AOServService<String,EmailAttachmentType> {
}