package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A <code>MySQLServer</code> corresponds to a unique MySQL install
 * space on one server.  The server name must be unique per server.
 * <code>MySQLDatabase</code>s and <code>MySQLServerUser</code>s are
 * unique per <code>MySQLServer</code>.
 *
 * @see  MySQLDatabase
 * @see  MySQLServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServer extends CachedObjectIntegerKey<MySQLServer> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=2,
        COLUMN_NET_BIND=5,
        COLUMN_ACCOUNTING=6
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_NAME_name = "name";

    /**
     * The supported versions of MySQL.
     */
    public static final String
        VERSION_5_1_PREFIX="5.1.",
        VERSION_5_0_PREFIX="5.0.",
        VERSION_4_1_PREFIX="4.1.",
        VERSION_4_0_PREFIX="4.0."
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
    public static final String[] getPreferredVersionPrefixes() {
        return new String[] {
            VERSION_5_1_PREFIX,
            VERSION_5_0_PREFIX,
            VERSION_4_1_PREFIX,
            VERSION_4_0_PREFIX
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
    String accounting;

    public int addMySQLDatabase(
        String name,
        Business bu
    ) throws IOException, SQLException {
    	return table.connector.getMysqlDatabases().addMySQLDatabase(
            name,
            this,
            bu
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 3: return Integer.valueOf(version);
            case 4: return Integer.valueOf(max_connections);
            case COLUMN_NET_BIND: return Integer.valueOf(net_bind);
            case COLUMN_ACCOUNTING: return accounting;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }

    public String getName() {
	return name;
    }

    /**
     * Gets the minor version number in X.X[-max] format.  This corresponds to the installation
     * directory under /usr/mysql/X.X[-max] or /opt/mysql-X.X[-max]
     */
    public String getMinorVersion() throws SQLException, IOException {
        String techVersion=getVersion().getVersion();
        int pos=techVersion.indexOf('.');
        if(pos==-1) return techVersion;
        int pos2=techVersion.indexOf('.', pos+1);
        if(pos2==-1) return techVersion;
        String S = techVersion.substring(0, pos2);
        if(techVersion.endsWith("-max")) return S+"-max";
        return S;
    }

    public TechnologyVersion getVersion() throws SQLException, IOException {
	TechnologyVersion obj=table.connector.getTechnologyVersions().get(version);
	if(obj==null) throw new SQLException("Unable to find TechnologyVersion: "+version);
        if(
            obj.getOperatingSystemVersion(table.connector).getPkey()
            != getAOServer().getServer().getOperatingSystemVersion().getPkey()
        ) {
            throw new SQLException("resource/operating system version mismatch on MySQLServer: #"+pkey);
        }
	return obj;
    }

    public AOServer getAOServer() throws SQLException, IOException {
	AOServer ao=table.connector.getAoServers().get(ao_server);
	if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
	return ao;
    }

    public int getMaxConnections() {
        return max_connections;
    }

    public NetBind getNetBind() throws SQLException, IOException {
        NetBind nb=table.connector.getNetBinds().get(net_bind);
        if(nb==null) throw new SQLException("Unable to find NetBind: "+net_bind);
        return nb;
    }

    public Business getBusiness() throws SQLException, IOException {
        Business bu=table.connector.getBusinesses().get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }

    public MySQLDatabase getMySQLDatabase(String name) throws IOException, SQLException {
    	return table.connector.getMysqlDatabases().getMySQLDatabase(name, this);
    }

    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return table.connector.getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<MySQLDatabase> getMySQLDatabases() throws IOException, SQLException {
	return table.connector.getMysqlDatabases().getMySQLDatabases(this);
    }

    public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
	return table.connector.getMysqlDBUsers().getMySQLDBUsers(this);
    }

    public MySQLServerUser getMySQLServerUser(String username) throws IOException, SQLException {
	return table.connector.getMysqlServerUsers().getMySQLServerUser(username, this);
    }

    public List<MySQLServerUser> getMySQLServerUsers() throws IOException, SQLException {
	return table.connector.getMysqlServerUsers().getMySQLServerUsers(this);
    }

    public List<MySQLUser> getMySQLUsers() throws IOException, SQLException {
	List<MySQLServerUser> psu=getMySQLServerUsers();
	int len=psu.size();
	List<MySQLUser> pu=new ArrayList<MySQLUser>(len);
	for(int c=0;c<len;c++) pu.add(psu.get(c).getMySQLUser());
	return pu;
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.MYSQL_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        name=result.getString(2);
        ao_server=result.getInt(3);
        version=result.getInt(4);
        max_connections=result.getInt(5);
        net_bind=result.getInt(6);
        accounting=result.getString(7);
    }

    public boolean isMySQLDatabaseNameAvailable(String name) throws IOException, SQLException {
    	return table.connector.getMysqlDatabases().isMySQLDatabaseNameAvailable(name, this);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        name=in.readUTF().intern();
        ao_server=in.readCompressedInt();
        version=in.readCompressedInt();
        max_connections=in.readCompressedInt();
        net_bind=in.readCompressedInt();
        accounting=in.readUTF().intern();
    }

    public void restartMySQL() throws IOException, SQLException {
        table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_MYSQL, pkey);
    }

    public void startMySQL() throws IOException, SQLException {
        table.connector.requestUpdate(false, AOServProtocol.CommandID.START_MYSQL, pkey);
    }

    public void stopMySQL() throws IOException, SQLException {
        table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_MYSQL, pkey);
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return name+" on "+getAOServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(name);
        out.writeCompressedInt(ao_server);
        out.writeCompressedInt(version);
        out.writeCompressedInt(max_connections);
        out.writeCompressedInt(net_bind);
        if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_28)>=0) out.writeUTF(accounting);
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
        return table.connector.requestResult(
            true,
            new AOServConnector.ResultRequest<MasterStatus>() {
                MasterStatus result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_MYSQL_MASTER_STATUS.ordinal());
                    out.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        result = new MasterStatus(
                            in.readNullUTF(),
                            in.readNullUTF()
                        );
                    } else if(code==AOServProtocol.DONE) {
                        result = null;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public MasterStatus afterRelease() {
                    return result;
                }
            }
        );
    }
}
