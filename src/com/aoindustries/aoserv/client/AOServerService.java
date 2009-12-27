package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ao_servers)
public interface AOServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,AOServer> {

    /**
     * Supports both Integer (server) and String (hostname) keys.
     */
    /* TODO
    @Override
    public AOServer get(Object pkey) throws IOException, SQLException {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get((String)pkey);
        else throw new IllegalArgumentException("Must be an Integer or a String");
    }
     */

    /* TODO
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

    AOServer getAOServerByDaemonConnectNetBind(NetBind nb) throws IOException, SQLException {
        int pkey=nb.pkey;
        List<AOServer> servers=getRows();
        int size=servers.size();
        for(int c=0;c<size;c++) {
            AOServer se=servers.get(c);
            if(se.daemon_connect_bind==pkey) return se;
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
    */
    /**
     * @see  AOServer#getNestedAOServers()
     */
    /*
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
    */

    /*
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.GET_MRTG_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_MRTG_FILE, args, 2, err)) {
                connector.getSimpleAOClient().getMrtgFile(
                    args[1],
                    args[2],
                    new WriterOutputStream(out)
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.RESTART_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_APACHE, args, 1, err)) {
                connector.getSimpleAOClient().restartApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_CRON, args, 1, err)) {
                connector.getSimpleAOClient().restartCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_XFS, args, 1, err)) {
                connector.getSimpleAOClient().restartXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.RESTART_XVFB, args, 1, err)) {
                connector.getSimpleAOClient().restartXvfb(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.START_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.START_APACHE, args, 1, err)) {
                connector.getSimpleAOClient().startApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.START_CRON, args, 1, err)) {
                connector.getSimpleAOClient().startCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.START_XFS, args, 1, err)) {
                connector.getSimpleAOClient().startXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.START_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.START_XVFB, args, 1, err)) {
                connector.getSimpleAOClient().startXvfb(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_APACHE)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_APACHE, args, 1, err)) {
                connector.getSimpleAOClient().stopApache(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_CRON)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_CRON, args, 1, err)) {
                connector.getSimpleAOClient().stopCron(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XFS)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_XFS, args, 1, err)) {
                connector.getSimpleAOClient().stopXfs(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XVFB)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_XVFB, args, 1, err)) {
                connector.getSimpleAOClient().stopXvfb(
                    args[1]
                );
            }
            return true;
	}
	return false;
    }
     */
}