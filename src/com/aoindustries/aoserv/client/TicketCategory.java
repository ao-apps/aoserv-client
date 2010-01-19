/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategory extends AOServObjectIntegerKey<TicketCategory> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketCategory> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Some conveniences constants for specific categories.
     */
    public static final int AOSERV_MASTER_PKEY = 110;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Integer parent;
    private String name;

    public TicketCategory(TicketCategoryService<?,?> service, int pkey, Integer parent, String name) {
        super(service, pkey);
        this.parent = parent;
        this.name = name;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketCategory other) {
        int diff = AOServObjectUtils.compare(parent, other.parent);
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="the unique category id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_PARENT = "parent";
    @SchemaColumn(order=1, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the category id of its parent or null if this is a top-level category")
    public TicketCategory getParent() throws RemoteException {
        if(parent==null) return null;
        return getService().getConnector().getTicketCategories().get(parent);
    }

    @SchemaColumn(order=2, name="name", description="the name of this category, unique per parent")
    public String getName() {
        return name;
    }

    static final String COLUMN_SLASH_PATH = "slash_path";
    private String slashPath = null;
    @SchemaColumn(order=3, name=COLUMN_SLASH_PATH, index=IndexType.UNIQUE, description="the full path to the category, separated by slashes (/)")
    synchronized public String getSlashPath() throws RemoteException {
        if(slashPath==null) slashPath = parent==null ? name : (getParent().getSlashPath()+'/'+name);
        return slashPath;
    }

    public static final String COLUMN_DOT_PATH = "dot_path";
    private String dotPath = null;
    @SchemaColumn(order=4, name=COLUMN_DOT_PATH, index=IndexType.UNIQUE, description="the full path to the category, separated by periods (.)")
    synchronized public String getDotPath() throws RemoteException {
        if(dotPath==null) dotPath = parent==null ? name : (getParent().getDotPath()+'.'+name);
        return dotPath;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketCategory getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketCategory(key, parent, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getParent()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getTickets(),
            getChildrenCategories()
            // TODO: getTicketActionsByOldCategory(),
            // TODO: getTicketActionsByNewCategory(),
            // TODO: getTicketBrandCategorys()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketCategory."+getDotPath()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TicketCategory> getChildrenCategories() throws RemoteException {
        return getService().getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
    /* TODO
    public List<TicketBrandCategory> getTicketBrandCategorys() throws IOException, SQLException {
        return getService().getConnector().getTicketBrandCategories().getTicketBrandCategories(this);
    }

    public List<Ticket> getTickets() throws IOException, SQLException {
        return getService().getConnector().getTickets().getIndexedRows(Ticket.COLUMN_CATEGORY, pkey);
    }

    public List<TicketAction> getTicketActionsByOldCategory() throws IOException, SQLException {
        return getService().getConnector().getTicketActions().getIndexedRows(TicketAction.COLUMN_OLD_CATEGORY, pkey);
    }

    public List<TicketAction> getTicketActionsByNewCategory() throws IOException, SQLException {
        return getService().getConnector().getTicketActions().getIndexedRows(TicketAction.COLUMN_NEW_CATEGORY, pkey);
    }*/
    // </editor-fold>
}