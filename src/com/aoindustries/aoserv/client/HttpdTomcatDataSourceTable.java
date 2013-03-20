/*
 * Copyright 2006-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatDataSource
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatDataSourceTable extends CachedTableIntegerKey<HttpdTomcatDataSource> {

    HttpdTomcatDataSourceTable(AOServConnector connector) {
	super(connector, HttpdTomcatDataSource.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
        new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_PATH_name, ASCENDING),
        new OrderBy(HttpdTomcatDataSource.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addHttpdTomcatDataSource(
        HttpdTomcatContext htc,
        String name,
        String driverClassName,
        String url,
        String username,
        String password,
        int maxActive,
        int maxIdle,
        int maxWait,
        String validationQuery
    ) throws IOException, SQLException {
        return connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES,
            htc.pkey,
            name,
            driverClassName,
            url,
            username,
            password,
            maxActive,
            maxIdle,
            maxWait,
            validationQuery==null ? "" : validationQuery
        );
    }

    public HttpdTomcatDataSource get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(HttpdTomcatDataSource.COLUMN_PKEY, pkey);
    }

    List<HttpdTomcatDataSource> getHttpdTomcatDataSources(HttpdTomcatContext htc) throws IOException, SQLException {
        return getIndexedRows(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT, htc.pkey);
    }

    HttpdTomcatDataSource getHttpdTomcatDataSource(HttpdTomcatContext htc, String name) throws IOException, SQLException {
        // Use index first
        List<HttpdTomcatDataSource> dataSources=getHttpdTomcatDataSources(htc);
        for(HttpdTomcatDataSource dataSource : dataSources) {
            if(dataSource.name.equals(name)) return dataSource;
        }
        return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES;
    }

    @Override
    boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_DATA_SOURCE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_DATA_SOURCE, args, 12, err)) {
                out.println(
                    connector.getSimpleAOClient().addHttpdTomcatDataSource(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        args[5],
                        args[6],
                        args[7],
                        args[8],
                        AOSH.parseInt(args[9], "max_active"),
                        AOSH.parseInt(args[10], "max_idle"),
                        AOSH.parseInt(args[11], "max_wait"),
                        args[12]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_DATA_SOURCE)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_DATA_SOURCE, args, 1, err)) {
                connector.getSimpleAOClient().removeHttpdTomcatDataSource(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.UPDATE_HTTPD_TOMCAT_DATA_SOURCE)) {
            if(AOSH.checkParamCount(AOSHCommand.UPDATE_HTTPD_TOMCAT_DATA_SOURCE, args, 13, err)) {
                connector.getSimpleAOClient().updateHttpdTomcatDataSource(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    args[7],
                    args[8],
                    args[9],
                    AOSH.parseInt(args[10], "max_active"),
                    AOSH.parseInt(args[11], "max_idle"),
                    AOSH.parseInt(args[12], "max_wait"),
                    args[13]
                );
            }
            return true;
	} else return false;
    }
}