/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.pki;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public final class CertificateTable extends CachedTableIntegerKey<Certificate> {

	CertificateTable(AOServConnector connector) {
		super(connector, Certificate.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Certificate.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(Certificate.COLUMN_CERT_FILE_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Certificate get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Certificate.COLUMN_PKEY, pkey);
	}

	public List<Certificate> getSslCertificates(Server aoServer) throws IOException, SQLException {
		return getIndexedRows(Certificate.COLUMN_AO_SERVER, aoServer.getPkey());
	}

	public List<Certificate> getSslCertificates(Package pk) throws IOException, SQLException {
		return getIndexedRows(Certificate.COLUMN_PACKAGE, pk.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SSL_CERTIFICATES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command = args[0];
		if(command.equalsIgnoreCase(Command.PKI_CERTIFICATE_CHECK)) {
			if(AOSH.checkParamCount(Command.PKI_CERTIFICATE_CHECK, args, 3, err)) {
				List<Certificate.Check> results = connector.getSimpleAOClient().checkSslCertificate(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "allowCached")
				);
				int size = results.size();
				List<Object[]> rows = new ArrayList<>(size);
				for(int i = 0; i < size; i++) {
					Certificate.Check status = results.get(i);
					rows.add(new Object[] {
						status.getCheck(),
						status.getValue(),
						status.getAlertLevel(),
						status.getMessage()
					});
				}
				// Display as a table
				SQLUtility.printTable(
					new String[] {"check", "value", "alert_level", "message"},
					rows,
					out,
					isInteractive,
					new boolean[] {false, false, false, false}
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
