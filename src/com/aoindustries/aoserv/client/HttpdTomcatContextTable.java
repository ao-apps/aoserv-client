package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdTomcatContext
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatContextTable extends CachedTableIntegerKey<HttpdTomcatContext> {

    HttpdTomcatContextTable(AOServConnector connector) {
	super(connector, HttpdTomcatContext.class);
    }

    int addHttpdTomcatContext(
        HttpdTomcatSite hts,
        String className,
        boolean cookies,
        boolean crossContext,
        String docBase,
        boolean override,
        String path,
        boolean privileged,
        boolean reloadable,
        boolean useNaming,
        String wrapperClass,
        int debug,
        String workDir
    ) {
        try {
            // Create the new profile
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            int pkey;
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.HTTPD_TOMCAT_CONTEXTS);
                out.writeCompressedInt(hts.pkey);
                AOServObject.writeNullUTF(out, className);
                out.writeBoolean(cookies);
                out.writeBoolean(crossContext);
                out.writeUTF(docBase);
                out.writeBoolean(override);
                out.writeUTF(path);
                out.writeBoolean(privileged);
                out.writeBoolean(reloadable);
                out.writeBoolean(useNaming);
                AOServObject.writeNullUTF(out, wrapperClass);
                out.writeCompressedInt(debug);
                AOServObject.writeNullUTF(out, workDir);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public HttpdTomcatContext get(Object pkey) {
	return getUniqueRow(HttpdTomcatContext.COLUMN_PKEY, pkey);
    }

    public HttpdTomcatContext get(int pkey) {
	return getUniqueRow(HttpdTomcatContext.COLUMN_PKEY, pkey);
    }

    HttpdTomcatContext getHttpdTomcatContext(HttpdTomcatSite hts, String path) {
        int hts_pkey=hts.pkey;
        List<HttpdTomcatContext> cached=getRows();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            HttpdTomcatContext htc=cached.get(c);
            if(htc.tomcat_site==hts_pkey && htc.path.equals(path)) return htc;
        }
        return null;
    }

    List<HttpdTomcatContext> getHttpdTomcatContexts(HttpdTomcatSite hts) {
        return getIndexedRows(HttpdTomcatContext.COLUMN_TOMCAT_SITE, hts.pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_TOMCAT_CONTEXTS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_CONTEXT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_CONTEXT, args, 14, err)) {
                out.println(
                    connector.simpleAOClient.addHttpdTomcatContext(
                        args[1],
                        args[2],
                        args[3],
                        AOSH.parseBoolean(args[4], "use_cookies"),
                        AOSH.parseBoolean(args[5], "cross_context"),
                        args[6],
                        AOSH.parseBoolean(args[7], "allow_override"),
                        args[8],
                        AOSH.parseBoolean(args[9], "is_privileged"),
                        AOSH.parseBoolean(args[10], "is_reloadable"),
                        AOSH.parseBoolean(args[11], "use_naming"),
                        args[12],
                        AOSH.parseInt(args[13], "debug_level"),
                        args[14]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_CONTEXT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_CONTEXT, args, 1, err)) {
                connector.simpleAOClient.removeHttpdTomcatContext(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES, args, 15, err)) {
                connector.simpleAOClient.setHttpdTomcatContextAttributes(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    AOSH.parseBoolean(args[5], "use_cookies"),
                    AOSH.parseBoolean(args[6], "cross_context"),
                    args[7],
                    AOSH.parseBoolean(args[8], "allow_override"),
                    args[9],
                    AOSH.parseBoolean(args[10], "is_privileged"),
                    AOSH.parseBoolean(args[11], "is_reloadable"),
                    AOSH.parseBoolean(args[12], "use_naming"),
                    args[13],
                    AOSH.parseInt(args[14], "debug_level"),
                    args[15]
                );
            }
            return true;
	} else return false;
    }
}