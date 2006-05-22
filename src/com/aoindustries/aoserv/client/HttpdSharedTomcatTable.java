package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    int addHttpdSharedTomcat(
	String name,
        AOServer aoServer,
        HttpdTomcatVersion version,
	LinuxServerAccount lsa,
	LinuxServerGroup lsg,
	boolean isSecure,
        boolean isOverflow
    ) {
        try {
            int pkey;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.HTTPD_SHARED_TOMCATS);
                out.writeUTF(name);
                out.writeCompressedInt(aoServer.pkey);
                out.writeCompressedInt(version.getTechnologyVersion(connector).getPKey());
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
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public String generateSharedTomcatName(String template) {
	return connector.requestStringQuery(AOServProtocol.GENERATE_SHARED_TOMCAT_NAME, template);
    }

    public HttpdSharedTomcat get(Object pkey) {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_PKEY, pkey);
    }

    public HttpdSharedTomcat get(int pkey) {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_PKEY, pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcat(HttpdWorker hw) {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_WORKER, hw.pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcatByShutdownPort(NetBind nb) {
	return getUniqueRow(HttpdSharedTomcat.COLUMN_TOMCAT4_SHUTDOWN_PORT, nb.pkey);
    }

    List<HttpdSharedTomcat> getHttpdSharedTomcats(LinuxServerAccount lsa) {
        return getIndexedRows(HttpdSharedTomcat.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    List<HttpdSharedTomcat> getHttpdSharedTomcats(Package pk) {
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

    List<HttpdSharedTomcat> getHttpdSharedTomcats(AOServer ao) {
        return getIndexedRows(HttpdSharedTomcat.COLUMN_AO_SERVER, ao.pkey);
    }

    HttpdSharedTomcat getHttpdSharedTomcat(String name, AOServer ao) {
        // Use the index first
        List<HttpdSharedTomcat> cached=getHttpdSharedTomcats(ao);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            HttpdSharedTomcat tomcat=cached.get(c);
            if(tomcat.getName().equals(name)) return tomcat;
	}
	return null;
    }

    int getTableID() {
	return SchemaTable.HTTPD_SHARED_TOMCATS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_SHARED_TOMCAT, args, 7, err)) {
                // Create an array of all the alternate hostnames
                out.println(
                    connector.simpleAOClient.addHttpdSharedTomcat(
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
                    connector.simpleAOClient.disableHttpdSharedTomcat(
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
                connector.simpleAOClient.enableHttpdSharedTomcat(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_SHARED_TOMCAT_NAME, args, 1, err)) {
                out.println(connector.simpleAOClient.generateSharedTomcatName(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_SHARED_TOMCAT_NAME_AVAILABLE, args, 1, err)) {
                out.println(connector.simpleAOClient.isSharedTomcatNameAvailable(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SHARED_TOMCAT, args, 2, err)) {
                connector.simpleAOClient.removeHttpdSharedTomcat(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_CONFIG_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_CONFIG_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSharedTomcatConfigBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_FILE_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_FILE_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSharedTomcatFileBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, args, 3, err)) {
                connector.simpleAOClient.setHttpdSharedTomcatIsManual(
                    args[1],
                    args[2],
                    AOSH.parseBoolean(args[3], "is_manual")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_LOG_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SHARED_TOMCAT_LOG_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSharedTomcatLogBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
	}
	return false;
    }

    public boolean isSharedTomcatNameAvailable(String name) {
	return connector.requestBooleanQuery(
            AOServProtocol.IS_SHARED_TOMCAT_NAME_AVAILABLE,
            name
	);
    }
}