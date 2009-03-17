package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerTable extends CachedTableIntegerKey<AOServer> {

    AOServerTable(AOServConnector connector) {
	super(connector, AOServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public AOServer get(Object pkey) {
        try {
            if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
            else if(pkey instanceof String) return get((String)pkey);
            else throw new IllegalArgumentException("Must be an Integer or a String");
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public AOServer get(int pkey) throws IOException, SQLException {
	return getUniqueRow(AOServer.COLUMN_SERVER, pkey);
    }

    public AOServer get(String hostname) throws IOException, SQLException {
	return getUniqueRow(AOServer.COLUMN_HOSTNAME, hostname);
    }

    AOServer getAOServerByDaemonNetBind(NetBind nb) throws IOException, SQLException {
        int pkey=nb.pkey;
        List<AOServer> servers=getRows();
        int size=servers.size();
        for(int c=0;c<size;c++) {
            AOServer se=servers.get(c);
            if(se.daemon_bind==pkey) return se;
        }
        return null;
    }

    AOServer getAOServerByJilterNetBind(NetBind nb) throws IOException, SQLException {
        int pkey=nb.pkey;
        List<AOServer> servers=getRows();
        int size=servers.size();
        for(int c=0;c<size;c++) {
            AOServer se=servers.get(c);
            if(se.jilter_bind==pkey) return se;
        }
        return null;
    }

    /**
     * @see  AOServer#getNestedAOServers()
     */
    List<AOServer> getNestedAOServers(AOServer server) throws IOException, SQLException {
        int pkey=server.pkey;
        List<AOServer> servers=getRows();
        int size=servers.size();
        List<AOServer> objs=new ArrayList<AOServer>();
        for(int c=0;c<size;c++) {
            AOServer se=servers.get(c);
            int fs=se.failover_server;
            if(fs!=-1 && fs==pkey) objs.add(se);
        }
        return objs;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.AO_SERVERS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.GET_MRTG_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_MRTG_FILE, args, 2, err)) {
                connector.simpleAOClient.getMrtgFile(
                    args[1],
                    args[2],
                    new WriterOutputStream(out)
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.RESTART_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_APACHE, args, 1, err)) {
                connector.simpleAOClient.restartApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_CRON, args, 1, err)) {
                connector.simpleAOClient.restartCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_XFS, args, 1, err)) {
                connector.simpleAOClient.restartXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_XVFB, args, 1, err)) {
                connector.simpleAOClient.restartXvfb(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.START_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.START_APACHE, args, 1, err)) {
                connector.simpleAOClient.startApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.START_CRON, args, 1, err)) {
                connector.simpleAOClient.startCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.START_XFS, args, 1, err)) {
                connector.simpleAOClient.startXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.START_XVFB, args, 1, err)) {
                connector.simpleAOClient.startXvfb(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_APACHE, args, 1, err)) {
                connector.simpleAOClient.stopApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_CRON, args, 1, err)) {
                connector.simpleAOClient.stopCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_XFS, args, 1, err)) {
                connector.simpleAOClient.stopXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_XVFB, args, 1, err)) {
                connector.simpleAOClient.stopXvfb(
                    args[1]
                );
            }
            return true;
	}
	return false;
    }
}