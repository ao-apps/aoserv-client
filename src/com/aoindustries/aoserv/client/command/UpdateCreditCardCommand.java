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
final public class UpdateCreditCardCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 9052612984035504932L;

    final private int pkey;
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
    final private String description;

    public UpdateCreditCardCommand(
        @Param(name="creditCard") CreditCard creditCard,
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
        @Param(name="description", nullable=true) String description
    ) {
        this.pkey = creditCard.getPkey();
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
        this.description = nullIfEmpty(description);
    }

    public int getPkey() {
        return pkey;
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

    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
