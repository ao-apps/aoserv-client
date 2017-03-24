/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.io.ByteCountInputStream;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.IoUtils;
import com.aoindustries.nio.charset.Charsets;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PostgresDatabase</code> corresponds to a unique PostgreSQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  PostgresEncoding
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabase extends CachedObjectIntegerKey<PostgresDatabase> implements Dumpable, Removable, JdbcProvider {

	static final int
		COLUMN_PKEY=0,
		COLUMN_POSTGRES_SERVER=2,
		COLUMN_DATDBA=3
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_POSTGRES_SERVER_name = "postgres_server";

	/**
	 * The classname of the JDBC driver used for the <code>PostgresDatabase</code>.
	 */
	public static final String JDBC_DRIVER="org.postgresql.Driver";

	/**
	 * Special databases.
	 */
	public static final PostgresDatabaseName
		AOINDUSTRIES,
		AOSERV,
		AOWEB,
		TEMPLATE0,
		TEMPLATE1
	;
	static {
		try {
			AOINDUSTRIES = PostgresDatabaseName.valueOf("aoindustries");
			AOSERV = PostgresDatabaseName.valueOf("aoserv");
			AOWEB = PostgresDatabaseName.valueOf("aoweb");
			TEMPLATE0 = PostgresDatabaseName.valueOf("template0");
			TEMPLATE1 = PostgresDatabaseName.valueOf("template1");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	PostgresDatabaseName name;
	int postgres_server;
	int datdba;
	private int encoding;
	private boolean is_template;
	private boolean allow_conn;
	private boolean enable_postgis;

	public boolean allowsConnections() {
		return allow_conn;
	}

	/**
	 * @see  #dump(java.io.Writer)
	 */
	@Override
	public void dump(PrintWriter out) throws IOException, SQLException {
		dump((Writer)out);
	}

	/**
	 * The character set used by the dumps.
	 */
	public static final Charset DUMP_ENCODING = Charsets.ISO_8859_1;

	/**
	 * Dumps the database into textual representation, not gzipped.
	 */
	public void dump(final Writer out) throws IOException, SQLException {
		table.connector.requestUpdate(
			false,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
					masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_POSTGRES_DATABASE.ordinal());
					masterOut.writeCompressedInt(pkey);
					masterOut.writeBoolean(false);
				}

				@Override
				public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
					long dumpSize = masterIn.readLong();
					if(dumpSize < 0) throw new IOException("dumpSize < 0: " + dumpSize);
					long bytesRead;
					try (
						ByteCountInputStream byteCountIn = new ByteCountInputStream(new NestedInputStream(masterIn));
						Reader nestedIn = new InputStreamReader(byteCountIn, DUMP_ENCODING)
					) {
						IoUtils.copy(nestedIn, out);
						bytesRead = byteCountIn.getCount();
					}
					if(bytesRead < dumpSize) throw new IOException("Too few bytes read: " + bytesRead + " < " + dumpSize);
					if(bytesRead > dumpSize) throw new IOException("Too many bytes read: " + bytesRead + " > " + dumpSize);
				}

				@Override
				public void afterRelease() {
				}
			}
		);
	}

	/**
	 * Dumps the database in {@link #DUMP_ENCODING} encoding into binary form, optionally gzipped.
	 */
	public void dump(
		final boolean gzip,
		final StreamHandler streamHandler
	) throws IOException, SQLException {
		table.connector.requestUpdate(
			false,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
					masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_POSTGRES_DATABASE.ordinal());
					masterOut.writeCompressedInt(pkey);
					masterOut.writeBoolean(gzip);
				}

				@Override
				public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
					long dumpSize = masterIn.readLong();
					if(dumpSize < 0) throw new IOException("dumpSize < 0: " + dumpSize);
					streamHandler.onDumpSize(dumpSize);
					long bytesRead;
					try (InputStream nestedIn = new NestedInputStream(masterIn)) {
						bytesRead = IoUtils.copy(nestedIn, streamHandler.getOut());
					}
					if(bytesRead < dumpSize) throw new IOException("Too few bytes read: " + bytesRead + " < " + dumpSize);
					if(bytesRead > dumpSize) throw new IOException("Too many bytes read: " + bytesRead + " > " + dumpSize);
				}

				@Override
				public void afterRelease() {
				}
			}
		);
	}

	/**
	 * Indicates that PostGIS should be enabled for this database.
	 */
	public boolean getEnablePostgis() {
		return enable_postgis;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case COLUMN_POSTGRES_SERVER: return postgres_server;
			case COLUMN_DATDBA: return datdba;
			case 4: return encoding;
			case 5: return is_template;
			case 6: return allow_conn;
			case 7: return enable_postgis;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public PostgresServerUser getDatDBA() throws SQLException, IOException {
		PostgresServerUser obj=table.connector.getPostgresServerUsers().get(datdba);
		if(obj==null) throw new SQLException("Unable to find PostgresServerUser: "+datdba);
		return obj;
	}

	@Override
	public String getJdbcDriver() {
		return JDBC_DRIVER;
	}

	@Override
	public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
		AOServer ao=getPostgresServer().getAOServer();
		return
			"jdbc:postgresql://"
			+ (ipOnly
			   ?ao.getServer().getNetDevice(ao.getDaemonDeviceID().getName()).getPrimaryIPAddress().getInetAddress().toBracketedString()
			   :ao.getHostname()
			)
			+ ':'
			+ getPostgresServer().getNetBind().getPort().getPort()
			+ '/'
			+ getName()
		;
	}

	@Override
	public String getJdbcDocumentationUrl() throws SQLException, IOException {
		String version=getPostgresServer().getPostgresVersion().getTechnologyVersion(table.connector).getVersion();
		return "https://aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
	}

	public PostgresDatabaseName getName() {
		return name;
	}

	public PostgresEncoding getPostgresEncoding() throws SQLException, IOException {
		PostgresEncoding obj=table.connector.getPostgresEncodings().get(encoding);
		if(obj==null) throw new SQLException("Unable to find PostgresEncoding: "+encoding);
		// Make sure the postgres encoding postgresql version matches the server this database is part of
		if(
			obj.getPostgresVersion(table.connector).getPkey()
			!= getPostgresServer().getPostgresVersion().getPkey()
		) {
			throw new SQLException("encoding/postgres server version mismatch on PostgresDatabase: #"+pkey);
		}

		return obj;
	}

	public PostgresServer getPostgresServer() throws SQLException, IOException {
		PostgresServer obj=table.connector.getPostgresServers().get(postgres_server);
		if(obj==null) throw new SQLException("Unable to find PostgresServer: "+postgres_server);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_DATABASES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			name = PostgresDatabaseName.valueOf(result.getString(2));
			postgres_server=result.getInt(3);
			datdba=result.getInt(4);
			encoding=result.getInt(5);
			is_template=result.getBoolean(6);
			allow_conn=result.getBoolean(7);
			enable_postgis=result.getBoolean(8);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isTemplate() {
		return is_template;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			name = PostgresDatabaseName.valueOf(in.readUTF());
			postgres_server=in.readCompressedInt();
			datdba=in.readCompressedInt();
			encoding=in.readCompressedInt();
			is_template=in.readBoolean();
			allow_conn=in.readBoolean();
			enable_postgis=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<PostgresDatabase>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<PostgresDatabase>> reasons=new ArrayList<>();

		PostgresServer ps=getPostgresServer();
		if(!allow_conn) reasons.add(new CannotRemoveReason<>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
		if(is_template) reasons.add(new CannotRemoveReason<>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
		if(
			name.equals(AOINDUSTRIES)
			|| name.equals(AOSERV)
			|| name.equals(AOWEB)
		) reasons.add(new CannotRemoveReason<>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.POSTGRES_DATABASES,
			pkey
		);
	}

	@Override
	String toStringImpl() {
		return name.toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		out.writeCompressedInt(postgres_server);
		out.writeCompressedInt(datdba);
		out.writeCompressedInt(encoding);
		out.writeBoolean(is_template);
		out.writeBoolean(allow_conn);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_27)>=0) out.writeBoolean(enable_postgis);
	}
}
