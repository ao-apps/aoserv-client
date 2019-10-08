/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2014, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.mysql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Dumpable;
import com.aoindustries.aoserv.client.JdbcProvider;
import com.aoindustries.aoserv.client.NestedInputStream;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.monitoring.AlertLevel;
import static com.aoindustries.aoserv.client.mysql.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.io.ByteCountInputStream;
import com.aoindustries.io.IoUtils;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.Port;
import com.aoindustries.net.URIEncoder;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>MySQLDatabase</code> corresponds to a unique MySQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  DatabaseUser
 *
 * @author  AO Industries, Inc.
 */
final public class Database extends CachedObjectIntegerKey<Database> implements Removable, Dumpable, JdbcProvider {

	/**
	 * Represents a name that may be used for a {@link Database}.  Database names must:
	 * <ul>
	 *   <li>Be non-null</li>
	 *   <li>Be non-empty</li>
	 *   <li>Be between 1 and 64 characters</li>
	 *   <li>Characters may contain <code>[a-z,A-Z,0-9,_,-,.,(space)]</code></li>
	 * </ul>
	 *
	 * @author  AO Industries, Inc.
	 */
	final static public class Name implements
		Comparable<Name>,
		Serializable,
		ObjectInputValidation,
		DtoFactory<com.aoindustries.aoserv.client.dto.MySQLDatabaseName>,
		Internable<Name>
	{

		private static final long serialVersionUID = 1495532864586195961L;

		/**
		 * The longest name allowed for a MySQL database.
		 */
		public static final int MAX_LENGTH = 64;

		/**
		 * Validates a MySQL database name.
		 */
		public static ValidationResult validate(String name) {
			if(name==null) return new InvalidResult(accessor, "Database.Name.validate.isNull");
			int len = name.length();
			if(len==0) return new InvalidResult(accessor, "Database.Name.validate.isEmpty");
			if(len > MAX_LENGTH) return new InvalidResult(accessor, "Database.Name.validate.tooLong", MAX_LENGTH, len);

			// Characters may contain [a-z,A-Z,0-9,_,-,.]
			for (int c = 0; c < len; c++) {
				char ch = name.charAt(c);
				if (
					(ch<'a' || ch>'z')
					&& (ch < 'A' || ch > 'Z')
					&& (ch < '0' || ch > '9')
					&& ch != '_'
					&& ch != '-'
					&& ch != '.'
					&& ch != ' '
				) return new InvalidResult(accessor, "Database.Name.validate.illegalCharacter");
			}
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String,Name> interned = new ConcurrentHashMap<>();

		/**
		 * @param name  when {@code null}, returns {@code null}
		 */
		public static Name valueOf(String name) throws ValidationException {
			if(name == null) return null;
			//Name existing = interned.get(name);
			//return existing!=null ? existing : new Name(name);
			return new Name(name, true);
		}

		final private String name;

		private Name(String name, boolean validate) throws ValidationException {
			this.name = name;
			if(validate) validate();
		}

		/**
		 * @param  name  Does not validate, should only be used with a known valid value.
		 */
		private Name(String name) {
			ValidationResult result;
			assert (result = validate(name)).isValid() : result.toString();
			this.name = name;
		}

		private void validate() throws ValidationException {
			ValidationResult result = validate(name);
			if(!result.isValid()) throw new ValidationException(result);
		}

		/**
		 * Perform same validation as constructor on readObject.
		 */
		private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			validateObject();
		}

		@Override
		public void validateObject() throws InvalidObjectException {
			try {
				validate();
			} catch(ValidationException err) {
				InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
				newErr.initCause(err);
				throw newErr;
			}
		}

		@Override
		public boolean equals(Object O) {
			return
				O!=null
				&& O instanceof Name
				&& name.equals(((Name)O).name)
			;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public int compareTo(Name other) {
			return this==other ? 0 : name.compareTo(other.name);
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Interns this name much in the same fashion as <code>String.intern()</code>.
		 *
		 * @see  String#intern()
		 */
		@Override
		public Name intern() {
			Name existing = interned.get(name);
			if(existing==null) {
				String internedName = name.intern();
				Name addMe = (name == internedName) ? this : new Name(internedName);
				existing = interned.putIfAbsent(internedName, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.MySQLDatabaseName getDto() {
			return new com.aoindustries.aoserv.client.dto.MySQLDatabaseName(name);
		}
	}

	static final int
		COLUMN_PKEY = 0,
		COLUMN_MYSQL_SERVER = 2,
		COLUMN_PACKAGE = 3
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

	/**
	 * The classname of the JDBC driver used for the <code>MySQLDatabase</code>.
	 */
	public static final String
		REDHAT_JDBC_DRIVER="com.mysql.jdbc.Driver",
		MANDRAKE_JDBC_DRIVER="com.mysql.jdbc.Driver",
		CENTOS_JDBC_DRIVER="com.mysql.jdbc.Driver",
		CENTOS_7_JDBC_DRIVER="com.mysql.cj.jdbc.Driver"
	;

	/**
	 * The URL for MySQL JDBC documentation.
	 */
	public static final String
		REDHAT_JDBC_DOCUMENTATION_URL="https://dev.mysql.com/doc/connector-j/5.1/en/",
		MANDRAKE_JDBC_DOCUMENTATION_URL="https://dev.mysql.com/doc/connector-j/5.1/en/",
		CENTOS_JDBC_DOCUMENTATION_URL="https://dev.mysql.com/doc/connector-j/5.1/en/",
		CENTOS_7_JDBC_DOCUMENTATION_URL="https://dev.mysql.com/doc/connector-j/8.0/en/"
	;

	public static final Name
		/** The root database for a MySQL installation. */
		MYSQL,
		/** MySQL */
		INFORMATION_SCHEMA,
		PERFORMANCE_SCHEMA,
		SYS,
		/** Monitoring */
		MYSQLMON
	;

	static {
		try {
			// The root database for a MySQL installation.
			MYSQL = Name.valueOf("mysql");
			// MySQL
			INFORMATION_SCHEMA = Name.valueOf("information_schema");
			PERFORMANCE_SCHEMA = Name.valueOf("performance_schema");
			SYS = Name.valueOf("sys");
			// Monitoring
			MYSQLMON = Name.valueOf("mysqlmon");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Special MySQL databases may not be added or removed.
	 */
	public static boolean isSpecial(Name name) {
		return
			// The root database for a MySQL installation.
			name.equals(MYSQL)
			// MySQL
			|| name.equals(INFORMATION_SCHEMA)
			|| name.equals(PERFORMANCE_SCHEMA)
			|| name.equals(SYS)
			// Monitoring
			|| name.equals(MYSQLMON);
	}

	private Name name;
	private int mysql_server;
	private Account.Name packageName;
	private AlertLevel maxCheckTableAlertLevel;

	/**
	 * @deprecated  Please call {@link DatabaseUserTable#addMySQLDBUser(com.aoindustries.aoserv.client.mysql.Database, com.aoindustries.aoserv.client.mysql.UserServer, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)} directly
	 */
	@Deprecated
	public int addMySQLServerUser(
		UserServer msu,
		boolean canSelect,
		boolean canInsert,
		boolean canUpdate,
		boolean canDelete,
		boolean canCreate,
		boolean canDrop,
		boolean canReference,
		boolean canIndex,
		boolean canAlter,
		boolean canCreateTempTable,
		boolean canLockTables,
		boolean canCreateView,
		boolean canShowView,
		boolean canCreateRoutine,
		boolean canAlterRoutine,
		boolean canExecute,
		boolean canEvent,
		boolean canTrigger
	) throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabaseUser().addMySQLDBUser(
			this,
			msu,
			canSelect,
			canInsert,
			canUpdate,
			canDelete,
			canCreate,
			canDrop,
			canReference,
			canIndex,
			canAlter,
			canCreateTempTable,
			canLockTables,
			canCreateView,
			canShowView,
			canCreateRoutine,
			canAlterRoutine,
			canExecute,
			canEvent,
			canTrigger
		);
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
	public static final Charset DUMP_ENCODING = StandardCharsets.UTF_8;

	/**
	 * Dumps the database into textual representation, not gzipped.
	 */
	public void dump(final Writer out) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			false,
			AoservProtocol.CommandID.DUMP_MYSQL_DATABASE,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(StreamableOutput masterOut) throws IOException {
					masterOut.writeCompressedInt(pkey);
					masterOut.writeBoolean(false);
				}

				@Override
				public void readResponse(StreamableInput masterIn) throws IOException, SQLException {
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
		table.getConnector().requestUpdate(
			false,
			AoservProtocol.CommandID.DUMP_MYSQL_DATABASE,
			new AOServConnector.UpdateRequest() {
				@Override
				public void writeRequest(StreamableOutput masterOut) throws IOException {
					masterOut.writeCompressedInt(pkey);
					masterOut.writeBoolean(gzip);
				}

				@Override
				public void readResponse(StreamableInput masterIn) throws IOException, SQLException {
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

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case COLUMN_MYSQL_SERVER: return mysql_server;
			case COLUMN_PACKAGE: return packageName;
			case 4 : return maxCheckTableAlertLevel.name();
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public String getJdbcDriver() throws SQLException, IOException {
		int osv = getMySQLServer().getLinuxServer().getHost().getOperatingSystemVersion_id();
		switch(osv) {
			case OperatingSystemVersion.MANDRIVA_2006_0_I586 :
				return MANDRAKE_JDBC_DRIVER;
			case OperatingSystemVersion.REDHAT_ES_4_X86_64 :
				return REDHAT_JDBC_DRIVER;
			case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64:
				return CENTOS_JDBC_DRIVER;
			case OperatingSystemVersion.CENTOS_7_X86_64:
				return CENTOS_7_JDBC_DRIVER;
			default : throw new SQLException("Unsupported OperatingSystemVersion: " + osv);
		}
	}

	@Override
	public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
		Server ms = getMySQLServer();
		com.aoindustries.aoserv.client.linux.Server ao = ms.getLinuxServer();
		StringBuilder jdbcUrl = new StringBuilder();
		jdbcUrl.append("jdbc:mysql://");
		Bind nb = ms.getBind();
		IpAddress ip = nb.getIpAddress();
		InetAddress ia = ip.getInetAddress();
		if(ipOnly) {
			if(ia.isUnspecified()) {
				jdbcUrl.append(ao.getHost().getNetDevice(ao.getDaemonDeviceId().getName()).getPrimaryIPAddress().getInetAddress().toBracketedString());
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
		jdbcUrl.append('/');
		URIEncoder.encodeURIComponent(getName().toString(), jdbcUrl);
		return jdbcUrl.toString();
	}

	@Override
	public String getJdbcDocumentationUrl() throws SQLException, IOException {
		int osv = getMySQLServer().getLinuxServer().getHost().getOperatingSystemVersion_id();
		switch(osv) {
			case OperatingSystemVersion.MANDRIVA_2006_0_I586 :
				return MANDRAKE_JDBC_DOCUMENTATION_URL;
			case OperatingSystemVersion.REDHAT_ES_4_X86_64 :
				return REDHAT_JDBC_DOCUMENTATION_URL;
			case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 :
				return CENTOS_JDBC_DOCUMENTATION_URL;
			case OperatingSystemVersion.CENTOS_7_X86_64 :
				return CENTOS_7_JDBC_DOCUMENTATION_URL;
			default : throw new SQLException("Unsupported OperatingSystemVersion: " + osv);
		}
	}

	public DatabaseUser getMySQLDBUser(UserServer msu) throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabaseUser().getMySQLDBUser(this, msu);
	}

	public List<DatabaseUser> getMySQLDBUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabaseUser().getMySQLDBUsers(this);
	}

	public List<UserServer> getMySQLServerUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabaseUser().getMySQLServerUsers(this);
	}

	public Name getName() {
		return name;
	}

	public boolean isSpecial() {
		return isSpecial(name);
	}

	public Account.Name getPackage_name() {
		return packageName;
	}

	public Package getPackage() throws SQLException, IOException {
		Package obj=table.getConnector().getBilling().getPackage().get(packageName);
		if(obj==null) throw new SQLException("Unable to find Package: "+packageName);
		return obj;
	}

	public int getMySQLServer_id() {
		return mysql_server;
	}

	public Server getMySQLServer() throws SQLException, IOException {
		Server obj = table.getConnector().getMysql().getServer().get(mysql_server);
		if(obj == null) throw new SQLException("Unable to find MySQLServer: " + mysql_server);
		return obj;
	}

	public AlertLevel getMaxCheckTableAlertLevel() {
		return maxCheckTableAlertLevel;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_DATABASES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			name = Name.valueOf(result.getString(2));
			mysql_server = result.getInt(3);
			packageName = Account.Name.valueOf(result.getString(4));
			maxCheckTableAlertLevel = AlertLevel.valueOf(result.getString(5));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			name = Name.valueOf(in.readUTF());
			mysql_server = in.readCompressedInt();
			packageName = Account.Name.valueOf(in.readUTF()).intern();
			maxCheckTableAlertLevel = AlertLevel.valueOf(in.readCompressedUTF());
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<Database>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<Database>> reasons=new ArrayList<>();
		if(isSpecial()) {
			Server ms = getMySQLServer();
			reasons.add(
				new CannotRemoveReason<>(
					"Not allowed to drop a special MySQL database: "
						+ name
						+ " on "
						+ ms.getName()
						+ " on "
						+ ms.getLinuxServer().getHostname(),
					this
				)
			);
		}
		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		if(isSpecial()) throw new SQLException("Refusing to remove special MySQL database: " + this);
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.MYSQL_DATABASES,
			pkey
		);
	}

	@Override
	public String toStringImpl() {
		return name.toString();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)<0) out.writeCompressedInt(-1);
		else out.writeCompressedInt(mysql_server);
		out.writeUTF(packageName.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_74) >= 0) {
			out.writeCompressedUTF(maxCheckTableAlertLevel.name());
		}
	}

	public enum Engine {
		CSV,
		MyISAM,
		InnoDB,
		HEAP,
		MEMORY,
		PERFORMANCE_SCHEMA
	}

	// TODO: Pull-out into outer class "Table", with TableName inside it was "Name"
	// TODO: Also make a full-on AOServTable?
	public static class TableStatus {

		// TODO: How to handle unknown versions over protocols by time?  An "UNKNOWN" that new are converted to, with a "sinceVersion" and "lastVersion" settings?
		// TODO: This would be a general solution for enums that may be extended with older clients still in use.
		public enum RowFormat {
			Compact,
			Dynamic,
			Fixed
		}

		public enum Collation {
			binary,
			latin1_swedish_ci,
			utf8_bin,
			utf8_general_ci,
			utf8_unicode_ci,
			utf8mb4_unicode_ci,
			utf8mb4_unicode_520_ci
		}

		private final Table_Name name;
		private final Engine engine;
		private final Integer version;
		private final RowFormat rowFormat;
		private final Long rows;
		private final Long avgRowLength;
		private final Long dataLength;
		private final Long maxDataLength;
		private final Long indexLength;
		private final Long dataFree;
		private final Long autoIncrement;
		private final String createTime;
		private final String updateTime;
		private final String checkTime;
		private final Collation collation;
		private final String checksum;
		private final String createOptions;
		private final String comment;

		public TableStatus(
			Table_Name name,
			Engine engine,
			Integer version,
			RowFormat rowFormat,
			Long rows,
			Long avgRowLength,
			Long dataLength,
			Long maxDataLength,
			Long indexLength,
			Long dataFree,
			Long autoIncrement,
			String createTime,
			String updateTime,
			String checkTime,
			Collation collation,
			String checksum,
			String createOptions,
			String comment
		) {
			this.name = name;
			this.engine = engine;
			this.version = version;
			this.rowFormat = rowFormat;
			this.rows = rows;
			this.avgRowLength = avgRowLength;
			this.dataLength = dataLength;
			this.maxDataLength = maxDataLength;
			this.indexLength = indexLength;
			this.dataFree = dataFree;
			this.autoIncrement = autoIncrement;
			this.createTime = createTime;
			this.updateTime = updateTime;
			this.checkTime = checkTime;
			this.collation = collation;
			this.checksum = checksum;
			this.createOptions = createOptions;
			this.comment = comment;
		}

		/**
		 * @return the name
		 */
		public Table_Name getName() {
			return name;
		}

		/**
		 * @return the engine
		 */
		public Engine getEngine() {
			return engine;
		}

		/**
		 * @return the version
		 */
		public Integer getVersion() {
			return version;
		}

		/**
		 * @return the rowFormat
		 */
		public RowFormat getRowFormat() {
			return rowFormat;
		}

		/**
		 * @return the rows
		 */
		public Long getRows() {
			return rows;
		}

		/**
		 * @return the avgRowLength
		 */
		public Long getAvgRowLength() {
			return avgRowLength;
		}

		/**
		 * @return the dataLength
		 */
		public Long getDataLength() {
			return dataLength;
		}

		/**
		 * @return the maxDataLength
		 */
		public Long getMaxDataLength() {
			return maxDataLength;
		}

		/**
		 * @return the indexLength
		 */
		public Long getIndexLength() {
			return indexLength;
		}

		/**
		 * @return the dataFree
		 */
		public Long getDataFree() {
			return dataFree;
		}

		/**
		 * @return the autoIncrement
		 */
		public Long getAutoIncrement() {
			return autoIncrement;
		}

		/**
		 * @return the createTime
		 */
		public String getCreateTime() {
			return createTime;
		}

		/**
		 * @return the updateTime
		 */
		public String getUpdateTime() {
			return updateTime;
		}

		/**
		 * @return the checkTime
		 */
		public String getCheckTime() {
			return checkTime;
		}

		/**
		 * @return the collation
		 */
		public Collation getCollation() {
			return collation;
		}

		/**
		 * @return the checksum
		 */
		public String getChecksum() {
			return checksum;
		}

		/**
		 * @return the createOptions
		 */
		public String getCreateOptions() {
			return createOptions;
		}

		/**
		 * @return the comment
		 */
		public String getComment() {
			return comment;
		}
	}

	/**
	 * Gets the table status on the master server.
	 */
	public List<TableStatus> getTableStatus() throws IOException, SQLException {
		return getTableStatus(null);
	}

	/**
	 * Gets the table status on the master server or provided slave server.
	 */
	public List<TableStatus> getTableStatus(final MysqlReplication mysqlSlave) throws IOException, SQLException {
		return table.getConnector().requestResult(
			true,
			AoservProtocol.CommandID.GET_MYSQL_TABLE_STATUS,
			new AOServConnector.ResultRequest<List<TableStatus>>() {
				private List<TableStatus> result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(mysqlSlave==null ? -1 : mysqlSlave.getPkey());
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.NEXT) {
						int size = in.readCompressedInt();
						List<TableStatus> tableStatuses = new ArrayList<>(size);
						for(int c=0;c<size;c++) {
							try {
								tableStatuses.add(
									new TableStatus(
										Table_Name.valueOf(in.readUTF()), // name
										in.readNullEnum(Engine.class), // engine
										in.readNullInteger(), // version
										in.readNullEnum(TableStatus.RowFormat.class), // rowFormat
										in.readNullLong(), // rows
										in.readNullLong(), // avgRowLength
										in.readNullLong(), // dataLength
										in.readNullLong(), // maxDataLength
										in.readNullLong(), // indexLength
										in.readNullLong(), // dataFree
										in.readNullLong(), // autoIncrement
										in.readNullUTF(), // createTime
										in.readNullUTF(), // updateTime
										in.readNullUTF(), // checkTime
										in.readNullEnum(TableStatus.Collation.class), // collation
										in.readNullUTF(), // checksum
										in.readNullUTF(), // createOptions
										in.readNullUTF() // comment
									)
								);
							} catch(ValidationException e) {
								throw new IOException(e);
							}
						}
						this.result = tableStatuses;
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public List<TableStatus> afterRelease() {
					return result;
				}
			}
		);
	}

	public static class CheckTableResult {

		public enum MsgType {
			status,
			error,
			info,
			warning,
			// From MySQL 5.1
			note,
			Error
		}

		private final Table_Name table;
		private final long duration;
		private final MsgType msgType;
		private final String msgText;

		public CheckTableResult(
			Table_Name table,
			long duration,
			MsgType msgType,
			String msgText
		) {
			this.table = table;
			this.duration = duration;
			this.msgType = msgType;
			this.msgText = msgText;
		}

		/**
		 * @return the table
		 */
		public Table_Name getTable() {
			return table;
		}

		/**
		 * @return the duration
		 */
		public long getDuration() {
			return duration;
		}

		/**
		 * @return the msgType
		 */
		public MsgType getMsgType() {
			return msgType;
		}

		/**
		 * @return the msgText
		 */
		public String getMsgText() {
			return msgText;
		}
	}

	/**
	 * Gets the table status on the master server.
	 */
	public List<CheckTableResult> checkTables(final Collection<Table_Name> tableNames) throws IOException, SQLException {
		return checkTables(null, tableNames);
	}

	/**
	 * Gets the table status on the master server or provided slave server.
	 */
	public List<CheckTableResult> checkTables(final MysqlReplication mysqlSlave, final Collection<Table_Name> tableNames) throws IOException, SQLException {
		if(tableNames.isEmpty()) return Collections.emptyList();
		return table.getConnector().requestResult(
			true,
			AoservProtocol.CommandID.CHECK_MYSQL_TABLES,
			new AOServConnector.ResultRequest<List<CheckTableResult>>() {
				private List<CheckTableResult> result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(mysqlSlave==null ? -1 : mysqlSlave.getPkey());
					int size = tableNames.size();
					out.writeCompressedInt(size);
					int count = 0;
					Iterator<Table_Name> iter = tableNames.iterator();
					while(count<size && iter.hasNext()) {
						out.writeUTF(iter.next().toString());
						count++;
					}
					if(count!=size) throw new ConcurrentModificationException("count!=size");
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.NEXT) {
						int size = in.readCompressedInt();
						List<CheckTableResult> checkTableResults = new ArrayList<>(size);
						for(int c=0;c<size;c++) {
							try {
								checkTableResults.add(
									new CheckTableResult(
										Table_Name.valueOf(in.readUTF()), // table
										in.readLong(), // duration
										in.readNullEnum(CheckTableResult.MsgType.class), // msgType
										in.readNullUTF() // msgText
									)
								);
							} catch(ValidationException e) {
								throw new IOException(e);
							}
						}
						this.result = checkTableResults;
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public List<CheckTableResult> afterRelease() {
					return result;
				}
			}
		);
	}

	/**
	 * Determines if a name is safe for use as a table/column name, the name identifier
	 * should be enclosed with backticks (`).
	 *
	 * @deprecated  Use {@link Name#validate(java.lang.String) instead}
	 */
	@Deprecated
	public static boolean isSafeName(String name) {
		return Name.validate(name).isValid();
	}
}
