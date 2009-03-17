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
 * @see  HttpdTomcatStdSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatStdSiteTable extends CachedTableIntegerKey<HttpdTomcatStdSite> {

    HttpdTomcatStdSiteTable(AOServConnector connector) {
	super(connector, HttpdTomcatStdSite.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
        new OrderBy(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addHttpdTomcatStdSite(
	AOServer aoServer,
	String siteName,
	Package packageObj,
	LinuxAccount jvmUser,
	LinuxGroup jvmGroup,
	String serverAdmin,
	boolean useApache,
	IPAddress ipAddress,
	String primaryHttpHostname,
	String[] altHttpHostnames,
	HttpdTomcatVersion tomcatVersion,
	String contentSrc
    ) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.HTTPD_TOMCAT_STD_SITES.ordinal());
            out.writeCompressedInt(aoServer.pkey);
            out.writeUTF(siteName);
            out.writeUTF(packageObj.name);
            out.writeUTF(jvmUser.pkey);
            out.writeUTF(jvmGroup.pkey);
            out.writeUTF(serverAdmin);
            out.writeBoolean(useApache);
            out.writeCompressedInt(ipAddress==null?-1:ipAddress.pkey);
            out.writeUTF(primaryHttpHostname);
            out.writeCompressedInt(altHttpHostnames.length);
            for(int c=0;c<altHttpHostnames.length;c++) out.writeUTF(altHttpHostnames[c]);
            out.writeCompressedInt(tomcatVersion.pkey);
            out.writeBoolean(contentSrc!=null);
            if (contentSrc!=null) out.writeUTF(contentSrc);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                pkey=in.readCompressedInt();
                invalidateList=AOServConnector.readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unknown response code: "+code);
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

    public HttpdTomcatStdSite get(Object pkey) {
        try {
            return getUniqueRow(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public HttpdTomcatStdSite get(int pkey) throws IOException, SQLException {
	return getUniqueRow(HttpdTomcatStdSite.COLUMN_TOMCAT_SITE, pkey);
    }

    public HttpdTomcatStdSite getHttpdTomcatStdSiteByShutdownPort(NetBind nb) throws IOException, SQLException {
        int pkey=nb.pkey;
        
        List<HttpdTomcatStdSite> cached=getRows();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            HttpdTomcatStdSite tomcat=cached.get(c);
            if(tomcat.tomcat4_shutdown_port==pkey) return tomcat;
	}
	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_STD_SITES;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_STD_SITE)) {
            if(AOSH.checkMinParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_STD_SITE, args, 12, err)) {
                // Create an array of all the alternate hostnames
                String[] altHostnames=new String[args.length-13];
                System.arraycopy(args, 13, altHostnames, 0, args.length-13);
                out.println(
                    connector.simpleAOClient.addHttpdTomcatStdSite(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        args[5],
                        args[6],
                        AOSH.parseBoolean(args[7], "use_apache"),
                        args[8],
                        args[9],
                        args[11],
                        altHostnames,
                        args[10],
                        args[12]
                    )
                );
                out.flush();
            }
            return true;
	}
	return false;
    }
}