/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class EmailAttachmentType extends AOServObject {

    private String extension;
    private boolean defaultBlock;

    public EmailAttachmentType() {
    }

    public EmailAttachmentType(String extension, boolean defaultBlock) {
        this.extension = extension;
        this.defaultBlock = defaultBlock;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isDefaultBlock() {
        return defaultBlock;
    }

    public void setDefaultBlock(boolean defaultBlock) {
        this.defaultBlock = defaultBlock;
    }
}
