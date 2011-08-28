/*
 * Copyright 2005-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * A <code>PackageDefinitionBusiness</code> grants a business the ability to add subaccounts of the specified package definition.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class PackageDefinitionBusiness extends AOServObjectIntegerKey implements Comparable<PackageDefinitionBusiness>, DtoFactory<com.aoindustries.aoserv.client.dto.PackageDefinitionBusiness> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 6175564218523408872L;

    final private int packageDefinition;
    private AccountingCode accounting;
    final private String display;
    final private String description;
    final private boolean active;

    public PackageDefinitionBusiness(
        AOServConnector connector,
        int pkey,
        int packageDefinition,
        AccountingCode accounting,
        String display,
        String description,
        boolean active
    ) {
        super(connector, pkey);
        this.packageDefinition = packageDefinition;
        this.accounting = accounting;
        this.display = display;
        this.description = description;
        this.active = active;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PackageDefinitionBusiness other) {
        try {
            int diff = packageDefinition==other.packageDefinition ? 0 : getPackageDefinition().compareTo(other.getPackageDefinition());
            if(diff!=0) return diff;
            return accounting.toUpperCase()==other.accounting.toUpperCase() ? 0 : getBusiness().compareTo(other.getBusiness());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique identifier")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_PACKAGE_DEFINITION = getMethodColumn(PackageDefinitionBusiness.class, "packageDefinition");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the pkey of the package definition")
    public PackageDefinition getPackageDefinition() throws RemoteException {
        return getConnector().getPackageDefinitions().get(packageDefinition);
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(PackageDefinitionBusiness.class, "business");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the business that is allowed to create subaccounts with this package")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=3, description="a short description for display use")
    public String getDisplay() {
        return display;
    }

    @SchemaColumn(order=4, description="a longer description of the package")
    public String getDescription() {
        return description;
    }

    @SchemaColumn(order=5, description="includes this package in signup forms and advertising")
    public boolean isActive() {
        return active;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PackageDefinitionBusiness(AOServConnector connector, com.aoindustries.aoserv.client.dto.PackageDefinitionBusiness dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getPackageDefinition(),
            getAccountingCode(dto.getAccounting()),
            dto.getDisplay(),
            dto.getDescription(),
            dto.isActive()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PackageDefinitionBusiness getDto() {
        return new com.aoindustries.aoserv.client.dto.PackageDefinitionBusiness(
            getKeyInt(),
            packageDefinition,
            getDto(accounting),
            display,
            description,
            active
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return display;
    }
    // </editor-fold>
}
