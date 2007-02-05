package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteTable extends CachedTableIntegerKey<HttpdSite> {

    HttpdSiteTable(AOServConnector connector) {
	super(connector, HttpdSite.class);
    }

    public String generateSiteName(String template) {
	return connector.requestStringQuery(AOServProtocol.GENERATE_SITE_NAME, template);
    }

    public HttpdSite get(Object pkey) {
	return getUniqueRow(HttpdSite.COLUMN_PKEY, pkey);
    }

    public HttpdSite get(int pkey) {
	return getUniqueRow(HttpdSite.COLUMN_PKEY, pkey);
    }

    HttpdSite getHttpdSite(String siteName, AOServer ao) {
        int aoPKey=ao.pkey;

        List<HttpdSite> cached=getRows();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            HttpdSite site=cached.get(c);
            if(
                site.ao_server==aoPKey
                && site.site_name.equals(siteName)
            ) return site;
	}
	return null;
    }

    List<HttpdSite> getHttpdSites(HttpdServer server) {
        int serverPKey=server.pkey;

        List<HttpdSite> cached=getRows();
	int size=cached.size();
        List<HttpdSite> matches=new ArrayList<HttpdSite>(size);
	for(int c=0;c<size;c++) {
            HttpdSite site=cached.get(c);
            for(HttpdSiteBind bind : site.getHttpdSiteBinds()) {
                if(bind.getHttpdBind().httpd_server==serverPKey) {
                    matches.add(site);
                    break;
                }
            }
	}
	return matches;
    }

    List<HttpdSite> getHttpdSites(AOServer ao) {
        return getIndexedRows(HttpdSite.COLUMN_AO_SERVER, ao.pkey);
    }

    List<HttpdSite> getHttpdSites(Package pk) {
        return getIndexedRows(HttpdSite.COLUMN_PACKAGE, pk.name);
    }

    List<HttpdSite> getHttpdSites(LinuxServerAccount lsa) {
        String lsaUsername=lsa.username;
        int aoServer=lsa.ao_server;

	List<HttpdSite> cached=getRows();
	int size=cached.size();
        List<HttpdSite> matches=new ArrayList<HttpdSite>(size);
	for(int c=0;c<size;c++) {
            HttpdSite site=cached.get(c);
            if(
                site.ao_server==aoServer
                && site.linuxAccount.equals(lsaUsername)
            ) matches.add(site);
	}
	return matches;
    }

    int getTableID() {
	return SchemaTable.HTTPD_SITES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.CHECK_SITE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_SITE_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkSiteName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_SITE_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SITE)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SITE, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.disableHttpdSite(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SITE)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SITE, args, 2, err)) {
                connector.simpleAOClient.enableHttpdSite(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_SITE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_SITE_NAME, args, 1, err)) {
                out.println(connector.simpleAOClient.generateSiteName(args[1]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_AWSTATS_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_AWSTATS_FILE, args, 4, err)) {
                connector.simpleAOClient.getAWStatsFile(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    new WriterOutputStream(out)
                );
                out.flush();
            }
            return true;
        /*} else if(command.equalsIgnoreCase(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE)) {
            if(AOSH.checkParamCount(AOSHCommand.INITIALIZE_HTTPD_SITE_PASSWD_FILE, args, 4, err)) {
                connector.simpleAOClient.initializeHttpdSitePasswdFile(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
         */
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_SITE_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_SITE_NAME_AVAILABLE, args, 1, err)) {
                out.println(connector.simpleAOClient.isSiteNameAvailable(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SITE)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SITE, args, 2, err)) {
                connector.simpleAOClient.removeHttpdSite(args[1], args[2]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_SERVER_ADMIN)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_SERVER_ADMIN, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteServerAdmin(args[1], args[2], args[3]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_CONFIG_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_CONFIG_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteConfigBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_FILE_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_FILE_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteFileBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_FTP_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_FTP_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteFtpBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_IS_MANUAL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_IS_MANUAL, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteIsManual(
                    args[1],
                    args[2],
                    AOSH.parseBoolean(args[3], "is_manual")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_LOG_BACKUP_RETENTION)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_LOG_BACKUP_RETENTION, args, 3, err)) {
                connector.simpleAOClient.setHttpdSiteLogBackupRetention(
                    args[1],
                    args[2],
                    AOSH.parseShort(args[3], "backup_retention")
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_HTTPD_SITE_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_HTTPD_SITE_REBUILD, args, 1, err)) {
                connector.simpleAOClient.waitForHttpdSiteRebuild(args[1]);
            }
            return true;
	} else return false;
    }

    public boolean isSiteNameAvailable(String sitename) {
	return connector.requestBooleanQuery(AOServProtocol.IS_SITE_NAME_AVAILABLE, sitename);
    }

    void waitForRebuild(AOServer aoServer) {
	connector.requestUpdate(
            AOServProtocol.WAIT_FOR_REBUILD,
            SchemaTable.HTTPD_SITES,
            aoServer.pkey
        );
    }
}