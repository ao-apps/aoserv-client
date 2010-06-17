package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>CountryCode</code> is a simple wrapper for country
 * code and name mappings.  Each code is a two-digit ISO 3166-1 alpha-2 country
 * code.
 *
 * See <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</a>
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCode extends AOServObjectStringKey<CountryCode> implements BeanFactory<com.aoindustries.aoserv.client.beans.CountryCode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * <code>CountryCode</code>s used as constants.
     */
    public static final String US="US";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String name;
    final private boolean chargeComSupported;
    private String chargeComName;

    public CountryCode(
        CountryCodeService<?,?> table,
        String code,
        String name,
        boolean chargeComSupported,
        String chargeComName
    ) {
        super(table, code);
        this.name = name;
        this.chargeComSupported = chargeComSupported;
        this.chargeComName = chargeComName;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
        chargeComName = intern(chargeComName);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(CountryCode other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the two-character unique code for this country.
     */
    @SchemaColumn(order=0, name="code", index=IndexType.PRIMARY_KEY, description="the two-character code for the country")
    public String getCode() {
        return getKey();
    }

    @SchemaColumn(order=1, name="name", description="the name of the country")
    public String getName() {
    	return name;
    }

    @SchemaColumn(order=2, name="charge_com_supported", description="if the country is supported by Charge.Com")
    public boolean getChargeComSupported() {
        return chargeComSupported;
    }

    @SchemaColumn(order=3, name="charge_com_name", description="the Charge.Com specific name")
    public String getChargeComName() {
    	return chargeComName==null?name:chargeComName;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.CountryCode getBean() {
        return new com.aoindustries.aoserv.client.beans.CountryCode(getKey(), name, chargeComSupported, chargeComName);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusinessAdministrators(),
            getBusinessProfiles(),
            getCreditCards(),
            getCreditCardTransactionsByShippingCountryCode(),
            getCreditCardTransactionsByCreditCardCountryCode()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<BusinessAdministrator> getBusinessAdministrators() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().filterIndexed(BusinessAdministrator.COLUMN_COUNTRY, this);
    }

    public IndexedSet<BusinessProfile> getBusinessProfiles() throws RemoteException {
        return getService().getConnector().getBusinessProfiles().filterIndexed(BusinessProfile.COLUMN_COUNTRY, this);
    }

    public IndexedSet<CreditCard> getCreditCards() throws RemoteException {
        return getService().getConnector().getCreditCards().filterIndexed(CreditCard.COLUMN_COUNTRY_CODE, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByShippingCountryCode() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_SHIPPING_COUNTRY_CODE, this);
    }

    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByCreditCardCountryCode() throws RemoteException {
        return getService().getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_CREDIT_CARD_COUNTRY_CODE, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return name;
    }
    // </editor-fold>
}
