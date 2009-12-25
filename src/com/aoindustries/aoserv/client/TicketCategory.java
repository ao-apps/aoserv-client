package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategory extends AOServObjectIntegerKey<TicketCategory> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Some conveniences constants for specific categories.
     */
    public static final int AOSERV_MASTER_PKEY = 110;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int parent;
    final String name;

    public TicketCategory(TicketCategoryService<?,?> service, int pkey, int parent, String name) {
        super(service, pkey);
        this.parent = parent;
        this.name = name.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketCategory other) {
        int diff = compare(parent, other.parent);
        if(diff!=0) return diff;
        return compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="the unique category id")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="parent", description="the category id of its parent or null if this is a top-level category")
    public TicketCategory getParent() throws RemoteException {
        if(parent==-1) return null;
        TicketCategory tc = getService().getConnector().getTicketCategories().get(parent);
        if(tc==null) throw new RemoteException("Unable to find TicketCategory: "+parent);
        return tc;
    }

    @SchemaColumn(order=2, name="name", description="the name of this category, unique per parent")
    public String getName() {
        return name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getParent()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getTickets(),
            // TODO: getChildrenCategories(),
            // TODO: getTicketActionsByOldCategory(),
            // TODO: getTicketActionsByNewCategory(),
            // TODO: getTicketBrandCategorys()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    private String slashPath = null;
    synchronized public String getSlashPath() throws RemoteException {
        if(slashPath==null) slashPath = parent==-1 ? name : (getParent().getSlashPath()+'/'+name);
        return slashPath;
    }

    private String dotPath = null;
    synchronized public String getDotPath() throws RemoteException {
        if(dotPath==null) dotPath = parent==-1 ? name : (getParent().getDotPath()+'.'+name);
        return dotPath;
    }

    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketCategory."+getDotPath()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<TicketBrandCategory> getTicketBrandCategorys() throws IOException, SQLException {
        return table.connector.getTicketBrandCategories().getTicketBrandCategories(this);
    }

    public List<TicketCategory> getChildrenCategories() throws IOException, SQLException {
        return table.connector.getTicketCategories().getChildrenCategories(this);
    }

    public List<Ticket> getTickets() throws IOException, SQLException {
        return table.connector.getTickets().getIndexedRows(Ticket.COLUMN_CATEGORY, pkey);
    }

    public List<TicketAction> getTicketActionsByOldCategory() throws IOException, SQLException {
        return table.connector.getTicketActions().getIndexedRows(TicketAction.COLUMN_OLD_CATEGORY, pkey);
    }

    public List<TicketAction> getTicketActionsByNewCategory() throws IOException, SQLException {
        return table.connector.getTicketActions().getIndexedRows(TicketAction.COLUMN_NEW_CATEGORY, pkey);
    }*/
    // </editor-fold>
}