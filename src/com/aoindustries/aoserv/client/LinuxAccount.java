package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccount {

    /**
     * Some commonly used system and application account usernames.
     */
    public static final String
        APACHE="apache",
        AWSTATS="awstats",
        BIN="bin",
        CYRUS="cyrus",
        EMAILMON="emailmon",
        FTP="ftp",
        FTPMON="ftpmon",
        INTERBASE="interbase",
        MAIL="mail",
        NOBODY="nobody",
        OPERATOR="operator",
        POSTGRES="postgres",
        ROOT="root"
    ;

    private LinuxAccount() {
    }
}
