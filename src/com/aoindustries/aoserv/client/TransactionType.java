/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * A <code>TransactionType</code> is one type that may be used
 * in a <code>Transaction</code>.  Each <code>PackageDefinition</code>
 * and <code>PackageDefinitionLimit</code> defines which type will be
 * used for billing.
 *
 * @see  PackageDefinition
 * @see  PackageDefinitionLimit
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
final public class TransactionType extends AOServObjectStringKey<TransactionType> implements BeanFactory<com.aoindustries.aoserv.client.beans.TransactionType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        HTTPD="httpd",
        PAYMENT="payment",
        VIRTUAL="virtual"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    /**
     * If <code>true</code> this <code>TransactionType</code> represents a credit to
     * an account and will be listed in payments received reports.
     */
    final private boolean isCredit;

    public TransactionType(TransactionTypeService<?,?> service, String name, boolean isCredit) {
        super(service, name);
        this.isCredit = isCredit;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of this transaction type")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, name="is_credit", description="indicates that this type of transaction represents payment or credit")
    public boolean isCredit() {
        return isCredit;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.TransactionType getBean() {
        return new com.aoindustries.aoserv.client.beans.TransactionType(getKey(), isCredit);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTransactions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPackageDefinitionsBySetupFeeTransactionType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPackageDefinitionsByMonthlyRateTransactionType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPackageDefinitionLimits());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("TransactionType."+getKey()+".description");
    }

    public String getUnit() {
        return ApplicationResources.accessor.getMessage("TransactionType."+getKey()+".unit");
    }

    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("TransactionType."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getService().getConnector().getTransactions().filterIndexed(Transaction.COLUMN_TYPE, this);
    }

    public IndexedSet<PackageDefinition> getPackageDefinitionsBySetupFeeTransactionType() throws RemoteException {
        return getService().getConnector().getPackageDefinitions().filterIndexed(PackageDefinition.COLUMN_SETUP_FEE_TRANSACTION_TYPE, this);
    }

    public IndexedSet<PackageDefinition> getPackageDefinitionsByMonthlyRateTransactionType() throws RemoteException {
        return getService().getConnector().getPackageDefinitions().filterIndexed(PackageDefinition.COLUMN_MONTHLY_RATE_TRANSACTION_TYPE, this);
    }

    public IndexedSet<PackageDefinitionLimit> getPackageDefinitionLimits() throws RemoteException {
        return getService().getConnector().getPackageDefinitionLimits().filterIndexed(PackageDefinitionLimit.COLUMN_ADDITIONAL_TRANSACTION_TYPE, this);
    }
    // </editor-fold>
}
