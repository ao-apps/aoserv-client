/*
 * Copyright 2008-2009, 2014, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

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

	@Override
	public VirtualDisk get(int pkey) throws IOException, SQLException {
		return getUniqueRow(VirtualDisk.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.VIRTUAL_DISKS;
	}

	List<VirtualDisk> getVirtualDisks(VirtualServer vs) throws IOException, SQLException {
		return getIndexedRows(VirtualDisk.COLUMN_VIRTUAL_SERVER, vs.pkey);
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.VERIFY_VIRTUAL_DISK)) {
			if(AOSH.checkParamCount(AOSHCommand.VERIFY_VIRTUAL_DISK, args, 2, err)) {
				long lastVerified = connector.getSimpleAOClient().verifyVirtualDisk(args[1], args[2]);
				if(isInteractive) {
					out.println(SQLUtility.getDateTime(lastVerified));
				} else {
					out.println(lastVerified);
				}
				out.flush();
			}
			return true;
		}
		return false;
	}
}
