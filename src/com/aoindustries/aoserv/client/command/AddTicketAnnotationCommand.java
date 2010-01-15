package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class AddTicketAnnotationCommand extends AOServCommand<Integer> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_TICKET = "ticket",
        PARAM_SUMMARY = "summary",
        PARAM_DETAILS = "details"
    ;

    final private int ticket;
    final private String summary;
    final private String details;

    public AddTicketAnnotationCommand(
        @Param(name=PARAM_TICKET) int ticket,
        @Param(name=PARAM_SUMMARY) String summary,
        @Param(name=PARAM_DETAILS, nullable=true) String details
    ) {
        this.ticket = ticket;
        this.summary = summary;
        this.details = details;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
