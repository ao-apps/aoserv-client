package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.StringUtility;
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

        final String connectAs;
        final String authenticateAs;
        final String password;
        final String daemonServer;

        CacheKey(
            String connectAs,
            String authenticateAs,
            String password,
            String daemonServer
        ) {
            this.connectAs = connectAs;
            this.authenticateAs = authenticateAs;
            this.password = password;
            this.daemonServer = daemonServer;
        }

        @Override
        public boolean equals(Object o) {
            if(o==null || !(o instanceof CacheKey)) return false;
            CacheKey other = (CacheKey)o;
            return
                connectAs.equals(other.connectAs)
                && authenticateAs.equals(other.authenticateAs)
                && password.equals(other.password)
                && StringUtility.equals(daemonServer, other.daemonServer)
            ;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + connectAs.hashCode();
            hash = 29 * hash + authenticateAs.hashCode();
            hash = 29 * hash + password.hashCode();
            hash = 29 * hash + (daemonServer!=null ? daemonServer.hashCode() : 0);
            return hash;
        }
    }

    private final Map<CacheKey,C> connectors = new HashMap<CacheKey,C>();

    public C get(
        String connectAs,
        String authenticateAs,
        String password,
        String daemonServer
    ) {
        return connectors.get(new CacheKey(connectAs, authenticateAs, password, daemonServer));
    }

    public void put(
        String connectAs,
        String authenticateAs,
        String password,
        String daemonServer,
        C connector
    ) {
        connectors.put(new CacheKey(connectAs, authenticateAs, password, daemonServer), connector);
    }
}
