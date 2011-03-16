/*
 * Copyright 2007-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>CreditCardProcessor</code> represents on Merchant account used for credit card processing.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessor extends AOServObjectStringKey implements Comparable<CreditCardProcessor>, DtoFactory<com.aoindustries.aoserv.client.dto.CreditCardProcessor> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 1735379036885171107L;

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
        AOServConnector connector,
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
        super(connector, providerId);
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
            int diff = accounting==other.accounting ? 0 : compare(getBusiness(), other.getBusiness()); // OK - interned
            if(diff!=0) return diff;
            return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique ID of this processor")
    public String getProviderId() {
        return getKey();
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(CreditCardProcessor.class, "business");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the accounting code of the business owning the merchant account")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=2, description="the classname of the Java code that connects to the merchant services provider")
    public String getClassName() {
        return className;
    }

    @SchemaColumn(order=3, description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam1() {
        return param1;
    }

    @SchemaColumn(order=4, description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam2() {
        return param2;
    }

    @SchemaColumn(order=5, description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam3() {
        return param3;
    }

    @SchemaColumn(order=6, description="the optional parameters for the Java code that connects to the merchant services provider")
    public String getParam4() {
        return param4;
    }

    @SchemaColumn(order=7, description="the enabled flag")
    public boolean isEnabled() {
        return enabled;
    }

    @SchemaColumn(order=8, description="the weight used for multi-processor weighted transaction distribution")
    public int getWeight() {
        return weight;
    }

    @SchemaColumn(order=9, description="an optional description of the processor")
    public String getDescription() {
        return description;
    }

    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    /* TODO
    @SchemaColumn(order=10, description="the from that will be used for encryption")
    public EncryptionKey getEncryptionFrom() throws SQLException, IOException {
        if(encryptionFrom==-1) return null;
        return getConnector().getEncryptionKeys().get(encryptionFrom);
    }
     */

    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    /* TODO
    @SchemaColumn(order=11, description="the recipient that will be used for encryption")
    public EncryptionKey getEncryptionRecipient() throws SQLException, IOException {
        if(encryptionRecipient==-1) return null;
        return getConnector().getEncryptionKeys().get(encryptionRecipient);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public CreditCardProcessor(AOServConnector connector, com.aoindustries.aoserv.client.dto.CreditCardProcessor dto) throws ValidationException {
        this(
            connector,
            dto.getProviderId(),
            getAccountingCode(dto.getAccounting()),
            dto.getClassName(),
            dto.getParam1(),
            dto.getParam2(),
            dto.getParam3(),
            dto.getParam4(),
            dto.isEnabled(),
            dto.getWeight(),
            dto.getDescription(),
            dto.getEncryptionFrom(),
            dto.getEncryptionRecipient()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.CreditCardProcessor getDto() {
        return new com.aoindustries.aoserv.client.dto.CreditCardProcessor(getKey(), getDto(accounting), className, param1, param2, param3, param4, enabled, weight, description, encryptionFrom, encryptionRecipient);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<CreditCard> getCreditCards() throws RemoteException {
        return getConnector().getCreditCards().filterIndexed(CreditCard.COLUMN_PROCESSOR, this);
    }

    @DependentObjectSet
    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getConnector().getTransactions().filterIndexed(Transaction.COLUMN_PROCESSOR, this);
    }

    @DependentObjectSet
    public IndexedSet<BankTransaction> getBankTransactions() throws RemoteException {
        return getConnector().getBankTransactions().filterIndexed(BankTransaction.COLUMN_PROCESSOR, this);
    }

    @DependentObjectSet
    public IndexedSet<CreditCardTransaction> getCreditCardTransactions() throws RemoteException {
        return getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_PROCESSOR, this);
    }
    // </editor-fold>
}
