package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Email;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class AddTicketCommand extends AOServCommand<Integer> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_BRAND = "brand",
        PARAM_BUSINESS = "business",
        PARAM_LANGUAGE = "language",
        PARAM_CATEGORY = "category",
        PARAM_TICKET_TYPE = "ticket_type",
        PARAM_FROM_ADDRESS = "from_address",
        PARAM_SUMMARY = "summary",
        PARAM_DETAILS = "details",
        PARAM_CLIENT_PRIORITY = "client_priority",
        PARAM_CONTACT_EMAILS = "contact_emails",
        PARAM_CONTACT_PHONE_NUMBERS = "contact_phone_numbers"
    ;

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
        @Param(name=PARAM_BRAND) AccountingCode brand,
        @Param(name=PARAM_BUSINESS, nullable=true) AccountingCode business,
        @Param(name=PARAM_LANGUAGE) String language,
        @Param(name=PARAM_CATEGORY, nullable=true) Integer category,
        @Param(name=PARAM_TICKET_TYPE) String ticketType,
        @Param(name=PARAM_FROM_ADDRESS, nullable=true) Email fromAddress,
        @Param(name=PARAM_SUMMARY) String summary,
        @Param(name=PARAM_DETAILS, nullable=true) String details,
        @Param(name=PARAM_CLIENT_PRIORITY) String clientPriority,
        @Param(name=PARAM_CONTACT_EMAILS) String contactEmails,
        @Param(name=PARAM_CONTACT_PHONE_NUMBERS) String contactPhoneNumbers
    ) {
        this.brand = brand;
        this.business = business;
        this.language = language;
        this.category = category;
        this.ticketType = ticketType;
        this.fromAddress = fromAddress;
        this.summary = summary;
        this.details = details;
        this.clientPriority = clientPriority;
        this.contactEmails = contactEmails;
        this.contactPhoneNumbers = contactPhoneNumbers;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
