/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>PackageDefinitionLimit</code> stores the per-resource type limit and rates that are part of a <code>PackageDefinition</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class PackageDefinitionLimit extends AOServObjectIntegerKey<PackageDefinitionLimit> implements BeanFactory<com.aoindustries.aoserv.client.beans.PackageDefinitionLimit> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int packageDefinition;
    private String resourceType;
    final private Integer softLimit;
    final private Integer hardLimit;
    final private Money additionalRate;
    private String additionalTransactionType;

    public PackageDefinitionLimit(
        PackageDefinitionLimitService<?,?> service,
        int pkey,
        int packageDefinition,
        String resourceType,
        Integer softLimit,
        Integer hardLimit,
        Money additionalRate,
        String additionalTransactionType
    ) {
        super(service, pkey);
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
    protected int compareToImpl(PackageDefinitionLimit other) throws RemoteException {
        int diff = packageDefinition==other.packageDefinition ? 0 : getPackageDefinition().compareToImpl(other.getPackageDefinition());
        if(diff!=0) return diff;
        return resourceType==other.resourceType ? 0 : getResourceType().compareToImpl(other.getResourceType()); // OK - interned
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="the unique identifier for this limit")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_PACKAGE_DEFINITION = "package_definition";
    @SchemaColumn(order=1, name=COLUMN_PACKAGE_DEFINITION, index=IndexType.INDEXED, description="the pkey of the package definition")
    public PackageDefinition getPackageDefinition() throws RemoteException {
        return getService().getConnector().getPackageDefinitions().get(packageDefinition);
    }

    static final String COLUMN_RESOURCE_TYPE = "resource_type";
    @SchemaColumn(order=2, name=COLUMN_RESOURCE_TYPE, index=IndexType.INDEXED, description="the resource type")
    public ResourceType getResourceType() throws RemoteException {
        return getService().getConnector().getResourceTypes().get(resourceType);
    }

    @SchemaColumn(order=3, name="soft_limit", description="the number that may be used before additional charges are added, NULL means unlimited")
    public Integer getSoftLimit() {
        return softLimit;
    }

    @SchemaColumn(order=4, name="hard_limit", description="the maximum number that may be allocated, NULL means unlimited")
    public Integer getHardLimit() {
        return hardLimit;
    }

    /**
     * Gets the additional rate or <code>null</code> if there is none.
     */
    @SchemaColumn(order=5, name="additional_rate", description="the monthly rate for those past the soft_limit")
    public Money getAdditionalRate() {
        return additionalRate;
    }

    static final String COLUMN_ADDITIONAL_TRANSACTION_TYPE = "additional_transaction_type";
    @SchemaColumn(order=6, name=COLUMN_ADDITIONAL_TRANSACTION_TYPE, index=IndexType.INDEXED, description="the transaction type for those past the soft_limit")
    public TransactionType getAdditionalTransactionType() throws RemoteException {
        if(additionalTransactionType==null) return null;
        return getService().getConnector().getTransactionTypes().get(additionalTransactionType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.PackageDefinitionLimit getBean() {
        return new com.aoindustries.aoserv.client.beans.PackageDefinitionLimit(
            key,
            packageDefinition,
            resourceType,
            softLimit,
            hardLimit,
            getBean(additionalRate),
            additionalTransactionType
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getPackageDefinition(),
            getResourceType(),
            getAdditionalTransactionType()
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
