/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contact information associated with a <code>Business</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessProfile extends AOServObjectIntegerKey implements Comparable<BusinessProfile>, DtoFactory<com.aoindustries.aoserv.client.dto.BusinessProfile> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -3118229294208613743L;

    private AccountingCode accounting;
    final private int priority;
    final private String name;
    final private boolean isPrivate;
    final private String phone;
    final private String fax;
    final private String address1;
    final private String address2;
    final private String city;
    final private String state;
    private String country;
    final private String zip;
    final private boolean sendInvoice;
    final private long created;
    final private String billingContact;
    final private String billingEmail;
    final private String technicalContact;
    final private String technicalEmail;

    public BusinessProfile(
        AOServConnector connector,
        int pkey,
        AccountingCode accounting,
        int priority,
        String name,
        boolean isPrivate,
        String phone,
        String fax,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        boolean sendInvoice,
        long created,
        String billingContact,
        String billingEmail,
        String technicalContact,
        String technicalEmail
    ) {
        super(connector, pkey);
        this.accounting = accounting;
        this.priority = priority;
        this.name = name;
        this.isPrivate = isPrivate;
        this.phone = phone;
        this.fax = fax;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.sendInvoice = sendInvoice;
        this.created = created;
        this.billingContact = billingContact;
        this.billingEmail = billingEmail;
        this.technicalContact = technicalContact;
        this.technicalEmail = technicalEmail;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
        country = intern(country);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BusinessProfile other) {
        int diff = accounting.compareTo(other.accounting);
        if(diff!=0) return diff;
        return -compare(priority, other.priority);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a unique primary key")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(BusinessProfile.class, "business");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the accounting code of the business")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=2, description="the highest priority profile is used")
    public int getPriority() {
        return priority;
    }

    @SchemaColumn(order=3, description="the name of the business")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=4, description="indicates if the business should not be listed in publicly available lists")
    public boolean isPrivate() {
        return isPrivate;
    }

    @SchemaColumn(order=5, description="the phone number")
    public String getPhone() {
        return phone;
    }

    @SchemaColumn(order=6, description="the fax number")
    public String getFax() {
        return fax;
    }

    @SchemaColumn(order=7, description="the street address of the business")
    public String getAddress1() {
        return address1;
    }

    @SchemaColumn(order=8, description="the street address of the business")
    public String getAddress2() {
        return address2;
    }

    @SchemaColumn(order=9, description="the city")
    public String getCity() {
        return city;
    }

    @SchemaColumn(order=10, description="the state or providence")
    public String getState() {
        return state;
    }

    public static final MethodColumn COLUMN_COUNTRY = getMethodColumn(BusinessProfile.class, "country");
    @DependencySingleton
    @SchemaColumn(order=11, index=IndexType.INDEXED, description="the two-character country code")
    public CountryCode getCountry() throws RemoteException {
        return getConnector().getCountryCodes().get(country);
    }

    @SchemaColumn(order=12, description="the zip code")
    public String getZip() {
        return zip;
    }

    @SchemaColumn(order=13, description="indicates to send a monthly invoice via regular mail")
    public boolean getSendInvoice() {
    	return sendInvoice;
    }

    @SchemaColumn(order=14, description="the time this entry was created")
    public Timestamp getCreated() {
        return new Timestamp(created);
    }

    @SchemaColumn(order=15, description="the name to contact for billing information")
    public String getBillingContact() {
        return billingContact;
    }

    /**
     * Gets an unmodifiable list of the emails.
     */
    private static List<Email> getEmailList(String emails) throws ValidationException {
        List<String> strings = StringUtility.splitStringCommaSpace(emails);
        int size = strings.size();
        if(size==0) return Collections.emptyList();
        if(size==1) return Collections.singletonList(Email.valueOf(strings.get(0)));
        List<Email> results = new ArrayList<Email>(size);
        for(String string : strings) results.add(Email.valueOf(string));
        return Collections.unmodifiableList(results);
    }

    @SchemaColumn(order=16, description="the email address to notify for billing")
    public List<Email> getBillingEmail() throws ValidationException {
        return getEmailList(billingEmail);
    }

    @SchemaColumn(order=17, description="the name to contact for technical information")
    public String getTechnicalContact() {
        return technicalContact;
    }

    @SchemaColumn(order=18, description="the email address to notify for technical")
    public List<Email> getTechnicalEmail() throws ValidationException {
        return getEmailList(technicalEmail);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BusinessProfile(AOServConnector connector, com.aoindustries.aoserv.client.dto.BusinessProfile dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            getAccountingCode(dto.getAccounting()),
            dto.getPriority(),
            dto.getName(),
            dto.isIsPrivate(),
            dto.getPhone(),
            dto.getFax(),
            dto.getAddress1(),
            dto.getAddress2(),
            dto.getCity(),
            dto.getState(),
            dto.getCountry(),
            dto.getZip(),
            dto.isSendInvoice(),
            getTimeMillis(dto.getCreated()),
            dto.getBillingContact(),
            dto.getBillingEmail(),
            dto.getTechnicalContact(),
            dto.getTechnicalEmail()
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.BusinessProfile getDto() {
        return new com.aoindustries.aoserv.client.dto.BusinessProfile(getKeyInt(), getDto(accounting), priority, name, isPrivate, phone, fax, address1, address2, city, state, country, zip, sendInvoice, created, billingContact, billingEmail, technicalContact, technicalEmail);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return accounting + " (" + priority + ')';
    }
    // </editor-fold>
}