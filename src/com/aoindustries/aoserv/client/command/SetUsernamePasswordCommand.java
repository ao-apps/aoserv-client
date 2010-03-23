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
final public class SetUsernamePasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_USERNAME = "username",
        PARAM_PLAINTEXT = "plaintext"
    ;

    final private UserId username;
    final private String plaintext;

    public SetUsernamePasswordCommand(
        @Param(name=PARAM_USERNAME) UserId username,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.username = username;
        this.plaintext = plaintext;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO: Check LinuxAccountType.Constant for no password set allowed
        // TODO
        /*
        if(disableLog!=null) return false;

        BusinessAdministrator ba=getBusinessAdministrator();
    	if(ba!=null && !ba.canSetPassword()) return false;

        IndexedSet<LinuxAccount> las = getLinuxAccounts();
        for(LinuxAccount la : las) if(!la.canSetPassword()) return false;

        IndexedSet<MySQLUser> mus = getMysqlUsers();
    	for(MySQLUser mu : mus) if(!mu.canSetPassword()) return false;

        IndexedSet<PostgresUser> pus = getPostgresUser();
        for(PostgresUser pu : pus) if(!pu.canSetPassword()) return false;

        return ba!=null || !las.isEmpty() || !mus.isEmpty() || !pus.isEmpty();
         */
        return Collections.emptyMap();
    }
}
