/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.util.StringUtility;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Cached for various AOServConnectorFactory implementations.  Not intended for direct use.
 * This class is not thread safe and must be externally synchronized.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServConnectorFactoryCache<C extends AOServConnector> implements Iterable<C> {

    static class CacheKey {

        final Locale locale;
        final UserId username;
        final String password;
        final UserId switchUser;
        final DomainName daemonServer;
        final boolean readOnly;

        CacheKey(
            Locale locale,
            UserId username,
            String password,
            UserId switchUser,
            DomainName daemonServer,
            boolean readOnly
        ) {
            this.locale = locale;
            this.username = username.intern();
            this.password = password.intern();
            this.switchUser = switchUser.intern();
            this.daemonServer = daemonServer==null ? null : daemonServer.intern();
            this.readOnly = readOnly;
        }

        @Override
        public boolean equals(Object o) {
            if(o==null || !(o instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)o;
            return
                locale.equals(other.locale)
                && username==other.username // interned - OK
                && password==other.password // interned - OK
                && switchUser==switchUser   // interned - OK
                && StringUtility.equals(daemonServer, other.daemonServer)
                && readOnly==other.readOnly
            ;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + locale.hashCode();
            hash = 29 * hash + username.hashCode();
            hash = 29 * hash + password.hashCode();
            hash = 29 * hash + switchUser.hashCode();
            hash = 29 * hash + (daemonServer!=null ? daemonServer.hashCode() : 0);
            hash ^= Boolean.valueOf(readOnly).hashCode();
            return hash;
        }
    }

    private final Map<CacheKey,C> connectors = new HashMap<CacheKey,C>();

    public C get(
        Locale locale,
        UserId username,
        String password,
        UserId switchUser,
        DomainName daemonServer,
        boolean readOnly
    ) {
        return connectors.get(new CacheKey(locale, username, password, switchUser, daemonServer, readOnly));
    }

    public void put(
        Locale locale,
        UserId username,
        String password,
        UserId switchUser,
        DomainName daemonServer,
        boolean readOnly,
        C connector
    ) {
        connectors.put(new CacheKey(locale, username, password, switchUser, daemonServer, readOnly), connector);
    }

    @Override
    public Iterator<C> iterator() {
        return connectors.values().iterator();
    }
}
