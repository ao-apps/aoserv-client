/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategory extends AOServObjectIntegerKey implements Comparable<TicketCategory>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketCategory> {

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

    public TicketCategory(AOServConnector connector, int pkey, Integer parent, String name) {
        super(connector, pkey);
        this.parent = parent;
        this.name = name;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketCategory other) {
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
    @DependencySingleton
    @SchemaColumn(order=1, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the category id of its parent or null if this is a top-level category")
    public TicketCategory getParent() throws RemoteException {
        if(parent==null) return null;
        return getConnector().getTicketCategories().get(parent);
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketCategory(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketCategory dto) {
        this(connector, dto.getPkey(), dto.getParent(), dto.getName());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketCategory getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketCategory(key, parent, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("TicketCategory."+getDotPath()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<TicketCategory> getChildrenCategories() throws RemoteException {
        return getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByOldCategory() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_CATEGORY, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByNewCategory() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_CATEGORY, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_CATEGORY, this);
    }

    /* TODO
    public List<TicketBrandCategory> getTicketBrandCategories() throws IOException, SQLException {
        return getConnector().getTicketBrandCategories().getTicketBrandCategories(this);
    }
    */
    // </editor-fold>
}