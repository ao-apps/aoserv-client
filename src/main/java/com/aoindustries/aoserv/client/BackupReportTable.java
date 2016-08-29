/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	BackupReportTable(AOServConnector connector) {
		super(connector, BackupReport.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BackupReport.COLUMN_DATE_name, DESCENDING),
		new OrderBy(BackupReport.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BackupReport.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BackupReport.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BackupReport get(Object pkey) throws IOException, SQLException {
		return get(((Integer)pkey).intValue());
	}

	public BackupReport get(int pkey) throws IOException, SQLException {
		return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.BACKUP_REPORTS, pkey);
	}

	List<BackupReport> getBackupReports(Package pk) throws IOException, SQLException {
		int pkPKey=pk.pkey;
		List<BackupReport> cached=getRows();
		int size=cached.size();
		List<BackupReport> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			BackupReport br=cached.get(c);
			if(br.packageNum==pkPKey) matches.add(br);
		}
		return matches;
	}

	List<BackupReport> getBackupReports(Server se) throws IOException, SQLException {
		int sePKey=se.pkey;
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
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.BACKUP_REPORTS);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BACKUP_REPORTS;
	}

	@Override
	protected BackupReport getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=BackupReport.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
 }
