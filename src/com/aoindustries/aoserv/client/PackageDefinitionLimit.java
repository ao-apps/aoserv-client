/*
 * Copyright 2005-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

/**
 * A <code>PackageDefinitionLimit</code> stores the per-resource type limit and rates that are part of a <code>PackageDefinition</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class PackageDefinitionLimit extends AOServObjectIntegerKey implements Comparable<PackageDefinitionLimit>, DtoFactory<com.aoindustries.aoserv.client.dto.PackageDefinitionLimit> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int packageDefinition;
    private String resourceType;
    final private Integer softLimit;
    final private Integer hardLimit;
    final private Money additionalRate;
    private String additionalTransactionType;

    public PackageDefinitionLimit(
        AOServConnector connector,
        int pkey,
        int packageDefinition,
        String resourceType,
        Integer softLimit,
        Integer hardLimit,
        Money additionalRate,
        String additionalTransactionType
    ) {
        super(connector, pkey);
        this.packageDefinition = packageDefinition;
        this.resourceType = resourceType;
        this.softLimit = softLimit;
        this.hardLimit = hardLimit;
        this.additionalRate = additionalRate;
        this.additionalTransactionType = additionalTransactionType;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        resourceType = intern(resourceType);
        additionalTransactionType = intern(additionalTransactionType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PackageDefinitionLimit other) {
        try {
            int diff = packageDefinition==other.packageDefinition ? 0 : getPackageDefinition().compareTo(other.getPackageDefinition());
            if(diff!=0) return diff;
            return resourceType==other.resourceType ? 0 : getResourceType().compareTo(other.getResourceType()); // OK - interned
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique identifier for this limit")
    public int getPkey() {
        return key;
    }

    public static final MethodColumn COLUMN_PACKAGE_DEFINITION = getMethodColumn(PackageDefinitionLimit.class, "packageDefinition");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the pkey of the package definition")
    public PackageDefinition getPackageDefinition() throws RemoteException {
        return getConnector().getPackageDefinitions().get(packageDefinition);
    }

    public static final MethodColumn COLUMN_RESOURCE_TYPE = getMethodColumn(PackageDefinitionLimit.class, "resourceType");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the resource type")
    public ResourceType getResourceType() throws RemoteException {
        return getConnector().getResourceTypes().get(resourceType);
    }

    @SchemaColumn(order=3, description="the number that may be used before additional charges are added, NULL means unlimited")
    public Integer getSoftLimit() {
        return softLimit;
    }

    @SchemaColumn(order=4, description="the maximum number that may be allocated, NULL means unlimited")
    public Integer getHardLimit() {
        return hardLimit;
    }

    /**
     * Gets the additional rate or <code>null</code> if there is none.
     */
    @SchemaColumn(order=5, description="the monthly rate for those past the soft_limit")
    public Money getAdditionalRate() {
        return additionalRate;
    }

    public static final MethodColumn COLUMN_ADDITIONAL_TRANSACTION_TYPE = getMethodColumn(PackageDefinitionLimit.class, "additionalTransactionType");
    @DependencySingleton
    @SchemaColumn(order=6, index=IndexType.INDEXED, description="the transaction type for those past the soft_limit")
    public TransactionType getAdditionalTransactionType() throws RemoteException {
        if(additionalTransactionType==null) return null;
        return getConnector().getTransactionTypes().get(additionalTransactionType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PackageDefinitionLimit(AOServConnector connector, com.aoindustries.aoserv.client.dto.PackageDefinitionLimit dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getPackageDefinition(),
            dto.getResourceType(),
            dto.getSoftLimit(),
            dto.getHardLimit(),
            getMoney(dto.getAdditionalRate()),
            dto.getAdditionalTransactionType()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PackageDefinitionLimit getDto() {
        return new com.aoindustries.aoserv.client.dto.PackageDefinitionLimit(
            key,
            packageDefinition,
            resourceType,
            softLimit,
            hardLimit,
            getDto(additionalRate),
            additionalTransactionType
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /**
     * Gets the soft limit and unit or <code>null</code> if there is none.
     */
    /* TODO
    public String getSoftLimitDisplayUnit() throws RemoteException {
        return softLimit==-1 ? null : getResourceType().getDisplayUnit(softLimit);
    }
     */
    /**
     * Gets the hard limit and unit or <code>null</code> if there is none.
     */
    /* TODO
    public String getHardLimitDisplayUnit() throws RemoteException {
        return hardLimit==-1 ? null : getResourceType().getDisplayUnit(hardLimit);
    }
     */

    /**
     * Gets the additional rate per unit or <code>null</code> if there is none.
     */
    /* TODO
    public String getAdditionalRatePerUnit() throws RemoteException {
        return additionalRate==-1 ? null : '$'+getResourceType().getPerUnit(BigDecimal.valueOf(additionalRate, 2));
    }
     */
    // </editor-fold>
}
