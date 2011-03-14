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
final public class AddTicketCommand extends RemoteCommand<Integer> {

    // TODO: private static final long serialVersionUID = 1L;

    final private AccountingCode brand;
    final private AccountingCode business;
    final private String language;
    final private Integer category;
    final private String ticketType;
    final private Email fromAddress;
    final private String summary;
    final private String details;
    final private String clientPriority;
    final private String contactEmails;
    final private String contactPhoneNumbers;

    public AddTicketCommand(
        @Param(name="brand") Brand brand,
        @Param(name="business", nullable=true) Business business,
        @Param(name="language") Language language,
        @Param(name="category", nullable=true) TicketCategory category,
        @Param(name="ticketType") TicketType ticketType,
        @Param(name="fromAddress", nullable=true) Email fromAddress,
        @Param(name="summary") String summary,
        @Param(name="details", nullable=true) String details,
        @Param(name="clientPriority") TicketPriority clientPriority,
        @Param(name="contactEmails") String contactEmails,
        @Param(name="contactPhoneNumbers") String contactPhoneNumbers
    ) {
        this.brand = brand.getKey();
        this.business = business==null ? null : business.getAccounting();
        this.language = language.getKey();
        this.category = category==null ? null : category.getKey();
        this.ticketType = ticketType.getKey();
        this.fromAddress = fromAddress;
        this.summary = summary;
        this.details = nullIfEmpty(details);
        this.clientPriority = clientPriority.getKey();
        this.contactEmails = contactEmails;
        this.contactPhoneNumbers = contactPhoneNumbers;
    }

    public AccountingCode getBrand() {
        return brand;
    }

    public AccountingCode getBusiness() {
        return business;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getCategory() {
        return category;
    }

    public String getTicketType() {
        return ticketType;
    }

    public Email getFromAddress() {
        return fromAddress;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetails() {
        return details;
    }

    public String getClientPriority() {
        return clientPriority;
    }

    public String getContactEmails() {
        return contactEmails;
    }

    public String getContactPhoneNumbers() {
        return contactPhoneNumbers;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
