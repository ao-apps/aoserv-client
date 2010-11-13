/*
 * Copyright 2007-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>CreditCardProcessor</code> represents on Merchant account used for credit card processing.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessor extends AOServObjectStringKey<CreditCardProcessor> implements Comparable<CreditCardProcessor>, DtoFactory<com.aoindustries.aoserv.client.dto.CreditCardProcessor> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode accounting;
    private String className;
    final private String param1;
    final private String param2;
    final private String param3;
    final private String param4;
    final private boolean enabled;
    final private int weight;
    final private String description;
    final private Integer encryptionFrom;
    final private Integer encryptionRecipient;

    public CreditCardProcessor(
        CreditCardProcessorService<?,?> service,
        String providerId,
        AccountingCode accounting,
        String className,
        String param1,
        String param2,
        String param3,
        String param4,
        boolean enabled,
        int weight,
        String description,
        Integer encryptionFrom,
        Integer encryptionRecipient
    ) {
        super(service, providerId);
        this.accounting = accounting;
        this.className = className;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.enabled = enabled;
        this.weight = weight;
        this.description = description;
        this.encryptionFrom = encryptionFrom;
        this.encryptionRecipient = encryptionRecipient;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
        className = intern(className);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(CreditCardProcessor other) {
        try {
            int diff = accounting==other.accounting ? 0 : AOServObjectUtils.compare(getBusiness(), other.getBusiness()); // OK - interned
            if(diff!=0) return diff;
            return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="provider_id", index=IndexType.PRIMARY_KEY, description="the unique ID of this processor")
    public String getProviderId() {
        return getKey();
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the accounting code of the business owning the merchant account")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=2, name="class_name", description="the classname of the Java code that connects to the merchant services provider")
    public String getClassName() {
        return className;
    }

    @SchemaColumn(order=3, name="param1", description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam1() {
        return param1;
    }

    @SchemaColumn(order=4, name="param2", description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam2() {
        return param2;
    }

    @SchemaColumn(order=5, name="param3", description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam3() {
        return param3;
    }

    @SchemaColumn(order=6, name="param4", description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam4() {
        return param4;
    }

    @SchemaColumn(order=7, name="enabled", description="the enabled flag")
    public boolean getEnabled() {
        return enabled;
    }

    @SchemaColumn(order=8, name="weight", description="the weight used for multi-processor weighted transaction distribution")
    public int getWeight() {
        return weight;
    }

    @SchemaColumn(order=9, name="description", description="an optional description of the processor")
    public String getDescription() {
        return description;
    }

    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    /* TODO
    @SchemaColumn(order=10, name="encryption_from", description="the from that will be used for encryption")
    public EncryptionKey getEncryptionFrom() throws SQLException, IOException {
        if(encryptionFrom==-1) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionFrom);
    }
     */

    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    /* TODO
    @SchemaColumn(order=11, name="encryption_recipient", description="the recipient that will be used for encryption")
    public EncryptionKey getEncryptionRecipient() throws SQLException, IOException {
        if(encryptionRecipient==-1) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionRecipient);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.CreditCardProcessor getDto() {
        return new com.aoindustries.aoserv.client.dto.CreditCardProcessor(getKey(), getDto(accounting), className, param1, param2, param3, param4, enabled, weight, description, encryptionFrom, encryptionRecipient);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getBankTransactions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCards());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransactions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTransactions());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<CreditCard> getCreditCards() throws RemoteException {
        return getService().getConnector().getCreditCards().filterIndexed(CreditCard.COLUMN_PROCESSOR_ID, this);
    }

    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getService().getConnector().getTransactions().filterIndexed(Transaction.COLUMN_PROCESSOR, this);
    }
    /* TODO
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getService().getConnector().getBankTransactions().getIndexedRows(BankTransaction.COLUMN_PROCESSOR, pkey);
    }
    */
    public IndexedSet<CreditCardTransaction> getCreditCardTransactions() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_PROCESSOR_ID, this);
    }
    // </editor-fold>
}
