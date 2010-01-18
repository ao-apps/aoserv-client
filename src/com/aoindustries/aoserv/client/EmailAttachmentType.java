/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

/**
 * An <code>EmailAttachmentType</code> represents one extension that may
 * be blocked by virus filters.
 *
 * @see  EmailAttachmentBlock
 *
 * @author  AO Industries, Inc.
 */
final public class EmailAttachmentType extends AOServObjectStringKey<EmailAttachmentType> implements BeanFactory<com.aoindustries.aoserv.client.beans.EmailAttachmentType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean isDefaultBlock;

    public EmailAttachmentType(EmailAttachmentTypeService<?,?> service, String extension, boolean isDefaultBlock) {
        super(service, extension);
        this.isDefaultBlock = isDefaultBlock;
    }
    // </editor-fold>

    public String getExtension() {
        return key;
    }
    
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "EmailAttachmentType."+key+".description");
    }

    public boolean isDefaultBlock() {
        return isDefaultBlock;
    }
}