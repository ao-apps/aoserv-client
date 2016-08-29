/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.BufferManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
	public static final String
		AOINDUSTRIES="aoindustries",
		AOSERV="aoserv",
		AOWEB="aoweb",
		TEMPLATE0="template0",
		TEMPLATE1="template1"
	;

	/**
	 * The name of a database is limited by the internal data type of
	 * the <code>pg_database</code> table.  The type is <code>name</code>
	 * which has a maximum length of 31 characters.
	 */
	public static final int MAX_DATABASE_NAME_LENGTH=31;

	String name;
	int postgres_server;
	int datdba;
	private int encoding;
	private boolean is_template;
	private boolean allow_conn;
	private boolean enable_postgis;

	public boolean allowsConnections() {
		return allow_conn;
	}

	@Override
	public void dump(PrintWriter out) throws IOException, SQLException {
		dump((Writer)out);
	}

	public void dump(final Writer out) throws IOException, SQLException {
		table.connector.requestUpdate(
			false,
			new AOServConnector.UpdateRequest() {

				@Override
				public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
					masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_POSTGRES_DATABASE.ordinal());
					masterOut.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
					int code;
					byte[] buff=BufferManager.getBytes();
					try {
						char[] chars=BufferManager.getChars();
						try {
							while((code=masterIn.readByte())==AOServProtocol.NEXT) {
								int len=masterIn.readShort();
								masterIn.readFully(buff, 0, len);
								for(int c=0;c<len;c++) chars[c]=(char)buff[c]; // Assumes ISO8859-1 encoding
								out.write(chars, 0, len);
							}
						} finally {
							BufferManager.release(chars, false);
						}
					} finally {
						BufferManager.release(buff, false);
					}
					if(code!=AOServProtocol.DONE) {
						AOServProtocol.checkResult(code, masterIn);
						throw new IOException("Unexpected response code: "+code);
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
		return "https://www.aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
	}

	public String getName() {
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
		pkey=result.getInt(1);
		name=result.getString(2);
		postgres_server=result.getInt(3);
		datdba=result.getInt(4);
		encoding=result.getInt(5);
		is_template=result.getBoolean(6);
		allow_conn=result.getBoolean(7);
		enable_postgis=result.getBoolean(8);
	}

	public boolean isTemplate() {
		return is_template;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		name=in.readUTF();
		postgres_server=in.readCompressedInt();
		datdba=in.readCompressedInt();
		encoding=in.readCompressedInt();
		is_template=in.readBoolean();
		allow_conn=in.readBoolean();
		enable_postgis=in.readBoolean();
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason> reasons=new ArrayList<>();

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
		return name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
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
