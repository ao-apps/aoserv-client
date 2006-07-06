package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see BackupReport
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupReportTable extends AOServTable<Integer,BackupReport> {

    BackupReportTable(AOServConnector connector) {
	super(connector, BackupReport.class);
    }

    public BackupReport get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public BackupReport get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.BACKUP_REPORTS, pkey);
    }

    List<BackupReport> getBackupReports(Package pk) {
        Profiler.startProfile(Profiler.UNKNOWN, BackupReportTable.class, "getBackupReports(Package)", null);
        try {
            int pkPKey=pk.pkey;
            List<BackupReport> cached=getRows();
            int size=cached.size();
            List<BackupReport> matches=new ArrayList<BackupReport>(size);
            for(int c=0;c<size;c++) {
                BackupReport br=cached.get(c);
                if(br.packageNum==pkPKey) matches.add(br);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    List<BackupReport> getBackupReports(Server se) {
        Profiler.startProfile(Profiler.UNKNOWN, BackupReportTable.class, "getBackupReports(Server)", null);
        try {
            int sePKey=se.pkey;
            List<BackupReport> cached=getRows();
            int size=cached.size();
            List<BackupReport> matches=new ArrayList<BackupReport>(size);
            for(int c=0;c<size;c++) {
                BackupReport br=cached.get(c);
                if(br.server==sePKey) matches.add(br);
            }
            return matches;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public List<BackupReport> getRows() {
        List<BackupReport> list=new ArrayList<BackupReport>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.BACKUP_REPORTS);
        return list;
    }

    int getTableID() {
	return SchemaTable.BACKUP_REPORTS;
    }

    protected BackupReport getUniqueRowImpl(int col, Object value) {
        if(col!=BackupReport.COLUMN_PKEY) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
 }