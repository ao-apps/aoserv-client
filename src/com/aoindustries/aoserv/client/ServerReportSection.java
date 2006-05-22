package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Each <code>ServerReport</code> is comprised of many
 * <code>ServerReportSection</code>s.  These sections allow
 * access to a smaller set of data while not incurring the
 * cost of transferring the entire report.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class ServerReportSection<V extends ServerReportSection<V>> extends CachedObject<Integer,V> {

    protected int server_report;

    final public ServerReport getServerReport() {
        ServerReport sr=table.connector.serverReports.get(server_report);
        if(sr==null) throw new WrappedException(new SQLException("Unable to find ServerReport: "+server_report));
        return sr;
    }

    final boolean equalsImpl(Object O) {
	return
            O!=null
            && O.getClass()==getClass()
            && ((ServerReportSection)O).getKey().equals(getKey())
	;
    }
}