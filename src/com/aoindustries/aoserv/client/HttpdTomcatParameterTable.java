package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatParameter
 *
 * @version  1.5
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatParameterTable extends CachedTableIntegerKey<HttpdTomcatParameter> {

    int addHttpdTomcatParameter(
        HttpdTomcatContext htc,
        String name,
        String value,
        boolean override,
        String description
    ) {
        return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.HTTPD_TOMCAT_PARAMETERS,
            htc.pkey,
            name,
            value,
            override,
            description==null ? "" : description
        );
    }

    HttpdTomcatParameterTable(AOServConnector connector) {
	super(connector, HttpdTomcatParameter.class);
    }

    public HttpdTomcatParameter get(Object pkey) {
	return getUniqueRow(HttpdTomcatParameter.COLUMN_PKEY, pkey);
    }

    public HttpdTomcatParameter get(int pkey) {
	return getUniqueRow(HttpdTomcatParameter.COLUMN_PKEY, pkey);
    }

    List<HttpdTomcatParameter> getHttpdTomcatParameters(HttpdTomcatContext htc) {
        return getIndexedRows(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT, htc.pkey);
    }

    HttpdTomcatParameter getHttpdTomcatParameter(HttpdTomcatContext htc, String name) {
        // Use index first
        List<HttpdTomcatParameter> parameters=getHttpdTomcatParameters(htc);
        for(HttpdTomcatParameter parameter : parameters) {
            if(parameter.name.equals(name)) return parameter;
        }
        return null;
    }

    int getTableID() {
	return SchemaTable.HTTPD_TOMCAT_PARAMETERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_PARAMETER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_PARAMETER, args, 7, err)) {
                out.println(
                    connector.simpleAOClient.addHttpdTomcatParameter(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        args[5],
                        AOSH.parseBoolean(args[6], "override"),
                        args[7]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_PARAMETER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_PARAMETER, args, 1, err)) {
                connector.simpleAOClient.removeHttpdTomcatParameter(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.UPDATE_HTTPD_TOMCAT_PARAMETER)) {
            if(AOSH.checkParamCount(AOSHCommand.UPDATE_HTTPD_TOMCAT_PARAMETER, args, 8, err)) {
                connector.simpleAOClient.updateHttpdTomcatParameter(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    AOSH.parseBoolean(args[7], "override"),
                    args[8]
                );
            }
            return true;
	} else return false;
    }
}
