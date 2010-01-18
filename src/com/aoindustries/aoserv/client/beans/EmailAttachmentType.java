/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class EmailAttachmentType {

    private String extension;
    private boolean isDefaultBlock;

    public EmailAttachmentType() {
    }

    public EmailAttachmentType(String extension, boolean isDefaultBlock) {
        this.extension = extension;
        this.isDefaultBlock = isDefaultBlock;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isIsDefaultBlock() {
        return isDefaultBlock;
    }

    public void setIsDefaultBlock(boolean isDefaultBlock) {
        this.isDefaultBlock = isDefaultBlock;
    }
}
