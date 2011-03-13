/*
 * Copyright 2002-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  CvsRepository
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.cvs_repositories)
public interface CvsRepositoryService extends AOServService<Integer,CvsRepository> {

    /* TODO
    int addCvsRepository(
        AOServer ao,
        String path,
        LinuxServerAccount lsa,
        LinuxServerGroup lsg,
        long mode
    ) throws IOException, SQLException {
    	return connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.CVS_REPOSITORIES,
            ao.pkey,
            path,
            lsa.pkey,
            lsg.pkey,
            mode
    	);
    }

    public List<String> getValidPrefixes() throws IOException, SQLException {
        List<String> prefixes=new ArrayList<String>();

        // Home directories
        for(LinuxServerAccount lsa : connector.getLinuxServerAccounts().getRows()) {
            if(lsa.getLinuxAccount().getType().getName().equals(LinuxAccountType.USER) && !lsa.isDisabled()) {
                String dir=lsa.getHome();
                if(!prefixes.contains(dir)) prefixes.add(dir);
            }
        }

        // HttpdSites
        for(HttpdSite site : connector.getHttpdSites().getRows()) {
            String dir=site.getInstallDirectory();
            if(!site.isDisabled() && !prefixes.contains(dir)) prefixes.add(dir);
        }

        // HttpdSharedTomcats
        for(HttpdSharedTomcat tomcat : connector.getHttpdSharedTomcats().getRows()) {
            String dir=tomcat.getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory()+'/'+tomcat.getName();
            if(!tomcat.isDisabled() && !prefixes.contains(dir)) prefixes.add(dir);
        }
        
        // The global directory
        if(!prefixes.contains("/var/cvs")) prefixes.add("/var/cvs");

        // Sort and return
        AutoSort.sortStatic(prefixes);
        return prefixes;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 5, err)) {
                int pkey=connector.getSimpleAOClient().addCvsRepository(
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
                    connector.getSimpleAOClient().disableCvsRepository(
                        AOSH.parseInt(args[1], "pkey"),
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_CVS_REPOSITORY, args, 1, err)) {
                connector.getSimpleAOClient().enableCvsRepository(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_CVS_REPOSITORY)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_CVS_REPOSITORY, args, 2, err)) {
                connector.getSimpleAOClient().removeCvsRepository(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_CVS_REPOSITORY_MODE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_CVS_REPOSITORY_MODE, args, 3, err)) {
                connector.getSimpleAOClient().setCvsRepositoryMode(
                    args[1],
                    args[2],
                    AOSH.parseOctalLong(args[3], "mode")
                );
            }
            return true;
        }
	return false;
    }
    */
}
