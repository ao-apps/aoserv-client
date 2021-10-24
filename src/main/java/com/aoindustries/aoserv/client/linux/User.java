/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.FastExternalizable;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.ftp.GuestUser;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class User extends CachedObjectUserNameKey<User> implements PasswordProtected, Removable, Disablable {

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, User.class);

	/**
	 * Used for the various user-provided fields in the <code>/etc/passwd</code> file.
	 * <p>
	 * <a href="https://wikipedia.org/wiki/Gecos_field">https://wikipedia.org/wiki/Gecos_field</a>
	 * </p>
	 *
	 * @author  AO Industries, Inc.
	 */
	public static final class Gecos implements
		Comparable<Gecos>,
		Serializable,
		DtoFactory<com.aoindustries.aoserv.client.dto.Gecos>,
		Internable<Gecos> {

		private static final long serialVersionUID = -117164942375352467L;

		public static final int MAX_LENGTH = 100;

		/**
		 * Determines if a name can be used as a GECOS field.  A GECOS field
		 * is valid if it is between 1 and 100 characters in length and uses only
		 * {@code [a-z,A-Z,0-9,-,_,@, ,.,#,=,/,$,%,^,&,*,(,),?,']} for each
		 * character.<br>
		 * <br>
		 * Refer to <code>man 5 passwd</code>
		 */
		public static ValidationResult validate(String value) {
			// Be non-null
			if(value==null) return new InvalidResult(RESOURCES, "Gecos.validate.isNull");
			int len = value.length();
			if(len==0) return new InvalidResult(RESOURCES, "Gecos.validate.isEmpty");
			if(len>MAX_LENGTH) return new InvalidResult(RESOURCES, "Gecos.validate.tooLong", MAX_LENGTH, len);

			for (int c = 0; c < len; c++) {
				char ch = value.charAt(c);
				if (
					(ch < 'a' || ch > 'z')
					&& (ch<'A' || ch>'Z')
					&& (ch < '0' || ch > '9')
					&& ch != '-'
					&& ch != '_'
					&& ch != '@'
					&& ch != ' '
					&& ch != '.'
					&& ch != '#'
					&& ch != '='
					&& ch != '/'
					&& ch != '$'
					&& ch != '%'
					&& ch != '^'
					&& ch != '&'
					&& ch != '*'
					&& ch != '('
					&& ch != ')'
					&& ch != '?'
					&& ch != '\''
					&& ch != '+'
				) return new InvalidResult(RESOURCES, "Gecos.validate.invalidCharacter", ch);
			}
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String, Gecos> interned = new ConcurrentHashMap<>();

		/**
		 * @param value  when {@code null}, returns {@code null}
		 */
		public static Gecos valueOf(String value) throws ValidationException {
			if(value == null) return null;
			//Gecos existing = interned.get(value);
			//return existing!=null ? existing : new Gecos(value);
			return new Gecos(value, true);
		}

		private final String value;

		private Gecos(String value, boolean validate) throws ValidationException {
			this.value = value;
			if(validate) validate();
		}

		/**
		 * @param  value  Does not validate, should only be used with a known valid value.
		 */
		private Gecos(String value) {
			ValidationResult result;
			assert (result = validate(value)).isValid() : result.toString();
			this.value = value;
		}

		private void validate() throws ValidationException {
			ValidationResult result = validate(value);
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
		public boolean equals(Object obj) {
			return
				(obj instanceof Gecos)
				&& value.equals(((Gecos)obj).value)
			;
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public int compareTo(Gecos other) {
			return this==other ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(value, other.value);
		}

		@Override
		public String toString() {
			return value;
		}

		/**
		 * Interns this id much in the same fashion as <code>String.intern()</code>.
		 *
		 * @see  String#intern()
		 */
		@Override
		public Gecos intern() {
			Gecos existing = interned.get(value);
			if(existing==null) {
				String internedValue = value.intern();
				@SuppressWarnings("StringEquality") // Using identity String comparison to see if already interned
				Gecos addMe = (value == internedValue) ? this : new Gecos(internedValue);
				existing = interned.putIfAbsent(internedValue, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.Gecos getDto() {
			return new com.aoindustries.aoserv.client.dto.Gecos(value);
		}
	}

	/**
	 * Represents a Linux username.  {@link User} names must:
	 * <ul>
	 *   <li>Be non-null</li>
	 *   <li>Be non-empty</li>
	 *   <li>Be between 1 and 32 characters</li>
	 *   <li>Must start with <code>[a-z]</code></li>
	 *   <li>Uses only ASCII 0x21 through 0x7f, excluding {@code space , : ( ) [ ] ' " | & ; A-Z \ /}</li>
	 *   <li>
	 *     If contains any @ symbol, must also be a valid email address.  Please note that the
	 *     reverse is not implied - email addresses may exist that are not valid user names.
	 *   </li>
	 *   <li>May not start with cyrus@</li>
	 *   <li>TODO: May only end on "$"?</li>
	 *   <li>TODO: "+" is allowed, "lost+found" should be specifically disallowed due to /home/lost+found on mount points.</li>
	 *   <li>Must be a valid {@link com.aoindustries.aoserv.client.account.User.Name} - this is implied by the above rules</li>
	 * </ul>
	 *
	 * @see Email#validate(java.lang.String)
	 *
	 * @author  AO Industries, Inc.
	 */
	// TODO: Update for IEEE Std 1003.1.2001 "3.426 User Name"? https://paulgorman.org/technical/presentations/linux_username_conventions.pdf
	// TODO: Rename "LinuxName" and combined with "GroupName" as "PosixName" (and an associated "PosixPortableFilename")?
	public static class Name extends com.aoindustries.aoserv.client.account.User.Name implements
		FastExternalizable
	{

		/**
		 * The maximum length of a Linux username.
		 * <p>
		 * <b>Implementation Note:</b><br>
		 * 32 characters
		 * </p>
		 */
		public static final int LINUX_NAME_MAX_LENGTH = 32;

		/**
		 * Validates a {@link User} name.
		 */
		public static ValidationResult validate(String name) {
			if(name==null) return new InvalidResult(RESOURCES, "Name.validate.isNull");
			int len = name.length();
			if(len==0) return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
			if(len > LINUX_NAME_MAX_LENGTH) return new InvalidResult(RESOURCES, "Name.validate.tooLong", LINUX_NAME_MAX_LENGTH, len);

			// The first character must be [a-z]
			char ch = name.charAt(0);
			if(ch < 'a' || ch > 'z') return new InvalidResult(RESOURCES, "Name.validate.startAToZ");

			// The rest may have additional characters
			boolean hasAt = false;
			for (int c = 1; c < len; c++) {
				ch = name.charAt(c);
				if(ch==' ') return new InvalidResult(RESOURCES, "Name.validate.noSpace");
				if(ch<=0x21 || ch>0x7f) return new InvalidResult(RESOURCES, "Name.validate.specialCharacter");
				if(ch>='A' && ch<='Z') return new InvalidResult(RESOURCES, "Name.validate.noCapital");
				switch(ch) {
					case ',' : return new InvalidResult(RESOURCES, "Name.validate.comma");
					case ':' : return new InvalidResult(RESOURCES, "Name.validate.colon");
					case '(' : return new InvalidResult(RESOURCES, "Name.validate.leftParen");
					case ')' : return new InvalidResult(RESOURCES, "Name.validate.rightParen");
					case '[' : return new InvalidResult(RESOURCES, "Name.validate.leftSquare");
					case ']' : return new InvalidResult(RESOURCES, "Name.validate.rightSquare");
					case '\'' : return new InvalidResult(RESOURCES, "Name.validate.apostrophe");
					case '"' : return new InvalidResult(RESOURCES, "Name.validate.quote");
					case '|' : return new InvalidResult(RESOURCES, "Name.validate.verticalBar");
					case '&' : return new InvalidResult(RESOURCES, "Name.validate.ampersand");
					case ';' : return new InvalidResult(RESOURCES, "Name.validate.semicolon");
					case '\\' : return new InvalidResult(RESOURCES, "Name.validate.backslash");
					case '/' : return new InvalidResult(RESOURCES, "Name.validate.slash");
					case '@' : hasAt = true; break;
				}
			}

			// More strict at sign control is required for user@domain structure in Cyrus virtdomains.
			if(hasAt) {
				// Must also be a valid email address
				ValidationResult result = Email.validate(name);
				if(!result.isValid()) return result;
				if(name.startsWith("cyrus@")) return new InvalidResult(RESOURCES, "Name.validate.startWithCyrusAt");
			}
			assert com.aoindustries.aoserv.client.account.User.Name.validate(name).isValid() : "A Linux User.Name is always a valid Account User.Name.";
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String, Name> interned = new ConcurrentHashMap<>();

		/**
		 * @param name  when {@code null}, returns {@code null}
		 */
		public static Name valueOf(String name) throws ValidationException {
			if(name == null) return null;
			//Name existing = interned.get(name);
			//return existing!=null ? existing : new Name(name);
			return new Name(name, true);
		}

		protected Name(String name, boolean validate) throws ValidationException {
			super(name, validate);
		}

		/**
		 * @param  name  Does not validate, should only be used with a known valid value.
		 */
		protected Name(String name) {
			super(name);
		}

		@Override
		protected void validate() throws ValidationException {
			ValidationResult result = validate(name);
			if(!result.isValid()) throw new ValidationException(result);
		}

		@Override
		public Name intern() {
			Name existing = interned.get(name);
			if(existing==null) {
				String internedId = name.intern();
				@SuppressWarnings("StringEquality")
				Name addMe = (name == internedId) ? this : new Name(internedId);
				existing = interned.putIfAbsent(internedId, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.LinuxUserName getDto() {
			return new com.aoindustries.aoserv.client.dto.LinuxUserName(name);
		}

		// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
		private static final long serialVersionUID = 2L;

		public Name() {
		}

		@Override
		public long getSerialVersionUID() {
			return serialVersionUID;
		}
		// </editor-fold>
	}

	static final int COLUMN_USERNAME=0;
	static final String COLUMN_USERNAME_name = "username";

	/**
	 * Some commonly used system and application account usernames.
	 */
	public static final Name
		ADM,
		AOADMIN,
		AOSERV_JILTER,
		AOSERV_XEN_MIGRATION,
		APACHE,
		AVAHI_AUTOIPD,
		AWSTATS,
		BIN,
		BIRD,
		CENTOS, // Amazon EC2 cloud-init
		CHRONY,
		CLAMSCAN,
		CLAMUPDATE,
		CYRUS,
		DAEMON,
		DBUS,
		DHCPD,
		EMAILMON,
		FTP,
		FTPMON,
		GAMES,
		HALT,
		INTERBASE,
		LP,
		MAIL,
		MAILNULL,
		MEMCACHED,
		MYSQL,
		NAMED,
		NFSNOBODY,
		NGINX,
		NOBODY,
		OPERATOR,
		POLKITD,
		POSTGRES,
		REDIS,
		ROOT,
		RPC,
		RPCUSER,
		SASLAUTH,
		SHUTDOWN,
		SMMSP,
		SSHD,
		SYNC,
		SYSTEMD_BUS_PROXY,
		SYSTEMD_NETWORK,
		TCPDUMP,
		TSS,
		UNBOUND,
		// AOServ Master:
		AOSERV_MASTER,
		// AOServ Schema:
		ACCOUNTING,
		BILLING,
		DISTRIBUTION,
		INFRASTRUCTURE,
		MANAGEMENT,
		MONITORING,
		RESELLER;

	/**
	 * @deprecated  User httpd no longer used.
	 */
	@Deprecated
	public static final Name HTTPD;
	static {
		try {
			ADM = Name.valueOf("adm");
			AOADMIN = Name.valueOf("aoadmin");
			AOSERV_JILTER = Name.valueOf("aoserv-jilter");
			AOSERV_XEN_MIGRATION = Name.valueOf("aoserv-xen-migration");
			APACHE = Name.valueOf("apache");
			AVAHI_AUTOIPD = Name.valueOf("avahi-autoipd");
			AWSTATS = Name.valueOf("awstats");
			BIN = Name.valueOf("bin");
			BIRD = Name.valueOf("bird");
			CENTOS = Name.valueOf("centos");
			CHRONY = Name.valueOf("chrony");
			CLAMSCAN = Name.valueOf("clamscan");
			CLAMUPDATE = Name.valueOf("clamupdate");
			CYRUS = Name.valueOf("cyrus");
			DAEMON = Name.valueOf("daemon");
			DBUS = Name.valueOf("dbus");
			DHCPD = Name.valueOf("dhcpd");
			EMAILMON = Name.valueOf("emailmon");
			FTP = Name.valueOf("ftp");
			FTPMON = Name.valueOf("ftpmon");
			GAMES = Name.valueOf("games");
			HALT = Name.valueOf("halt");
			INTERBASE = Name.valueOf("interbase");
			LP = Name.valueOf("lp");
			MAIL = Name.valueOf("mail");
			MAILNULL = Name.valueOf("mailnull");
			MEMCACHED = Name.valueOf("memcached");
			MYSQL = Name.valueOf("mysql");
			NAMED = Name.valueOf("named");
			NFSNOBODY = Name.valueOf("nfsnobody");
			NGINX = Name.valueOf("nginx");
			NOBODY = Name.valueOf("nobody");
			OPERATOR = Name.valueOf("operator");
			POLKITD = Name.valueOf("polkitd");
			POSTGRES = Name.valueOf("postgres");
			REDIS = Name.valueOf("redis");
			ROOT = Name.valueOf("root");
			RPC = Name.valueOf("rpc");
			RPCUSER = Name.valueOf("rpcuser");
			SASLAUTH = Name.valueOf("saslauth");
			SHUTDOWN = Name.valueOf("shutdown");
			SMMSP = Name.valueOf("smmsp");
			SSHD = Name.valueOf("sshd");
			SYNC = Name.valueOf("sync");
			SYSTEMD_BUS_PROXY = Name.valueOf("systemd-bus-proxy");
			SYSTEMD_NETWORK = Name.valueOf("systemd-network");
			TCPDUMP = Name.valueOf("tcpdump");
			TSS = Name.valueOf("tss");
			UNBOUND = Name.valueOf("unbound");
			// Now unused
			HTTPD = Name.valueOf("httpd");
			// AOServ Master:
			AOSERV_MASTER = Name.valueOf("aoserv-master");
			// AOServ Schema:
			ACCOUNTING = Name.valueOf("accounting");
			BILLING = Name.valueOf("billing");
			DISTRIBUTION = Name.valueOf("distribution");
			INFRASTRUCTURE = Name.valueOf("infrastructure");
			MANAGEMENT = Name.valueOf("management");
			MONITORING = Name.valueOf("monitoring");
			RESELLER = Name.valueOf("reseller");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	public static final String NO_PASSWORD_CONFIG_VALUE="!!";

	private Gecos name;
	private Gecos office_location;
	private Gecos office_phone;
	private Gecos home_phone;
	private String type;
	private PosixPath shell;
	private UnmodifiableTimestamp created;
	private int disable_log;

	public void addFTPGuestUser() throws IOException, SQLException {
		table.getConnector().getFtp().getGuestUser().addFTPGuestUser(pkey);
	}

	public void addLinuxGroup(Group group) throws IOException, SQLException {
		table.getConnector().getLinux().getGroupUser().addLinuxGroupAccount(group, this);
	}

	public int addLinuxServerAccount(Server aoServer, PosixPath home) throws IOException, SQLException {
		return table.getConnector().getLinux().getUserServer().addLinuxServerAccount(this, aoServer, home);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return com.aoindustries.aoserv.client.account.User.groupPasswordsSet(getLinuxServerAccounts());
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		// Already disabled
		if(disable_log!=-1) return false;

		// linux_server_accounts
		for(UserServer lsa : getLinuxServerAccounts()) {
			if(!lsa.isDisabled()) return false;
		}

		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getUsername().isDisabled();
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws IOException {
		return checkPassword(pkey, type, password);
	}

	/**
	 * Checks the strength of a password as required for this
	 * <code>LinuxAccount</code>.  The strength requirement
	 * depends on the <code>LinuxAccountType</code>.
	 *
	 * @see  UserType#getPasswordStrength(java.lang.String)
	 * @see  PasswordChecker#checkPassword(com.aoindustries.aoserv.client.account.User.Name, java.lang.String, com.aoindustries.aoserv.client.password.PasswordChecker.PasswordStrength)
	 */
	public static List<PasswordChecker.Result> checkPassword(Name username, String type, String password) throws IOException {
		return PasswordChecker.checkPassword(username, password, UserType.getPasswordStrength(type));
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.LINUX_ACCOUNTS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.LINUX_ACCOUNTS, pkey);
	}

	@Override
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_USERNAME: return pkey;
			case 1: return name;
			case 2: return office_location;
			case 3: return office_phone;
			case 4: return home_phone;
			case 5: return type;
			case 6: return shell;
			case 7: return created;
			case 8: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getCreated() {
		return created;
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public GuestUser getFTPGuestUser() throws IOException, SQLException {
		return table.getConnector().getFtp().getGuestUser().get(pkey);
	}

	public Gecos getHomePhone() {
		return home_phone;
	}

	public List<Group> getLinuxGroups() throws IOException, SQLException {
		return table.getConnector().getLinux().getGroupUser().getLinuxGroups(this);
	}

	public UserServer getLinuxServerAccount(Server aoServer) throws IOException, SQLException {
		return table.getConnector().getLinux().getUserServer().getLinuxServerAccount(aoServer, pkey);
	}

	public List<UserServer> getLinuxServerAccounts() throws IOException, SQLException {
		return table.getConnector().getLinux().getUserServer().getLinuxServerAccounts(this);
	}

	public Gecos getName() {
		return name;
	}

	public Gecos getOfficeLocation() {
		return office_location;
	}

	public Gecos getOfficePhone() {
		return office_phone;
	}

	public Group getPrimaryGroup() throws IOException, SQLException {
		return table.getConnector().getLinux().getGroupUser().getPrimaryGroup(this);
	}

	public Shell getShell() throws SQLException, IOException {
		Shell shellObject = table.getConnector().getLinux().getShell().get(shell);
		if (shellObject == null) throw new SQLException("Unable to find Shell: " + shell);
		return shellObject;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_ACCOUNTS;
	}

	public UserType getType() throws IOException, SQLException {
		UserType typeObject = table.getConnector().getLinux().getUserType().get(type);
		if (typeObject == null) throw new IllegalArgumentException(new SQLException("Unable to find LinuxAccountType: " + type));
		return typeObject;
	}

	public Name getUsername_id() {
		return pkey;
	}

	public com.aoindustries.aoserv.client.account.User getUsername() throws SQLException, IOException {
		com.aoindustries.aoserv.client.account.User usernameObject = table.getConnector().getAccount().getUser().get(pkey);
		if (usernameObject == null) throw new SQLException("Unable to find Username: " + pkey);
		return usernameObject;
	}

	public List<PosixPath> getValidHomeDirectories(Server ao) throws SQLException, IOException {
		return getValidHomeDirectories(pkey, ao);
	}

	public static List<PosixPath> getValidHomeDirectories(Name username, Server ao) throws SQLException, IOException {
		try {
			List<PosixPath> dirs=new ArrayList<>();
			if(username != null) {
				dirs.add(UserServer.getDefaultHomeDirectory(username));
				dirs.add(UserServer.getHashedHomeDirectory(username));
			}
			List<Site> hss=ao.getHttpdSites();
			int hsslen=hss.size();
			for(int c=0;c<hsslen;c++) {
				Site hs=hss.get(c);
				PosixPath siteDir=hs.getInstallDirectory();
				dirs.add(siteDir);
				if(hs.getHttpdTomcatSite()!=null) {
					dirs.add(PosixPath.valueOf(siteDir.toString() + "/webapps"));
				}
			}

			List<SharedTomcat> hsts=ao.getHttpdSharedTomcats();
			int hstslen=hsts.size();
			for(int c=0;c<hstslen;c++) {
				SharedTomcat hst=hsts.get(c);
				dirs.add(
					PosixPath.valueOf(
						hst.getLinuxServer().getHost().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
						+ '/' + hst.getName()
					)
				);
			}
			return dirs;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = Name.valueOf(result.getString(1));
			name = Gecos.valueOf(result.getString(2));
			office_location = Gecos.valueOf(result.getString(3));
			office_phone = Gecos.valueOf(result.getString(4));
			home_phone = Gecos.valueOf(result.getString(5));
			type = result.getString(6);
			shell = PosixPath.valueOf(result.getString(7));
			created = UnmodifiableTimestamp.valueOf(result.getTimestamp(8));
			disable_log=result.getInt(9);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = Name.valueOf(in.readUTF()).intern();
			name = Gecos.valueOf(in.readNullUTF());
			office_location = Gecos.valueOf(in.readNullUTF());
			office_phone = Gecos.valueOf(in.readNullUTF());
			home_phone = Gecos.valueOf(in.readNullUTF());
			type = in.readUTF().intern();
			shell = PosixPath.valueOf(in.readUTF()).intern();
			created = SQLStreamables.readUnmodifiableTimestamp(in);
			disable_log = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		// All LinuxServerAccounts must be removable
		for(UserServer lsa : getLinuxServerAccounts()) {
			reasons.addAll(lsa.getCannotRemoveReasons());
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.LINUX_ACCOUNTS,
			pkey
		);
	}

	public void removeLinuxGroup(Group group) throws IOException, SQLException {
		for(GroupUser lga : table.getConnector().getLinux().getGroupUser().getLinuxGroupAccounts(group.getName(), pkey)) {
			lga.remove();
		}
	}

	public void setHomePhone(Gecos phone) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone.toString());
	}

	public void setName(Gecos name) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.SET_LINUX_ACCOUNT_NAME,
			pkey,
			name==null ? "" : name.toString()
		);
	}

	public void setOfficeLocation(Gecos location) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location.toString());
	}

	public void setOfficePhone(Gecos phone) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone.toString());
	}

	@Override
	public void setPassword(String password) throws SQLException, IOException {
		for(UserServer lsa : getLinuxServerAccounts()) {
			if(lsa.canSetPassword()) lsa.setPassword(password);
		}
	}

	public void setShell(Shell shell) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_ACCOUNT_SHELL, pkey, shell.getPath());
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) < 0) {
			// Older clients require name, use "*" as name when none set
			out.writeUTF(name==null ? "*" : name.toString());
		} else {
			out.writeNullUTF(Objects.toString(name, null));
		}
		out.writeNullUTF(Objects.toString(office_location, null));
		out.writeNullUTF(Objects.toString(office_phone, null));
		out.writeNullUTF(Objects.toString(home_phone, null));
		out.writeUTF(type);
		out.writeUTF(shell.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(created.getTime());
		} else {
			SQLStreamables.writeTimestamp(created, out);
		}
		out.writeCompressedInt(disable_log);
	}

	@Override
	public boolean canSetPassword() throws IOException, SQLException {
		return disable_log==-1 && getType().canSetPassword();
	}

	public void setPrimaryLinuxGroup(Group group) throws SQLException, IOException {
		List<GroupUser> lgas = table.getConnector().getLinux().getGroupUser().getLinuxGroupAccounts(group.getName(), pkey);
		if(lgas.isEmpty()) throw new SQLException("Unable to find LinuxGroupAccount for username="+pkey+" and group="+group.getName());
		if(lgas.size() > 1) throw new SQLException("Found more than one LinuxGroupAccount for username="+pkey+" and group="+group.getName());
		lgas.get(0).setAsPrimary();
	}
}
