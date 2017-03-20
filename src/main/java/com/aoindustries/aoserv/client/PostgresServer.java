/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=2,
		COLUMN_NET_BIND=5
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	PostgresServerName name;
	int ao_server;
	private int version;
	private int max_connections;
	int net_bind;
	private int sort_mem;
	private int shared_buffers;
	private boolean fsync;

	public int addPostgresDatabase(
		PostgresDatabaseName name,
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

	public PostgresDatabase getPostgresDatabase(PostgresDatabaseName name) throws IOException, SQLException {
		return table.connector.getPostgresDatabases().getPostgresDatabase(name, this);
	}

	public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
		return table.connector.getPostgresDatabases().getPostgresDatabases(this);
	}

	public PostgresServerUser getPostgresServerUser(PostgresUserId username) throws IOException, SQLException {
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
		try {
			pkey=result.getInt(1);
			name = PostgresServerName.valueOf(result.getString(2));
			ao_server=result.getInt(3);
			version=result.getInt(4);
			max_connections=result.getInt(5);
			net_bind=result.getInt(6);
			sort_mem=result.getInt(7);
			shared_buffers=result.getInt(8);
			fsync=result.getBoolean(9);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isPostgresDatabaseNameAvailable(PostgresDatabaseName name) throws IOException, SQLException {
		return table.connector.getPostgresDatabases().isPostgresDatabaseNameAvailable(name, this);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			name = PostgresServerName.valueOf(in.readUTF()).intern();
			ao_server=in.readCompressedInt();
			version=in.readCompressedInt();
			max_connections=in.readCompressedInt();
			net_bind=in.readCompressedInt();
			sort_mem=in.readCompressedInt();
			shared_buffers=in.readCompressedInt();
			fsync=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
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
		out.writeUTF(name.toString());
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
