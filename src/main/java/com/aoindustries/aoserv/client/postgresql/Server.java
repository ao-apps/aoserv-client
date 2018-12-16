/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.Port;
import com.aoindustries.validation.ValidationException;
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
 * @see  Version
 * @see  Database
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends CachedObjectIntegerKey<Server> {

	// <editor-fold defaultstate="collapsed" desc="Constants">
	/**
	 * The default PostSQL port.
	 */
	public static final Port DEFAULT_PORT;
	static {
		try {
			DEFAULT_PORT = Port.valueOf(5432, com.aoindustries.net.Protocol.TCP);
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * The directory that contains the PostgreSQL data files.
	 */
	public static final UnixPath DATA_BASE_DIR;
	static {
		try {
			DATA_BASE_DIR = UnixPath.valueOf("/var/lib/pgsql");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

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
		COLUMN_BIND = 0,
		COLUMN_AO_SERVER = 2
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	private PostgresServerName name;
	private int ao_server;
	private int version;
	private int max_connections;
	private int sort_mem;
	private int shared_buffers;
	private boolean fsync;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_BIND: return pkey;
			case 1: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return version;
			case 4: return max_connections;
			case 5: return sort_mem;
			case 6: return shared_buffers;
			case 7: return fsync;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getBind_id() {
		return pkey;
	}

	public Bind getBind() throws SQLException, IOException {
		Bind nb = table.getConnector().getNet().getNetBinds().get(pkey);
		if(nb == null) throw new SQLException("Unable to find NetBind: " + pkey);
		return nb;
	}

	public int getAoServer_server_pkey() {
		return ao_server;
	}

	public com.aoindustries.aoserv.client.linux.Server getAoServer() throws SQLException, IOException {
		com.aoindustries.aoserv.client.linux.Server ao = table.getConnector().getLinux().getAoServers().get(ao_server);
		if(ao == null) throw new SQLException("Unable to find AOServer: " + ao_server);
		return ao;
	}

	public int getVersion_version_id() {
		return version;
	}

	public Version getVersion() throws SQLException, IOException {
		Version obj=table.getConnector().getPostgresql().getPostgresVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find PostgresVersion: "+version);
		if(
			obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
			!= getAoServer().getServer().getOperatingSystemVersion_id()
		) {
			throw new SQLException("resource/operating system version mismatch on PostgresServer: #"+pkey);
		}
		return obj;
	}

	public int getMaxConnections() {
		return max_connections;
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
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			name = PostgresServerName.valueOf(result.getString(pos++));
			ao_server = result.getInt(pos++);
			version = result.getInt(pos++);
			max_connections = result.getInt(pos++);
			sort_mem = result.getInt(pos++);
			shared_buffers = result.getInt(pos++);
			fsync = result.getBoolean(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			name = PostgresServerName.valueOf(in.readUTF()).intern();
			ao_server = in.readCompressedInt();
			version = in.readCompressedInt();
			max_connections = in.readCompressedInt();
			sort_mem = in.readCompressedInt();
			shared_buffers = in.readCompressedInt();
			fsync = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(version);
		out.writeCompressedInt(max_connections);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeCompressedInt(pkey); // net_bind
		}
		out.writeCompressedInt(sort_mem);
		out.writeCompressedInt(shared_buffers);
		out.writeBoolean(fsync);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130)<=0) {
			out.writeCompressedInt(-1);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_SERVERS;
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return name+" on "+getAoServer().getHostname();
	}

	public int addPostgresDatabase(
		PostgresDatabaseName name,
		UserServer datdba,
		Encoding encoding,
		boolean enablePostgis
	) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresDatabases().addPostgresDatabase(
			name,
			this,
			datdba,
			encoding,
			enablePostgis
		);
	}

	public UnixPath getDataDirectory() {
		try {
			return UnixPath.valueOf(DATA_BASE_DIR.toString() + '/' + name.toString());
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError();
			ae.initCause(e);
			throw ae;
		}
	}

	public PostgresServerName getName() {
		return name;
	}

	public Database getPostgresDatabase(PostgresDatabaseName name) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresDatabases().getPostgresDatabase(name, this);
	}

	public List<Database> getPostgresDatabases() throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresDatabases().getPostgresDatabases(this);
	}

	public UserServer getPostgresServerUser(PostgresUserId username) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresServerUsers().getPostgresServerUser(username, this);
	}

	public List<UserServer> getPostgresServerUsers() throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresServerUsers().getPostgresServerUsers(this);
	}

	public List<User> getPostgresUsers() throws SQLException, IOException {
		List<UserServer> psu=getPostgresServerUsers();
		int len=psu.size();
		List<User> pu=new ArrayList<>(len);
		for(int c=0;c<len;c++) pu.add(psu.get(c).getPostgresUser());
		return pu;
	}

	public boolean isPostgresDatabaseNameAvailable(PostgresDatabaseName name) throws IOException, SQLException {
		return table.getConnector().getPostgresql().getPostgresDatabases().isPostgresDatabaseNameAvailable(name, this);
	}

	public void restartPostgreSQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_POSTGRESQL, pkey);
	}

	public void startPostgreSQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_POSTGRESQL, pkey);
	}

	public void stopPostgreSQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_POSTGRESQL, pkey);
	}
}
