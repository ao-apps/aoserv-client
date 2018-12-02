/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see BackupReport
 *
 * @author  AO Industries, Inc.
 */
final public class BackupReportTable extends AOServTable<Integer,BackupReport> {

	public BackupReportTable(AOServConnector connector) {
		super(connector, BackupReport.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BackupReport.COLUMN_DATE_name, DESCENDING),
		new OrderBy(BackupReport.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BackupReport.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BackupReport.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public BackupReport get(Object pkey) throws IOException, SQLException {
		if(pkey == null) return null;
		return get(((Integer)pkey).intValue());
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	public BackupReport get(int pkey) throws IOException, SQLException {
		return getObject(true, AoservProtocol.CommandID.GET_OBJECT, Table.TableID.BACKUP_REPORTS, pkey);
	}

	public List<BackupReport> getBackupReports(Package pk) throws IOException, SQLException {
		int pkPKey=pk.getPkey();
		List<BackupReport> cached=getRows();
		int size=cached.size();
		List<BackupReport> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			BackupReport br=cached.get(c);
			if(br.packageNum==pkPKey) matches.add(br);
		}
		return matches;
	}

	public List<BackupReport> getBackupReports(Host se) throws IOException, SQLException {
		int sePKey=se.getPkey();
		List<BackupReport> cached=getRows();
		int size=cached.size();
		List<BackupReport> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			BackupReport br=cached.get(c);
			if(br.server==sePKey) matches.add(br);
		}
		return matches;
	}

	@Override
	public List<BackupReport> getRows() throws IOException, SQLException {
		List<BackupReport> list=new ArrayList<>();
		getObjects(true, list, AoservProtocol.CommandID.GET_TABLE, Table.TableID.BACKUP_REPORTS);
		return list;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BACKUP_REPORTS;
	}

	@Override
	protected BackupReport getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=BackupReport.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
 }
