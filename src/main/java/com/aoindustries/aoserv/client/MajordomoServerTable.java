/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  MajordomoServer
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoServerTable extends CachedTableIntegerKey<MajordomoServer> {

	MajordomoServerTable(AOServConnector connector) {
	super(connector, MajordomoServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(MajordomoServer.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addMajordomoServer(
		EmailDomain emailDomain,
		LinuxServerAccount linuxServerAccount,
		LinuxServerGroup linuxServerGroup,
		MajordomoVersion majordomoVersion
	) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.MAJORDOMO_SERVERS,
			emailDomain.pkey,
			linuxServerAccount.pkey,
			linuxServerGroup.pkey,
			majordomoVersion.pkey
		);
	}

	@Override
	public MajordomoServer get(int domain) throws IOException, SQLException {
		return getUniqueRow(MajordomoServer.COLUMN_DOMAIN, domain);
	}

	List<MajordomoServer> getMajordomoServers(AOServer ao) throws IOException, SQLException {
		int aoPKey=ao.pkey;
		List<MajordomoServer> cached=getRows();
		int size=cached.size();
		List<MajordomoServer> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			MajordomoServer ms=cached.get(c);
			if(ms.getDomain().ao_server==aoPKey) matches.add(ms);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MAJORDOMO_SERVERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_MAJORDOMO_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_MAJORDOMO_SERVER, args, 5, err)) {
				connector.getSimpleAOClient().addMajordomoServer(
					AOSH.parseDomainName(args[1], "domain"),
					args[2],
					args[3],
					args[4],
					args[5]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MAJORDOMO_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_MAJORDOMO_SERVER, args, 2, err)) {
				connector.getSimpleAOClient().removeMajordomoServer(
					AOSH.parseDomainName(args[1], "domain"),
					args[2]
				);
			}
			return true;
		}
		return false;
	}
}
