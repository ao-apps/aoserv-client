/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class CreditCard {

    private int pkey;
    private String processorId;
    private AccountingCode accounting;
    private String groupName;
    private String cardInfo;
    private String providerUniqueId;
    private String firstName;
    private String lastName;
    private String companyName;
    private Email email;
    private String phone;
    private String fax;
    private String customerTaxId;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private Date created;
    private UserId createdBy;
    private String principalName;
    private boolean useMonthly;
    private boolean active;
    private Date deactivatedOn;
    private String deactivateReason;
    private String description;
    private String encryptedCardNumber;
    private Integer encryptionCardNumberFrom;
    private Integer encryptionCardNumberRecipient;
    private String encryptedExpiration;
    private Integer encryptionExpirationFrom;
    private Integer encryptionExpirationRecipient;

    public CreditCard() {
    }

    public CreditCard(
        int pkey,
        String processorId,
        AccountingCode accounting,
        String groupName,
        String cardInfo,
        String providerUniqueId,
        String firstName,
        String lastName,
        String companyName,
        Email email,
        String phone,
        String fax,
        String customerTaxId,
        String streetAddress1,
        String streetAddress2,
        String city,
        String state,
        String postalCode,
        String countryCode,
        Date created,
        UserId createdBy,
        String principalName,
        boolean useMonthly,
        boolean active,
        Date deactivatedOn,
        String deactivateReason,
        String description,
        String encrypted_card_number,
        Integer encryption_card_number_from,
        Integer encryption_card_number_recipient,
        String encrypted_expiration,
        Integer encryption_expiration_from,
        Integer encryption_expiration_recipient
    ) {
        this.pkey = pkey;
        this.processorId = processorId;
        this.accounting = accounting;
        this.groupName = groupName;
        this.cardInfo = cardInfo;
        this.providerUniqueId = providerUniqueId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.customerTaxId = customerTaxId;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.created= created;
        this.createdBy = createdBy;
        this.principalName = principalName;
        this.useMonthly = useMonthly;
        this.active = active;
        this.deactivatedOn = deactivatedOn;
        this.deactivateReason = deactivateReason;
        this.description = description;
        this.encryptedCardNumber = encrypted_card_number;
        this.encryptionCardNumberFrom = encryption_card_number_from;
        this.encryptionCardNumberRecipient = encryption_card_number_recipient;
        this.encryptedExpiration = encrypted_expiration;
        this.encryptionExpirationFrom = encryption_expiration_from;
        this.encryptionExpirationRecipient = encryption_expiration_recipient;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(String cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getProviderUniqueId() {
        return providerUniqueId;
    }

    public void setProviderUniqueId(String providerUniqueId) {
        this.providerUniqueId = providerUniqueId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCustomerTaxId() {
        return customerTaxId;
    }

    public void setCustomerTaxId(String customerTaxId) {
        this.customerTaxId = customerTaxId;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserId createdBy) {
        this.createdBy = createdBy;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public boolean isUseMonthly() {
        return useMonthly;
    }

    public void setUseMonthly(boolean useMonthly) {
        this.useMonthly = useMonthly;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDeactivatedOn() {
        return deactivatedOn;
    }

    public void setDeactivatedOn(Date deactivatedOn) {
        this.deactivatedOn = deactivatedOn;
    }

    public String getDeactivateReason() {
        return deactivateReason;
    }

    public void setDeactivateReason(String deactivateReason) {
        this.deactivateReason = deactivateReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
    }

    public Integer getEncryptionCardNumberFrom() {
        return encryptionCardNumberFrom;
    }

    public void setEncryptionCardNumberFrom(Integer encryptionCardNumberFrom) {
        this.encryptionCardNumberFrom = encryptionCardNumberFrom;
    }

    public Integer getEncryptionCardNumberRecipient() {
        return encryptionCardNumberRecipient;
    }

    public void setEncryptionCardNumberRecipient(Integer encryptionCardNumberRecipient) {
        this.encryptionCardNumberRecipient = encryptionCardNumberRecipient;
    }

    public String getEncryptedExpiration() {
        return encryptedExpiration;
    }

    public void setEncryptedExpiration(String encryptedExpiration) {
        this.encryptedExpiration = encryptedExpiration;
    }

    public Integer getEncryptionExpirationFrom() {
        return encryptionExpirationFrom;
    }

    public void setEncryptionExpirationFrom(Integer encryptionExpirationFrom) {
        this.encryptionExpirationFrom = encryptionExpirationFrom;
    }

    public Integer getEncryptionExpirationRecipient() {
        return encryptionExpirationRecipient;
    }

    public void setEncryptionExpirationRecipient(Integer encryptionExpirationRecipient) {
        this.encryptionExpirationRecipient = encryptionExpirationRecipient;
    }
}
