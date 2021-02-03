/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.collections.AoCollections;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.i18n.Resources;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.Port;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>MySQLServer</code> corresponds to a unique MySQL install
 * space on one server.  The server name must be unique per server.
 * <code>MySQLDatabase</code>s and <code>MySQLServerUser</code>s are
 * unique per <code>MySQLServer</code>.
 *
 * @see  Database
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends CachedObjectIntegerKey<Server> {

	private static final Resources RESOURCES = Resources.getResources(Server.class);

	/**
	 * Represents a name that may be used for a MySQL installation.  Names must:
	 * <ul>
	 *   <li>Be non-null</li>
	 *   <li>Be non-empty</li>
	 *   <li>Be between 1 and 255 characters</li>
	 *   <li>Must start with <code>[a-z,0-9]</code></li>
	 *   <li>The rest of the characters may contain [a-z], [0-9], period (.), hyphen (-), and underscore (_)</li>
	 * </ul>
	 *
	 * @author  AO Industries, Inc.
	 */
	final static public class Name implements
		Comparable<Name>,
		Serializable,
		DtoFactory<com.aoindustries.aoserv.client.dto.MySQLServerName>,
		Internable<Name>
	{

		private static final long serialVersionUID = 6148467549389988813L;

		public static final int MAX_LENGTH = 255;

		/**
		 * Validates a MySQL server name.
		 */
		public static ValidationResult validate(String name) {
			if(name==null) return new InvalidResult(RESOURCES, "Name.validate.isNull");
			int len = name.length();
			if(len==0) return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
			if(len > MAX_LENGTH) return new InvalidResult(RESOURCES, "Name.validate.tooLong", MAX_LENGTH, len);

			// The first character must be [a-z] or [0-9]
			char ch = name.charAt(0);
			if(
				(ch < 'a' || ch > 'z')
				&& (ch<'0' || ch>'9')
			) return new InvalidResult(RESOURCES, "Name.validate.startAtoZor0to9");

			// The rest may have additional characters
			for (int c = 1; c < len; c++) {
				ch = name.charAt(c);
				if (
					(ch<'a' || ch>'z')
					&& (ch<'0' || ch>'9')
					&& ch!='.'
					&& ch!='-'
					&& ch!='_'
				) return new InvalidResult(RESOURCES, "Name.validate.illegalCharacter");
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
				@SuppressWarnings("StringEquality")
				Name addMe = (name == internedName) ? this : new Name(internedName);
				existing = interned.putIfAbsent(internedName, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.MySQLServerName getDto() {
			return new com.aoindustries.aoserv.client.dto.MySQLServerName(name);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Constants">
	/**
	 * The default MySQL port.
	 */
	public static final Port DEFAULT_PORT;
	static {
		try {
			DEFAULT_PORT = Port.valueOf(3306, com.aoindustries.net.Protocol.TCP);
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * The supported versions of MySQL.
	 */
	public static final String
		VERSION_8_0_PREFIX = "8.0.",
		VERSION_5_7_PREFIX = "5.7.",
		VERSION_5_6_PREFIX = "5.6.",
		VERSION_5_1_PREFIX = "5.1.",
		VERSION_5_0_PREFIX = "5.0.",
		VERSION_4_1_PREFIX = "4.1.",
		VERSION_4_0_PREFIX = "4.0."
	;

	/**
	 * The directory that contains the MySQL data files.
	 */
	public static final PosixPath DATA_BASE_DIR;
	static {
		try {
			DATA_BASE_DIR = PosixPath.valueOf("/var/lib/mysql");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * Gets the versions of MySQL in order of
	 * preference.  Index <code>0</code> is the most
	 * preferred.
	 */
	public static final List<String> PREFERRED_VERSION_PREFIXES = Collections.unmodifiableList(
		Arrays.asList(
			VERSION_8_0_PREFIX,
			VERSION_5_7_PREFIX,
			VERSION_5_6_PREFIX,
			VERSION_5_1_PREFIX,
			VERSION_5_0_PREFIX,
			VERSION_4_1_PREFIX,
			VERSION_4_0_PREFIX
		)
	);

	/**
	 * @deprecated 2019-07-14: Is this still used?
	 */
	@Deprecated
	// TODO: Move to top-level class in schema, add to SQL implementation
	public enum ReservedWord {
		ACTION,
		ADD,
		AFTER,
		AGGREGATE,
		ALL,
		ALTER,
		AND,
		AS,
		ASC,
		AUTO_INCREMENT,
		AVG,
		AVG_ROW_LENGTH,
		BETWEEN,
		BIGINT,
		BINARY,
		BIT,
		BLOB,
		BOOL,
		BOTH,
		BY,
		CASCADE,
		CASE,
		CHANGE,
		CHAR,
		CHARACTER,
		CHECK,
		CHECKSUM,
		COLUMN,
		COLUMNS,
		COMMENT,
		CONSTRAINT,
		CREATE,
		CROSS,
		CURRENT_DATE,
		CURRENT_TIME,
		CURRENT_TIMESTAMP,
		DATA,
		DATABASE,
		DATABASES,
		DATE,
		DATETIME,
		DAY,
		DAY_HOUR,
		DAY_MINUTE,
		DAY_SECOND,
		DAYOFMONTH,
		DAYOFWEEK,
		DAYOFYEAR,
		DEC,
		DECIMAL,
		DEFAULT,
		DELAY_KEY_WRITE,
		DELAYED,
		DELETE,
		DESC,
		DESCRIBE,
		DISTINCT,
		DISTINCTROW,
		DOUBLE,
		DROP,
		ELSE,
		ENCLOSED,
		END,
		ENUM,
		ESCAPE,
		ESCAPED,
		EXISTS,
		EXPLAIN,
		FIELDS,
		FILE,
		FIRST,
		FLOAT,
		FLOAT4,
		FLOAT8,
		FLUSH,
		FOR,
		FOREIGN,
		FROM,
		FULL,
		FUNCTION,
		GLOBAL,
		GRANT,
		GRANTS,
		GROUP,
		HAVING,
		HEAP,
		HIGH_PRIORITY,
		HOSTS,
		HOUR,
		HOUR_MINUTE,
		HOUR_SECOND,
		IDENTIFIED,
		IF,
		IGNORE,
		IN,
		INDEX,
		INFILE,
		INNER,
		INSERT,
		INSERT_ID,
		INT,
		INT1,
		INT2,
		INT3,
		INT4,
		INT8,
		INTEGER,
		INTERVAL,
		INTO,
		IS,
		ISAM,
		JOIN,
		KEY,
		KEYS,
		KILL,
		LAST_INSERT_ID,
		LEADING,
		LEFT,
		LENGTH,
		LIKE,
		LIMIT,
		LINES,
		LOAD,
		LOCAL,
		LOCK,
		LOGS,
		LONG,
		LONGBLOB,
		LONGTEXT,
		LOW_PRIORITY,
		MATCH,
		MAX,
		MAX_ROWS,
		MEDIUMBLOB,
		MEDIUMINT,
		MEDIUMTEXT,
		MIDDLEINT,
		MIN_ROWS,
		MINUTE,
		MINUTE_SECOND,
		MODIFY,
		MONTH,
		MONTHNAME,
		MYISAM,
		NATURAL,
		NO,
		NOT,
		NULL,
		NUMERIC,
		ON,
		OPTIMIZE,
		OPTION,
		OPTIONALLY,
		OR,
		ORDER,
		OUTER,
		OUTFILE,
		PACK_KEYS,
		PARTIAL,
		PASSWORD,
		PRECISION,
		PRIMARY,
		PRIVILEGES,
		PROCEDURE,
		PROCESS,
		PROCESSLIST,
		READ,
		REAL,
		REFERENCES,
		REGEXP,
		RELOAD,
		RENAME,
		REPLACE,
		RESTRICT,
		RETURNS,
		REVOKE,
		RLIKE,
		ROW,
		ROWS,
		SECOND,
		SELECT,
		SET,
		SHOW,
		SHUTDOWN,
		SMALLINT,
		SONAME,
		SQL_BIG_RESULT,
		SQL_BIG_SELECTS,
		SQL_BIG_TABLES,
		SQL_LOG_OFF,
		SQL_LOG_UPDATE,
		SQL_LOW_PRIORITY_UPDATES,
		SQL_SELECT_LIMIT,
		SQL_SMALL_RESULT,
		SQL_WARNINGS,
		STARTING,
		STATUS,
		STRAIGHT_JOIN,
		STRING,
		TABLE,
		TABLES,
		TEMPORARY,
		TERMINATED,
		TEXT,
		THEN,
		TIME,
		TIMESTAMP,
		TINYBLOB,
		TINYINT,
		TINYTEXT,
		TO,
		TRAILING,
		TYPE,
		UNIQUE,
		UNLOCK,
		UNSIGNED,
		UPDATE,
		USAGE,
		USE,
		USING,
		VALUES,
		VARBINARY,
		VARCHAR,
		VARIABLES,
		VARYING,
		WHEN,
		WHERE,
		WITH,
		WRITE,
		YEAR,
		YEAR_MONTH,
		ZEROFILL;

		private static volatile Set<String> reservedWords = null;

		/**
		 * Case-insensitive check for if the provided string is a reserved word.
		 */
		public static boolean isReservedWord(String value) {
			Set<String> words = reservedWords;
			if(words==null) {
				ReservedWord[] values = values();
				words = AoCollections.newHashSet(values.length);
				for(ReservedWord word : values) {
					words.add(word.name().toUpperCase(Locale.ROOT));
				}
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
	public static final String COLUMN_AO_SERVER_name = "ao_server";
	public static final String COLUMN_NAME_name = "name";

	/**
	 * The maximum length of the name.
	 *
	 * @deprecated  Please use {@link Name#MAX_LENGTH} instead.
	 */
	@Deprecated
	public static final int MAX_SERVER_NAME_LENGTH = Name.MAX_LENGTH;

	private Name name;
	private int ao_server;
	private int version;
	private int max_connections;
	// Protocol conversion
	private Account.Name packageName;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_BIND: return pkey;
			case 1: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return version;
			case 4: return max_connections;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getBind_id() {
		return pkey;
	}

	public Bind getBind() throws SQLException, IOException {
		Bind nb = table.getConnector().getNet().getBind().get(pkey);
		if(nb == null) throw new SQLException("Unable to find NetBind: " + pkey);
		return nb;
	}

	public Name getName() {
		return name;
	}

	public int getAoServer_server_pkey() {
		return ao_server;
	}

	public com.aoindustries.aoserv.client.linux.Server getLinuxServer() throws SQLException, IOException {
		com.aoindustries.aoserv.client.linux.Server ao = table.getConnector().getLinux().getServer().get(ao_server);
		if(ao == null) throw new SQLException("Unable to find linux.Server: " + ao_server);
		return ao;
	}

	public SoftwareVersion getVersion() throws SQLException, IOException {
		SoftwareVersion obj=table.getConnector().getDistribution().getSoftwareVersion().get(version);
		if(obj==null) throw new SQLException("Unable to find TechnologyVersion: "+version);
		if(
			obj.getOperatingSystemVersion(table.getConnector()).getPkey()
			!= getLinuxServer().getHost().getOperatingSystemVersion_id()
		) {
			throw new SQLException("resource/operating system version mismatch on MySQLServer: #"+pkey);
		}
		return obj;
	}

	public int getMaxConnections() {
		return max_connections;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			name = Name.valueOf(result.getString(pos++));
			ao_server = result.getInt(pos++);
			version = result.getInt(pos++);
			max_connections = result.getInt(pos++);
			// Protocol conversion
			packageName = Account.Name.valueOf(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			name = Server.Name.valueOf(in.readUTF()).intern();
			ao_server = in.readCompressedInt();
			version = in.readCompressedInt();
			max_connections = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(version);
		out.writeCompressedInt(max_connections);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeCompressedInt(pkey);
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_28) >= 0) out.writeUTF(packageName.toString());
		}
	}

	public int addMySQLDatabase(
		Database.Name name,
		Package pack
	) throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabase().addMySQLDatabase(
			name,
			this,
			pack
		);
	}

	public PosixPath getDataDirectory() {
		try {
			return PosixPath.valueOf(DATA_BASE_DIR.toString() + '/' + name.toString());
		} catch(ValidationException e) {
			throw new AssertionError("Generated data directory should always be valid", e);
		}
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

	public Database getMySQLDatabase(Database.Name name) throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabase().getMySQLDatabase(name, this);
	}

	public List<MysqlReplication> getFailoverMySQLReplications() throws IOException, SQLException {
		return table.getConnector().getBackup().getMysqlReplication().getFailoverMySQLReplications(this);
	}

	public List<Database> getMySQLDatabases() throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabase().getMySQLDatabases(this);
	}

	public List<DatabaseUser> getMySQLDBUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabaseUser().getMySQLDBUsers(this);
	}

	public UserServer getMySQLServerUser(User.Name username) throws IOException, SQLException {
		return table.getConnector().getMysql().getUserServer().getMySQLServerUser(username, this);
	}

	public List<UserServer> getMySQLServerUsers() throws IOException, SQLException {
		return table.getConnector().getMysql().getUserServer().getMySQLServerUsers(this);
	}

	public List<User> getMySQLUsers() throws IOException, SQLException {
		List<UserServer> psu=getMySQLServerUsers();
		int len=psu.size();
		List<User> pu=new ArrayList<>(len);
		for(int c=0;c<len;c++) {
			pu.add(psu.get(c).getMySQLUser());
		}
		return pu;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MYSQL_SERVERS;
	}

	public boolean isMySQLDatabaseNameAvailable(Database.Name name) throws IOException, SQLException {
		return table.getConnector().getMysql().getDatabase().isMySQLDatabaseNameAvailable(name, this);
	}

	public void restartMySQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_MYSQL, pkey);
	}

	public void startMySQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_MYSQL, pkey);
	}

	public void stopMySQL() throws IOException, SQLException {
		table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_MYSQL, pkey);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return name+" on "+getLinuxServer().getHostname();
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
	 * Gets the master status or {@code null} if no master status provided by MySQL.  If any error occurs, throws either
	 * IOException or SQLException.
	 */
	public MasterStatus getMasterStatus() throws IOException, SQLException {
		return table.getConnector().requestResult(
			true,
			AoservProtocol.CommandID.GET_MYSQL_MASTER_STATUS,
			new AOServConnector.ResultRequest<MasterStatus>() {
				private MasterStatus result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.NEXT) {
						result = new MasterStatus(
							in.readNullUTF(),
							in.readNullUTF()
						);
					} else if(code==AoservProtocol.DONE) {
						result = null;
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public MasterStatus afterRelease() {
					return result;
				}
			}
		);
	}
}
