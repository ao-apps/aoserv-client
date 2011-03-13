/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class AddCreditCardCommand extends RemoteCommand<Integer> {

    private static final long serialVersionUID = 1L;

    final private String processorId;
    final private AccountingCode accounting;
    final private String groupName;
    final private String cardInfo;
    final private String providerUniqueId;
    final private String firstName;
    final private String lastName;
    final private String companyName;
    final private Email email;
    final private String phone;
    final private String fax;
    final private String customerTaxId;
    final private String streetAddress1;
    final private String streetAddress2;
    final private String city;
    final private String state;
    final private String postalCode;
    final private String countryCode;
    final private String principalName;
    final private String description;
    final private String cardNumber;
    final private byte expirationMonth;
    final private short expirationYear;

    public AddCreditCardCommand(
        @Param(name="processor") CreditCardProcessor processor,
        @Param(name="business") Business business,
        @Param(name="groupName", nullable=true) String groupName,
        @Param(name="cardInfo") String cardInfo,
        @Param(name="providerUniqueId") String providerUniqueId,
        @Param(name="firstName") String firstName,
        @Param(name="lastName") String lastName,
        @Param(name="companyName", nullable=true) String companyName,
        @Param(name="email", nullable=true) Email email,
        @Param(name="phone", nullable=true) String phone,
        @Param(name="fax", nullable=true) String fax,
        @Param(name="customerTaxId", nullable=true) String customerTaxId,
        @Param(name="streetAddress1") String streetAddress1,
        @Param(name="streetAddress2", nullable=true) String streetAddress2,
        @Param(name="city") String city,
        @Param(name="state", nullable=true) String state,
        @Param(name="postalCode", nullable=true) String postalCode,
        @Param(name="countryCode") CountryCode countryCode,
        @Param(name="principalName", nullable=true) String principalName,
        @Param(name="description", nullable=true) String description,
        @Param(name="cardNumber") String cardNumber,
        @Param(name="expirationMonth") byte expirationMonth,
        @Param(name="expirationYear") short expirationYear
    ) {
        this.processorId = processor.getProviderId();
        this.accounting = business.getAccounting();
        this.groupName = nullIfEmpty(groupName);
        this.cardInfo = cardInfo;
        this.providerUniqueId = providerUniqueId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = nullIfEmpty(companyName);
        this.email = email;
        this.phone = nullIfEmpty(phone);
        this.fax = nullIfEmpty(fax);
        this.customerTaxId = nullIfEmpty(customerTaxId);
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = nullIfEmpty(streetAddress2);
        this.city = city;
        this.state = nullIfEmpty(state);
        this.postalCode = nullIfEmpty(postalCode);
        this.countryCode = countryCode.getCode();
        this.principalName = nullIfEmpty(principalName);
        this.description = nullIfEmpty(description);
        this.cardNumber = cardNumber;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
    }

    public String getProcessorId() {
        return processorId;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public String getProviderUniqueId() {
        return providerUniqueId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getCustomerTaxId() {
        return customerTaxId;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getDescription() {
        return description;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public byte getExpirationMonth() {
        return expirationMonth;
    }

    public short getExpirationYear() {
        return expirationYear;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
