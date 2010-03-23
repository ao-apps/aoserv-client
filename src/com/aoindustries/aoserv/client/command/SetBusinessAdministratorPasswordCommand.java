package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.UserId;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetBusinessAdministratorPasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_USERNAME = "username",
        PARAM_PLAINTEXT = "plaintext"
    ;

    final private UserId username;
    final private String plaintext;

    public SetBusinessAdministratorPasswordCommand(
        @Param(name=PARAM_USERNAME) UserId username,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.username = username;
        this.plaintext = plaintext;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        if(connectedUser.getService().get(username).getDisableLog()!=null) errors = addValidationError(errors, PARAM_USERNAME, locale, "SetBusinessAdministratorPasswordCommand.validate.disabled");
        // TODO: Check password strength
        return errors;
    }
}
