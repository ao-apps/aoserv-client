/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * A <code>PackageCategory</code> represents one type of service
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class PackageCategory extends AOServObjectStringKey implements Comparable<PackageCategory>, DtoFactory<com.aoindustries.aoserv.client.dto.PackageCategory> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        AOSERV="aoserv",
        APPLICATION="application",
        BACKUP="backup",
        COLOCATION="colocation",
        DEDICATED="dedicated",
        MANAGED="managed",
        RESELLER="reseller",
        SYSADMIN="sysadmin",
        VIRTUAL="virtual",
        VIRTUAL_DEDICATED="virtual_dedicated",
        VIRTUAL_MANAGED="virtual_managed"
    ;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fields">
    public PackageCategory(AOServConnector connector, String name) {
        super(connector, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PackageCategory other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the unique name of this resource type.
     */
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the category name")
    public String getName() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PackageCategory(AOServConnector connector, com.aoindustries.aoserv.client.dto.PackageCategory dto) {
        this(connector, dto.getName());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PackageCategory getDto() {
        return new com.aoindustries.aoserv.client.dto.PackageCategory(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("PackageCategory."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<PackageDefinition> getPackageDefinitions() throws RemoteException {
        return getConnector().getPackageDefinitions().filterIndexed(PackageDefinition.COLUMN_CATEGORY, this);
    }
    // </editor-fold>
}
