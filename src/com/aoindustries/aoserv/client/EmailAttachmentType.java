/*
 * Copyright 2004-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

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

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="extension", index=IndexType.PRIMARY_KEY, description="the unique filename extension")
    public String getExtension() {
        return getKey();
    }
    
    @SchemaColumn(order=1, name="is_default_block", description="indicates that the type will be blocked by default")
    public boolean isDefaultBlock() {
        return isDefaultBlock;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.EmailAttachmentType getBean() {
        return new com.aoindustries.aoserv.client.beans.EmailAttachmentType(getKey(), isDefaultBlock);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailAttachmentBlocks());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("EmailAttachmentType."+getKey()+".description");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IndexedSet<EmailAttachmentBlock> getEmailAttachmentBlocks() throws RemoteException {
        return getService().getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}