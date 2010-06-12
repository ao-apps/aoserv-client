package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import java.util.HashMap;
import java.util.Map;

/**
 * Cached for various AOServConnectorFactory implementations.  Not intended for direct use.
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServConnectorFactoryCache<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> {

    static class CacheKey {

        final UserId connectAs;
        final UserId authenticateAs;
        final String password;
        final DomainName daemonServer;
        final boolean readOnly;

        CacheKey(
            UserId connectAs,
            UserId authenticateAs,
            String password,
            DomainName daemonServer,
            boolean readOnly
        ) {
            this.connectAs = connectAs.intern();
            this.authenticateAs = authenticateAs.intern();
            this.password = password.intern();
            this.daemonServer = daemonServer==null ? null : daemonServer.intern();
            this.readOnly = readOnly;
        }

        @Override
        public boolean equals(Object o) {
            if(o==null || !(o instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)o;
            return
                connectAs==connectAs
                && authenticateAs==other.authenticateAs
                && password==other.password // interned - OK
                && daemonServer==other.daemonServer // interned - OK
                && readOnly==other.readOnly
            ;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + connectAs.hashCode();
            hash = 29 * hash + authenticateAs.hashCode();
            hash = 29 * hash + password.hashCode();
            hash = 29 * hash + (daemonServer!=null ? daemonServer.hashCode() : 0);
            hash = 29 * hash + (readOnly ? 1 : 0);
            return hash;
        }
    }

    private final Map<CacheKey,C> connectors = new HashMap<CacheKey,C>();

    public C get(
        UserId connectAs,
        UserId authenticateAs,
        String password,
        DomainName daemonServer,
        boolean readOnly
    ) {
        return connectors.get(new CacheKey(connectAs, authenticateAs, password, daemonServer, readOnly));
    }

    public void put(
        UserId connectAs,
        UserId authenticateAs,
        String password,
        DomainName daemonServer,
        boolean readOnly,
        C connector
    ) {
        connectors.put(new CacheKey(connectAs, authenticateAs, password, daemonServer, readOnly), connector);
    }
}
