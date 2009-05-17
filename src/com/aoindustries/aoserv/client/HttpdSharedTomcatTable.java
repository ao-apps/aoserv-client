package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSharedTomcat
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSharedTomcatTable extends CachedTableIntegerKey<HttpdSharedTomcat> {

    protected HttpdSharedTomcatTable(AOServConnector connector) {
	super(connector, HttpdSharedTomcat.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdSharedTomcat.COLUMN_NAME_name, ASCENDING),
        new OrderBy(HttpdSharedTomcat.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addHttpdSharedTomcat(
	String name,
        AOServer aoServer,
        HttpdTomcatVersion version,
	LinuxServerAccount lsa,
	LinuxServerGroup lsg,
	boolean isSecure,
        boolean isOverflow
    ) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.HTTPD_SHARED_TOMCATS.ordinal());
            out.writeUTF(name);
            out.writeCompressedInt(aoServer.pkey);
            out.writeCompressedInt(version.getTechnologyVersion(connector).getPkey());
            out.writeUTF(lsa.username);
            out.writeUTF(lsg.name);
            out.writeBoolean(isSecure);
            out.writeBoolean(isOverflow);
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
    }

    public String generateSharedTomcatName(String template) throws IOException, SQLException {
	return connector.requestStringQuery(AOServProtocol.CommandID.GENERATE_SHARED_TOMCAT_NAME, template);
    }

    public HttpdSharedTomcat get(Object pkey) {
        try {
            return getUniqueRow(HttpdSharedTomcat.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public HttpdSharedTomcat get(int pkey) throws SQLException, IOException {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_PKEY, pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcat(HttpdWorker hw) throws SQLException, IOException {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_WORKER, hw.pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort(NetBind nb) throws SQLException, IOException {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_SHUTDOWN_PORT, nb.pkey);
    }

    List<HttpdSharedTomcat> getHttpdSharedTomcats(LinuxServerAccount lsa) throws IOException, SQLException {
        return getIndexedRows(HttpdSharedTomcat.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    List<HttpdSharedTomcat> getHttpdSharedTomcats(Package pk) throws IOException, SQLException {
        String pkname=pk.name;

        List<HttpdSharedTomcat> cached=getRows();
	int size=cached.size();
        List<HttpdSharedTomcat> matches=new ArrayList<HttpdSharedTomcat>(size);
        for(int c=0;c<size;c++) {
            HttpdSharedTomcat hst=cached.get(c);
            if(hst.getLinuxServerGroup().getLinuxGroup().packageName.equals(pkname)) matches.add(hst);
        }
	return matches;
    }

    List<HttpdSharedTomcat> getHttpdSharedTomcats(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(HttpdSharedTomcat.COLUMN_AO_SERVER, ao.pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcat(String name, AOServer ao) throws IOException, SQLException {
        // Use the index first
        List<HttpdSharedTomcat> cached=getHttpdSharedTomcats(ao);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            HttpdSharedTomcat tomcat=cached.get(c);
            if(tomcat.getName().equals(name)) return tomcat;
	}
	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SHARED_TOMCATS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT, args, 7, err)) {
                // Create an array of all the alternate hostnames
                out.println(
                    connector.getSimpleAOClient().addHttpdSharedTomcat(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        args[5],
                        AOSH.parseBoolean(args[6], "is_secure"),
                        AOSH.parseBoolean(args[7], "is_overflow")
                    )
                );
                out.flush();
            }
            return true;
	} if(command.equalsIgnoreCase(AOSHCommand.CHECK_SHARED_TOMCAT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_SHARED_TOMCAT_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkSharedTomcatName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_SHARED_TOMCAT_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SHARED_TOMCAT, args, 3, err)) {
                out.println(
                    connector.getSimpleAOClient().disableHttpdSharedTomcat(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
                connector.getSimpleAOClient().enableHttpdSharedTomcat(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME, args, 1, err)) {
                out.println(connector.getSimpleAOClient().generateSharedTomcatName(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE, args, 1, err)) {
                out.println(connector.getSimpleAOClient().isSharedTomcatNameAvailable(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
                connector.getSimpleAOClient().removeHttpdSharedTomcat(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, args, 3, err)) {
                connector.getSimpleAOClient().setHttpdSharedTomcatIsManual(
                    args[1],
                    args[2],
                    AOSH.parseBoolean(args[3], "is_manual")
                );
            }
            return true;
	}
	return false;
    }

    public boolean isSharedTomcatNameAvailable(String name) throws IOException, SQLException {
	return connector.requestBooleanQuery(
            AOServProtocol.CommandID.IS_SHARED_TOMCAT_NAME_AVAILABLE,
            name
	);
    }
}