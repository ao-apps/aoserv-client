/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Dumpable;
import com.aoindustries.aoserv.client.JdbcProvider;
import com.aoindustries.aoserv.client.NestedInputStream;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.io.ByteCountInputStream;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.IoUtils;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.Port;
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
 * @see  Encoding
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class Database extends CachedObjectIntegerKey<Database> implements Dumpable, Removable, JdbcProvider {

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
		table.getConnector().requestUpdate(false,
			AoservProtocol.CommandID.DUMP_POSTGRES_DATABASE,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
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
		table.getConnector().requestUpdate(false,
			AoservProtocol.CommandID.DUMP_POSTGRES_DATABASE,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
					masterOut.writeCompressedInt(pkey);
					masterOut.writeBoolean(gzip);
				}

				@Override
				public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
					long dumpSize = masterIn.readLong();
					if(dumpSize < -1) throw new IOException("dumpSize < -1: " + dumpSize);
					streamHandler.onDumpSize(dumpSize);
					long bytesRead;
					try (InputStream nestedIn = new NestedInputStream(masterIn)) {
						bytesRead = IoUtils.copy(nestedIn, streamHandler.getOut());
					}
					if(dumpSize != -1) {
						if(bytesRead < dumpSize) throw new IOException("Too few bytes read: " + bytesRead + " < " + dumpSize);
						if(bytesRead > dumpSize) throw new IOException("Too many bytes read: " + bytesRead + " > " + dumpSize);
					}
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
	protected Object getColumnImpl(int i) {
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

	public UserServer getDatDBA() throws SQLException, IOException {
		UserServer obj=table.getConnector().getPostgresql().getUserServer().get(datdba);
		if(obj==null) throw new SQLException("Unable to find PostgresServerUser: "+datdba);
		return obj;
	}

	@Override
	public String getJdbcDriver() {
		return JDBC_DRIVER;
	}

	@Override
	public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
		Server ps = getPostgresServer();
		com.aoindustries.aoserv.client.linux.Server ao = ps.getAoServer();
		StringBuilder jdbcUrl = new StringBuilder();
		jdbcUrl.append("jdbc:postgresql://");
		Bind nb = ps.getBind();
		IpAddress ip = nb.getIpAddress();
		InetAddress ia = ip.getInetAddress();
		if(ipOnly) {
			if(ia.isUnspecified()) {
				jdbcUrl.append(ao.getServer().getNetDevice(ao.getDaemonDeviceId().getName()).getPrimaryIPAddress().getInetAddress().toBracketedString());
			} else {
				jdbcUrl.append(ia.toBracketedString());
			}
		} else {
			if(ia.isUnspecified()) {
				jdbcUrl.append(ao.getHostname());
			} else if(ia.isLoopback()) {
				// Loopback as IP addresses to avoid ambiguity about which stack (IPv4/IPv6) used for "localhost" in Java
				jdbcUrl.append(ia.toBracketedString());
			} else {
				jdbcUrl.append(ip.getHostname());
			}
		}
		Port port = nb.getPort();
		if(!port.equals(Server.DEFAULT_PORT)) {
			jdbcUrl
				.append(':')
				.append(port.getPort());
		}
		jdbcUrl
			.append('/')
			.append(getName());
		return jdbcUrl.toString();
	}

	@Override
	public String getJdbcDocumentationUrl() throws SQLException, IOException {
		String version = getPostgresServer().getVersion().getTechnologyVersion(table.getConnector()).getVersion();
		return "https://aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
	}

	public PostgresDatabaseName getName() {
		return name;
	}

	public Encoding getPostgresEncoding() throws SQLException, IOException {
		Encoding obj=table.getConnector().getPostgresql().getEncoding().get(encoding);
		if(obj==null) throw new SQLException("Unable to find PostgresEncoding: "+encoding);
		// Make sure the postgres encoding postgresql version matches the server this database is part of
		if(
			obj.getPostgresVersion(table.getConnector()).getPkey()
			!= getPostgresServer().getVersion().getPkey()
		) {
			throw new SQLException("encoding/postgres server version mismatch on PostgresDatabase: #"+pkey);
		}

		return obj;
	}

	public Server getPostgresServer() throws SQLException, IOException {
		Server obj=table.getConnector().getPostgresql().getServer().get(postgres_server);
		if(obj==null) throw new SQLException("Unable to find PostgresServer: "+postgres_server);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_DATABASES;
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
	public List<CannotRemoveReason<Database>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<Database>> reasons=new ArrayList<>();

		Server ps=getPostgresServer();
		if(!allow_conn) reasons.add(new CannotRemoveReason<>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAoServer().getHostname(), this));
		if(is_template) reasons.add(new CannotRemoveReason<>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAoServer().getHostname(), this));
		if(
			name.equals(AOINDUSTRIES)
			|| name.equals(AOSERV)
			|| name.equals(AOWEB)
		) reasons.add(new CannotRemoveReason<>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAoServer().getHostname(), this));

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.POSTGRES_DATABASES,
			pkey
		);
	}

	@Override
	public String toStringImpl() {
		return name.toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		out.writeCompressedInt(postgres_server);
		out.writeCompressedInt(datdba);
		out.writeCompressedInt(encoding);
		out.writeBoolean(is_template);
		out.writeBoolean(allow_conn);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_27)>=0) out.writeBoolean(enable_postgis);
	}
}
