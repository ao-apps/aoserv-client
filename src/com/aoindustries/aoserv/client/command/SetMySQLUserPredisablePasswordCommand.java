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
final public class SetMySQLUserPredisablePasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_MYSQL_USER = "mysql_user",
        PARAM_ENCRYPTED_PASSWORD = "encrypted_password"
    ;

    final private int mysqlUser;
    final private String encryptedPassword;

    public SetMySQLUserPredisablePasswordCommand(
        @Param(name=PARAM_MYSQL_USER) int mysqlUser,
        @Param(name=PARAM_ENCRYPTED_PASSWORD) String encryptedPassword
    ) {
        this.mysqlUser = mysqlUser;
        this.encryptedPassword = encryptedPassword;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
