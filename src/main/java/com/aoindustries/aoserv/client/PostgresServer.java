/*
 * Copyright 2002-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>PostgresServer</code> corresponds to a unique PostgreSQL install
 * space on one server.  The server name must be unique per server.
 * <code>PostgresDatabase</code>s and <code>PostgresServerUser</code>s are
 * unique per <code>PostgresServer</code>.
 *
 * @see  PostgresVersion
 * @see  PostgresDatabase
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServer extends CachedObjectIntegerKey<PostgresServer> {

	// <editor-fold defaultstate="collapsed" desc="Constants">
	/**
	 * The directory that contains the PostgreSQL data files.
	 */
	public static final String DATA_BASE_DIR="/var/lib/pgsql";

	public enum ReservedWord {
		ABORT,
		ALL,
		ANALYSE,
		ANALYZE,
		AND,
		ANY,
		AS,
		ASC,
		BETWEEN,
		BINARY,
		BIT,
		BOTH,
		CASE,
		CAST,
		CHAR,
		CHARACTER,
		CHECK,
		CLUSTER,
		COALESCE,
		COLLATE,
		COLUMN,
		CONSTRAINT,
		COPY,
		CROSS,
		CURRENT_DATE,
		CURRENT_TIME,
		CURRENT_TIMESTAMP,
		CURRENT_USER,
		DEC,
		DECIMAL,
		DEFAULT,
		DEFERRABLE,
		DESC,
		DISTINCT,
		DO,
		ELSE,
		END,
		EXCEPT,
		EXISTS,
		EXPLAIN,
		EXTEND,
		EXTRACT,
		FALSE,
		FLOAT,
		FOR,
		FOREIGN,
		FROM,
		FULL,
		GLOBAL,
		GROUP,
		HAVING,
		ILIKE,
		IN,
		INITIALLY,
		INNER,
		INOUT,
		INTERSECT,
		INTO,
		IS,
		ISNULL,
		JOIN,
		LEADING,
		LEFT,
		LIKE,
		LIMIT,
		LISTEN,
		LOAD,
		LOCAL,
		LOCK,
		MOVE,
		NATURAL,
		NCHAR,
		NEW,
		NOT,
		NOTNULL,
		NULL,
		NULLIF,
		NUMERIC,
		OFF,
		OFFSET,
		OLD,
		ON,
		ONLY,
		OR,
		ORDER,
		OUT,
		OUTER,
		OVERLAPS,
		POSITION,
		PRECISION,
		PRIMARY,
		PUBLIC,
		REFERENCES,
		RESET,
		RIGHT,
		SELECT,
		SESSION_USER,
		SETOF,
		SHOW,
		SOME,
		SUBSTRING,
		TABLE,
		THEN,
		TO,
		TRAILING,
		TRANSACTION,
		TRIM,
		TRUE,
		UNION,
		UNIQUE,
		USER,
		USING,
		VACUUM,
		VARCHAR,
		VERBOSE,
		WHEN,
		WHERE;

		private static volatile Set<String> reservedWords = null;

		/**
		 * Case-insensitive check for if the provided string is a reserved word.
		 */
		public static boolean isReservedWord(String value) {
			Set<String> words = reservedWords;
			if(words==null) {
				ReservedWord[] values = values();
				words = new HashSet<>(values.length*4/3+1);
				for(ReservedWord word : values) words.add(word.name().toUpperCase(Locale.ROOT));
				reservedWords = words;
			}
			return words.contains(value.toUpperCase(Locale.ROOT));
		}
	}
	// </editor-fold>

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=2,
		COLUMN_NET_BIND=5
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	/**
	 * The maximum length of the name.
	 */
	public static final int MAX_SERVER_NAME_LENGTH=31;

	String name;
	int ao_server;
	private int version;
	private int max_connections;
	int net_bind;
	private int sort_mem;
	private int shared_buffers;
	private boolean fsync;

	public int addPostgresDatabase(
		String name,
		PostgresServerUser datdba,
		PostgresEncoding encoding,
		boolean enablePostgis
	) throws IOException, SQLException {
		return table.connector.getPostgresDatabases().addPostgresDatabase(
			name,
			this,
			datdba,
			encoding,
			enablePostgis
		);
	}

	public static void checkServerName(String name) throws IllegalArgumentException {
		// Must be a-z or 0-9 first, then a-z or 0-9 or . or _
		int len = name.length();
		if (len == 0 || len > MAX_SERVER_NAME_LENGTH) throw new IllegalArgumentException("PostgreSQL server name should not exceed "+MAX_SERVER_NAME_LENGTH+" characters.");

		// The first character must be [a-z] or [0-9]
		char ch = name.charAt(0);
		if ((ch < 'a' || ch > 'z') && (ch<'0' || ch>'9')) throw new IllegalArgumentException("PostgreSQL server names must start with [a-z] or [0-9]");
		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch<'0' || ch>'9')
				&& ch!='.'
				&& ch!='_'
			) throw new IllegalArgumentException("PostgreSQL server names may only contain [a-z], [0-9], period (.), and underscore (_)");
		}
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return version;
			case 4: return max_connections;
			case COLUMN_NET_BIND: return net_bind;
			case 6: return sort_mem;
			case 7: return shared_buffers;
			case 8: return fsync;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDataDirectory() {
		return DATA_BASE_DIR+'/'+name;
	}

	public String getName() {
		return name;
	}

	public PostgresVersion getPostgresVersion() throws SQLException, IOException {
		PostgresVersion obj=table.connector.getPostgresVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find PostgresVersion: "+version);
		if(
			obj.getTechnologyVersion(table.connector).getOperatingSystemVersion(table.connector).getPkey()
			!= getAOServer().getServer().getOperatingSystemVersion().getPkey()
		) {
			throw new SQLException("resource/operating system version mismatch on PostgresServer: #"+pkey);
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

	public PostgresDatabase getPostgresDatabase(String name) throws IOException, SQLException {
		return table.connector.getPostgresDatabases().getPostgresDatabase(name, this);
	}

	public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
		return table.connector.getPostgresDatabases().getPostgresDatabases(this);
	}

	public PostgresServerUser getPostgresServerUser(String username) throws IOException, SQLException {
		return table.connector.getPostgresServerUsers().getPostgresServerUser(username, this);
	}

	public List<PostgresServerUser> getPostgresServerUsers() throws IOException, SQLException {
		return table.connector.getPostgresServerUsers().getPostgresServerUsers(this);
	}

	public List<PostgresUser> getPostgresUsers() throws SQLException, IOException {
		List<PostgresServerUser> psu=getPostgresServerUsers();
		int len=psu.size();
		List<PostgresUser> pu=new ArrayList<>(len);
		for(int c=0;c<len;c++) pu.add(psu.get(c).getPostgresUser());
		return pu;
	}

	public int getSortMem() {
		return sort_mem;
	}

	public int getSharedBuffers() {
		return shared_buffers;
	}

	public boolean getFSync() {
		return fsync;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		name=result.getString(2);
		ao_server=result.getInt(3);
		version=result.getInt(4);
		max_connections=result.getInt(5);
		net_bind=result.getInt(6);
		sort_mem=result.getInt(7);
		shared_buffers=result.getInt(8);
		fsync=result.getBoolean(9);
	}

	public boolean isPostgresDatabaseNameAvailable(String name) throws IOException, SQLException {
		return table.connector.getPostgresDatabases().isPostgresDatabaseNameAvailable(name, this);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		name=in.readUTF().intern();
		ao_server=in.readCompressedInt();
		version=in.readCompressedInt();
		max_connections=in.readCompressedInt();
		net_bind=in.readCompressedInt();
		sort_mem=in.readCompressedInt();
		shared_buffers=in.readCompressedInt();
		fsync=in.readBoolean();
	}

	public void restartPostgreSQL() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_POSTGRESQL, pkey);
	}

	public void startPostgreSQL() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.START_POSTGRESQL, pkey);
	}

	public void stopPostgreSQL() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_POSTGRESQL, pkey);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return name+" on "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(version);
		out.writeCompressedInt(max_connections);
		out.writeCompressedInt(net_bind);
		out.writeCompressedInt(sort_mem);
		out.writeCompressedInt(shared_buffers);
		out.writeBoolean(fsync);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_130)<=0) {
			out.writeCompressedInt(-1);
		}
	}
}