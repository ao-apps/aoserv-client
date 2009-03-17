package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  VirtualDisk
 *
 * @author  AO Industries, Inc.
 */
final public class VirtualDiskTable extends CachedTableIntegerKey<VirtualDisk> {

    VirtualDiskTable(AOServConnector connector) {
	super(connector, VirtualDisk.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(VirtualDisk.COLUMN_VIRTUAL_SERVER_name+'.'+VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(VirtualDisk.COLUMN_VIRTUAL_SERVER_name+'.'+VirtualServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(VirtualDisk.COLUMN_DEVICE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public VirtualDisk get(Object pkey) {
        try {
            return getUniqueRow(VirtualDisk.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.VIRTUAL_DISKS;
    }
    
    List<VirtualDisk> getVirtualDisks(VirtualServer vs) throws IOException, SQLException {
        return getIndexedRows(VirtualDisk.COLUMN_VIRTUAL_SERVER, vs.pkey);
    }
}
