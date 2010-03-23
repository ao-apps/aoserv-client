package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.MySQLUser;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetMySQLUserPasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_MYSQL_USER = "mysql_user",
        PARAM_PLAINTEXT = "plaintext"
    ;

    final private int mysqlUser;
    final private String plaintext;

    public SetMySQLUserPasswordCommand(
        @Param(name=PARAM_MYSQL_USER) int mysqlUser,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.mysqlUser = mysqlUser;
        this.plaintext = plaintext;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        MySQLUser mu = connectedUser.getService().getConnector().getMysqlUsers().get(mysqlUser);
        if(mu.getAoServerResource().getResource().getDisableLog()!=null) errors = addValidationError(errors, PARAM_MYSQL_USER, locale, "SetMySQLUserPasswordCommand.validate.disabled");
        if(mu.getUsername().getUsername().equals(MySQLUser.ROOT.getUserId())) errors = addValidationError(errors, PARAM_MYSQL_USER, locale, "SetMySQLUserPasswordCommand.validate.noSetRoot");
        // TODO: Check password strength
        return errors;
    }
}
