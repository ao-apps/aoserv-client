package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  VirtualServer
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualServerTable extends CachedTableIntegerKey<VirtualServer> {

    VirtualServerTable(AOServConnector connector) {
        super(connector, VirtualServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public VirtualServer get(Object server) {
        try {
            return getUniqueRow(VirtualServer.COLUMN_SERVER, server);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.VIRTUAL_SERVERS;
    }
}
