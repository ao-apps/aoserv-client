package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MySQLServer</code> corresponds to a unique MySQL install
 * space on one server.  The server name must be unique per server.
 * <code>MySQLDatabase</code>s and <code>MySQLServerUser</code>s are
 * unique per <code>MySQLServer</code>.
 *
 * @see  MySQLDatabase
 * @see  MySQLServerUser
 *
 * @version  1.4
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServer extends CachedObjectIntegerKey<MySQLServer> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_NET_BIND=5,
        COLUMN_PACKAGE=6
    ;

    /**
     * The supported versions of MySQL.
     */
    public static final String
        VERSION_5_0_18="5.0.18",
        VERSION_4_1_18="4.1.18"
    ;

    /**
     * The directory that contains the MySQLSQL data files.
     */
    public static final String DATA_BASE_DIR="/var/lib/mysql";

    /**
     * Gets the versions of MySQL in order of
     * preference.  Index <code>0</code> is the most
     * preferred.
     */
    public static final String[] getPreferredVersions() {
        return new String[] {
            VERSION_5_0_18,
            VERSION_4_1_18
        };
    }

    /**
     * The maximum length of the name.
     */
    public static final int MAX_SERVER_NAME_LENGTH=31;

    String name;
    int ao_server;
    private int version;
    private int max_connections;
    int net_bind;
    String packageName;

    public int addMySQLDatabase(
        String name,
        Package pack
    ) {
	return table.connector.mysqlDatabases.addMySQLDatabase(
            name,
            this,
            pack
	);
    }

    public static void checkServerName(String name) throws IllegalArgumentException {
	// Must be a-z or 0-9 first, then a-z or 0-9 or . or _
	int len = name.length();
	if (len == 0 || len > MAX_SERVER_NAME_LENGTH) throw new IllegalArgumentException("MySQL server name should not exceed "+MAX_SERVER_NAME_LENGTH+" characters.");

        // The first character must be [a-z] or [0-9]
	char ch = name.charAt(0);
	if ((ch < 'a' || ch > 'z') && (ch<'0' || ch>'9')) throw new IllegalArgumentException("MySQL server names must start with [a-z] or [0-9]");
        // The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='.'
                && ch!='_'
            ) throw new IllegalArgumentException("MySQL server names may only contain [a-z], [0-9], period (.), and underscore (_)");
	}
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 3: return Integer.valueOf(version);
            case 4: return Integer.valueOf(max_connections);
            case COLUMN_NET_BIND: return Integer.valueOf(net_bind);
            case COLUMN_PACKAGE: return packageName;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }

    public String getName() {
	return name;
    }

    public List<MySQLBackup> getMySQLBackups() {
	return table.connector.mysqlBackups.getMySQLBackups(this);
    }

    /**
     * Gets the minor version number in X.X[-max|-source] format.  This corresponds to the installation
     * directory under /usr/mysql/X.X[-max|-source]
     */
    public String getMinorVersion() {
        String version=getVersion().getVersion();
        int pos=version.indexOf('.');
        if(pos==-1) return version;
        int pos2=version.indexOf('.', pos+1);
        if(pos2==-1) return version;
        String S = version.substring(0, pos2);
        if(version.endsWith("-max")) return S+"-max";
        if(version.endsWith("-source")) return S+"-source";
        return S;
    }

    public TechnologyVersion getVersion() {
	TechnologyVersion obj=table.connector.technologyVersions.get(version);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find TechnologyVersion: "+version));
	return obj;
    }

    public AOServer getAOServer() {
	AOServer ao=table.connector.aoServers.get(ao_server);
	if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return ao;
    }

    public int getMaxConnections() {
        return max_connections;
    }

    public NetBind getNetBind() {
        NetBind nb=table.connector.netBinds.get(net_bind);
        if(nb==null) throw new WrappedException(new SQLException("Unable to find NetBind: "+net_bind));
        return nb;
    }

    public Package getPackage() {
        Package pk=table.connector.packages.get(packageName);
        if(pk==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageName));
        return pk;
    }

    public MySQLDatabase getMySQLDatabase(String name) {
	return table.connector.mysqlDatabases.getMySQLDatabase(name, this);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() {
        return table.connector.failoverMySQLReplications.getFailoverMySQLReplications(this);
    }

    public List<MySQLDatabase> getMySQLDatabases() {
	return table.connector.mysqlDatabases.getMySQLDatabases(this);
    }

    public List<MySQLDBUser> getMySQLDBUsers() {
	return table.connector.mysqlDBUsers.getMySQLDBUsers(this);
    }

    public MySQLServerUser getMySQLServerUser(String username) {
	return table.connector.mysqlServerUsers.getMySQLServerUser(username, this);
    }

    public List<MySQLServerUser> getMySQLServerUsers() {
	return table.connector.mysqlServerUsers.getMySQLServerUsers(this);
    }

    public List<MySQLUser> getMySQLUsers() {
	List<MySQLServerUser> psu=getMySQLServerUsers();
	int len=psu.size();
	List<MySQLUser> pu=new ArrayList<MySQLUser>(len);
	for(int c=0;c<len;c++) pu.add(psu.get(c).getMySQLUser());
	return pu;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	ao_server=result.getInt(3);
	version=result.getInt(4);
        max_connections=result.getInt(5);
        net_bind=result.getInt(6);
        packageName=result.getString(7);
    }

    public boolean isMySQLDatabaseNameAvailable(String name) {
	return table.connector.mysqlDatabases.isMySQLDatabaseNameAvailable(name, this);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF().intern();
	ao_server=in.readCompressedInt();
	version=in.readCompressedInt();
        max_connections=in.readCompressedInt();
        net_bind=in.readCompressedInt();
        packageName=in.readUTF().intern();
    }

    public void restartMySQL() {
        table.connector.requestUpdate(AOServProtocol.RESTART_MYSQL, pkey);
    }

    public void startMySQL() {
        table.connector.requestUpdate(AOServProtocol.START_MYSQL, pkey);
    }

    public void stopMySQL() {
        table.connector.requestUpdate(AOServProtocol.STOP_MYSQL, pkey);
    }

    String toStringImpl() {
        return name+" on "+getAOServer().getServer().hostname;
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeCompressedInt(ao_server);
	out.writeCompressedInt(version);
        out.writeCompressedInt(max_connections);
        out.writeCompressedInt(net_bind);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_28)>=0) out.writeUTF(packageName);
    }

    final public static class MasterStatus {

        final private String file;
        final private String position;
        
        public MasterStatus(
            String file,
            String position
        ) {
            this.file=file;
            this.position=position;
        }

        public String getFile() {
            return file;
        }

        public String getPosition() {
            return position;
        }
    }

    /**
     * Gets the master status or <code>null</code> if no master status provided by MySQL.  If any error occurs, throws either
     * IOException or SQLException.
     */
    public MasterStatus getMasterStatus() throws IOException, SQLException {
        AOServConnection connection=table.connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.GET_MYSQL_MASTER_STATUS);
            out.writeCompressedInt(pkey);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.NEXT) {
                return new MasterStatus(
                    in.readNullUTF(),
                    in.readNullUTF()
                );
            } else if(code==AOServProtocol.DONE) {
                return null;
            } else AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            table.connector.releaseConnection(connection);
        }
    }
}
