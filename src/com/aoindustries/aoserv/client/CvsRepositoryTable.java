package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.sort.AutoSort;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  CvsRepository
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CvsRepositoryTable extends CachedTableIntegerKey<CvsRepository> {

    CvsRepositoryTable(AOServConnector connector) {
	super(connector, CvsRepository.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(CvsRepository.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addCvsRepository(
        AOServer ao,
        String path,
        LinuxServerAccount lsa,
        LinuxServerGroup lsg,
        long mode
    ) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.CVS_REPOSITORIES,
            ao.pkey,
            path,
            lsa.pkey,
            lsg.pkey,
            mode
	);
    }

    public CvsRepository get(Object pkey) {
	return getUniqueRow(CvsRepository.COLUMN_PKEY, pkey);
    }

    public CvsRepository get(int pkey) {
	return getUniqueRow(CvsRepository.COLUMN_PKEY, pkey);
    }

    /**
     * Gets one <code>CvsRepository</code> from the database.
     */
    CvsRepository getCvsRepository(AOServer aoServer, String path) {
        int aoPKey=aoServer.pkey;

        List<CvsRepository> cached=getRows();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            CvsRepository cr=cached.get(c);
            if(cr.path.equals(path) && cr.getLinuxServerAccount().ao_server==aoPKey) return cr;
        }
	return null;
    }

    List<CvsRepository> getCvsRepositories(AOServer aoServer) {
        int aoPKey=aoServer.pkey;

        List<CvsRepository> cached=getRows();
	int size=cached.size();
        List<CvsRepository> matches=new ArrayList<CvsRepository>(size);
        for(int c=0;c<size;c++) {
            CvsRepository cr=cached.get(c);
            if(cr.getLinuxServerAccount().ao_server==aoPKey) matches.add(cr);
        }
	return matches;
    }

    List<CvsRepository> getCvsRepositories(Package pk) {
        String pkname=pk.name;

        List<CvsRepository> cached=getRows();
	int size=cached.size();
        List<CvsRepository> matches=new ArrayList<CvsRepository>(size);
        for(int c=0;c<size;c++) {
            CvsRepository cr=cached.get(c);
            if(cr.getLinuxServerAccount().getLinuxAccount().getUsername().packageName.equals(pkname)) matches.add(cr);
        }
	return matches;
    }

    List<CvsRepository> getCvsRepositories(LinuxServerAccount lsa) {
        return getIndexedRows(CvsRepository.COLUMN_LINUX_SERVER_ACCOUNT, lsa.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CVS_REPOSITORIES;
    }

    public List<String> getValidPrefixes() {
        List<String> prefixes=new ArrayList<String>();

        // Home directories
        for(LinuxServerAccount lsa : connector.linuxServerAccounts.getRows()) {
            if(lsa.getLinuxAccount().getType().getName().equals(LinuxAccountType.USER) && lsa.getDisableLog()==null) {
                String dir=lsa.getHome();
                if(!prefixes.contains(dir)) prefixes.add(dir);
            }
        }

        // HttpdSites
        for(HttpdSite site : connector.httpdSites.getRows()) {
            String dir=site.getInstallDirectory();
            if(site.getDisableLog()==null && !prefixes.contains(dir)) prefixes.add(dir);
        }

        // HttpdSharedTomcats
        for(HttpdSharedTomcat tomcat : connector.httpdSharedTomcats.getRows()) {
            String dir=tomcat.getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory()+'/'+tomcat.getName();
            if(tomcat.getDisableLog()==null && !prefixes.contains(dir)) prefixes.add(dir);
        }
        
        // The global directory
        if(!prefixes.contains("/var/cvs")) prefixes.add("/var/cvs");

        // Sort and return
        AutoSort.sortStatic(prefixes);
        return prefixes;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 5, err)) {
                int pkey=connector.simpleAOClient.addCvsRepository(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    AOSH.parseOctalLong(args[5], "mode")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_CVS_REPOSITORY, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableCvsRepository(
                        AOSH.parseInt(args[1], "pkey"),
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_CVS_REPOSITORY, args, 1, err)) {
                connector.simpleAOClient.enableCvsRepository(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 2, err)) {
                connector.simpleAOClient.removeCvsRepository(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_CVS_REPOSITORY_MODE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_CVS_REPOSITORY_MODE, args, 3, err)) {
                connector.simpleAOClient.setCvsRepositoryMode(
                    args[1],
                    args[2],
                    AOSH.parseOctalLong(args[3], "mode")
                );
            }
            return true;
        }
	return false;
    }
}