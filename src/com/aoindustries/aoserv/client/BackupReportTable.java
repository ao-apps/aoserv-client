package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
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

    public BackupReport get(Object pkey) {
        try {
            return get(((Integer)pkey).intValue());
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BackupReport get(int pkey) throws IOException, SQLException {
        return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.BACKUP_REPORTS, pkey);
    }

    List<BackupReport> getBackupReports(Package pk) throws IOException, SQLException {
        int pkPKey=pk.pkey;
        List<BackupReport> cached=getRows();
        int size=cached.size();
        List<BackupReport> matches=new ArrayList<BackupReport>(size);
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
        List<BackupReport> matches=new ArrayList<BackupReport>(size);
        for(int c=0;c<size;c++) {
            BackupReport br=cached.get(c);
            if(br.server==sePKey) matches.add(br);
        }
        return matches;
    }

    public List<BackupReport> getRows() throws IOException, SQLException {
        List<BackupReport> list=new ArrayList<BackupReport>();
        getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.BACKUP_REPORTS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.BACKUP_REPORTS;
    }

    protected BackupReport getUniqueRowImpl(int col, Object value) {
        if(col!=BackupReport.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
 }
