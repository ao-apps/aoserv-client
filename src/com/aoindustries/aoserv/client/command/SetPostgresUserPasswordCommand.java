package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.PostgresUser;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetPostgresUserPasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_POSTGRES_USER = "postgres_user",
        PARAM_PLAINTEXT = "plaintext"
    ;

    final private int postgresUser;
    final private String plaintext;

    public SetPostgresUserPasswordCommand(
        @Param(name=PARAM_POSTGRES_USER) int postgresUser,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.postgresUser = postgresUser;
        this.plaintext = plaintext;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        PostgresUser pu = connectedUser.getService().getConnector().getPostgresUsers().get(postgresUser);
        if(pu.getAoServerResource().getResource().getDisableLog()!=null) errors = addValidationError(errors, PARAM_POSTGRES_USER, locale, "SetPostgresUserPasswordCommand.validate.disabled");
        if(pu.getUsername().getUsername().equals(PostgresUser.POSTGRES.getUserId())) errors = addValidationError(errors, PARAM_POSTGRES_USER, locale, "SetPostgresUserPasswordCommand.validate.noSetPostgres");
        // TODO: Check password strength
        return errors;
    }
}
