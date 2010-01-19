package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.util.Locale;

/**
 * A <code>PackageCategory</code> represents one type of service
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class PackageCategory extends AOServObjectStringKey<PackageCategory> implements BeanFactory<com.aoindustries.aoserv.client.beans.PackageCategory> {

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
    public PackageCategory(PackageCategoryService<?,?> table, String name) {
        super(table, name);
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PackageCategory getBean() {
        return new com.aoindustries.aoserv.client.beans.PackageCategory(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "PackageCategory."+getKey()+".toString");
    }
    // </editor-fold>
}
