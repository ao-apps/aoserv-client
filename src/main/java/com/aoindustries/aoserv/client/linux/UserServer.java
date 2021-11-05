/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.linux;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.email.AttachmentBlock;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.InboxAddress;
import com.aoindustries.aoserv.client.email.InboxAttributes;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.email.SpamAssassinMode;
import com.aoindustries.aoserv.client.ftp.PrivateServer;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>LinuxServerAccount</code> grants a <code>LinuxAccount</code>
 * access to a {@link Server}.
 *
 * @see  User
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class UserServer extends CachedObjectIntegerKey<UserServer> implements Removable, PasswordProtected, Disablable {

	/**
	 * The UID of the root user.
	 *
	 * Note: Copied from PosixFile.java to avoid interproject dependency.
	 */
	public static final int ROOT_UID = 0;

	static final int
		COLUMN_PKEY=0,
		COLUMN_USERNAME=1,
		COLUMN_AO_SERVER=2
	;
	public static final String COLUMN_USERNAME_name = "username";
	public static final String COLUMN_AO_SERVER_name = "ao_server";

	/**
	 * The default number of days email messages will be kept in the "Trash" folder.
	 */
	public static final int DEFAULT_TRASH_EMAIL_RETENTION = 31;

	/**
	 * The default number of days email messages will be kept in the "Junk" folder.
	 */
	public static final int DEFAULT_JUNK_EMAIL_RETENTION = 31;

	/**
	 * The default SpamAssassin required score.
	 */
	public static final float DEFAULT_SPAM_ASSASSIN_REQUIRED_SCORE = 3.0F;

	/**
	 * The default SpamAssassin discard score.
	 */
	public static final int DEFAULT_SPAM_ASSASSIN_DISCARD_SCORE = 20;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public UserServer() {
		// Do nothing
	}

	private User.Name username;
	private int ao_server;
	private LinuxId uid;
	private PosixPath home;
	private int autoresponder_from;
	private String autoresponder_subject;
	private String autoresponder_path;
	private boolean is_autoresponder_enabled;
	private int disable_log;
	private String predisable_password;
	private UnmodifiableTimestamp created;
	private boolean use_inbox;
	private int trash_email_retention;
	private int junk_email_retention;
	private String sa_integration_mode;
	private float sa_required_score;
	private int sa_discard_score;
	private String sudo;

	@Override
	public boolean canDisable() throws IOException, SQLException {
		// already disabled
		if(disable_log!=-1) return false;

		// is a system user
		if(uid.compareTo(getServer().getUidMin()) < 0) return false;

		// cvs_repositories
		for(CvsRepository cr : getCvsRepositories()) {
			if(!cr.isDisabled()) return false;
		}

		// httpd_shared_tomcats
		for(SharedTomcat hst : getHttpdSharedTomcats()) {
			if(!hst.isDisabled()) return false;
		}

		// email_lists
		for(com.aoindustries.aoserv.client.email.List el : getEmailLists()) {
			if(!el.isDisabled()) return false;
		}

		// httpd_sites
		for(Site hs : getHttpdSites()) {
			if(!hs.isDisabled()) return false;
		}

		return true;
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getLinuxAccount().isDisabled();
	}

	@Override
	public List<PasswordChecker.Result> checkPassword(String password) throws SQLException, IOException {
		return getLinuxAccount().checkPassword(password);
	}

	public long copyHomeDirectory(final Server toServer) throws IOException, SQLException {
		return table.getConnector().requestResult(
			false,
			AoservProtocol.CommandID.COPY_HOME_DIRECTORY,
			// Java 9: new AOServConnector.ResultRequest<>
			new AOServConnector.ResultRequest<Long>() {
				private long result;
				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(toServer.getPkey());
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code!=AoservProtocol.DONE) {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
					result = in.readLong();
				}

				@Override
				public Long afterRelease() {
					return result;
				}
			}
		);
	}

	public void copyPassword(UserServer other) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.getPkey());
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.LINUX_SERVER_ACCOUNTS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.LINUX_SERVER_ACCOUNTS, pkey);
	}

	@Override
	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_USERNAME: return username;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return uid;
			case 4: return home;
			case 5: return autoresponder_from==-1?null:autoresponder_from;
			case 6: return autoresponder_subject;
			case 7: return autoresponder_path;
			case 8: return is_autoresponder_enabled;
			case 9: return disable_log == -1 ? null : disable_log;
			case 10: return predisable_password;
			case 11: return created;
			case 12: return use_inbox;
			case 13: return trash_email_retention==-1?null:trash_email_retention;
			case 14: return junk_email_retention==-1?null:junk_email_retention;
			case 15: return sa_integration_mode;
			case 16: return sa_required_score;
			case 17: return sa_discard_score==-1 ? null : sa_discard_score;
			case 18: return sudo;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
		return table.getConnector().getScm().getCvsRepository().getCvsRepositories(this);
	}

	public List<AttachmentBlock> getEmailAttachmentBlocks() throws IOException, SQLException {
		return table.getConnector().getEmail().getAttachmentBlock().getEmailAttachmentBlocks(this);
	}

	public String getAutoresponderContent() throws IOException, SQLException {
		String content=table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AUTORESPONDER_CONTENT, pkey);
		if(content.length()==0) return null;
		return content;
	}

	public InboxAddress getAutoresponderFrom() throws IOException, SQLException {
		if(autoresponder_from==-1) return null;
		// Might be filtered
		return table.getConnector().getEmail().getInboxAddress().get(autoresponder_from);
	}

	public String getAutoresponderSubject() {
		return autoresponder_subject;
	}

	public String getAutoresponderPath() {
		return autoresponder_path;
	}

	public boolean isAutoresponderEnabled() {
		return is_autoresponder_enabled;
	}

	public String getCronTable() throws IOException, SQLException {
		return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_CRON_TABLE, pkey);
	}

	/**
	 * Gets the default non-hashed home directory of <code>/home/<i>username</i></code>.
	 */
	public static PosixPath getDefaultHomeDirectory(User.Name username) {
		try {
			return PosixPath.valueOf("/home/" + username);
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Gets the optional hashed home directory of <code>/home/<i>u</i>/<i>username</i></code>.
	 */
	public static PosixPath getHashedHomeDirectory(User.Name username) {
		try {
			String usernameStr = username.toString();
			return PosixPath.valueOf("/home/" + usernameStr.charAt(0) + '/' + usernameStr);
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<Address> getEmailAddresses() throws SQLException, IOException {
		return table.getConnector().getEmail().getInboxAddress().getEmailAddresses(this);
	}

	public List<SharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getSharedTomcat().getHttpdSharedTomcats(this);
	}

	public List<Site> getHttpdSites() throws IOException, SQLException {
		return table.getConnector().getWeb().getSite().getHttpdSites(this);
	}

	public InboxAttributes getInboxAttributes() throws IOException, SQLException {
		return table.getConnector().requestResult(
			true,
			AoservProtocol.CommandID.GET_INBOX_ATTRIBUTES,
			// Java 9: new AOServConnector.ResultRequest<>
			new AOServConnector.ResultRequest<InboxAttributes>() {

				private InboxAttributes result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						InboxAttributes attr;
						if(in.readBoolean()) {
							attr=new InboxAttributes(table.getConnector(), UserServer.this);
							attr.read(in, AoservProtocol.Version.CURRENT_VERSION);
						} else attr=null;
						result = attr;
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public InboxAttributes afterRelease() {
					return result;
				}
			}
		);
	}

	public long[] getImapFolderSizes(final String[] folderNames) throws IOException, SQLException {
		final long[] sizes=new long[folderNames.length];
		if(sizes.length>0) {
			table.getConnector().requestUpdate(
				true,
				AoservProtocol.CommandID.GET_IMAP_FOLDER_SIZES,
				new AOServConnector.UpdateRequest() {
					@Override
					public void writeRequest(StreamableOutput out) throws IOException {
						out.writeCompressedInt(pkey);
						out.writeCompressedInt(folderNames.length);
						for (String folderName : folderNames) {
							out.writeUTF(folderName);
						}
					}

					@Override
					public void readResponse(StreamableInput in) throws IOException, SQLException {
						int code=in.readByte();
						if(code==AoservProtocol.DONE) {
							for(int c=0;c<folderNames.length;c++) {
								sizes[c]=in.readLong();
							}
						} else {
							AoservProtocol.checkResult(code, in);
							throw new IOException("Unexpected response code: "+code);
						}
					}

					@Override
					public void afterRelease() {
						// Do nothing
					}
				}
			);
		}
		return sizes;
	}

	public List<InboxAddress> getLinuxAccAddresses() throws IOException, SQLException {
		return table.getConnector().getEmail().getInboxAddress().getLinuxAccAddresses(this);
	}

	public PosixPath getHome() {
		return home;
	}

	public User.Name getLinuxAccount_username_id() {
		return username;
	}

	public User getLinuxAccount() throws SQLException, IOException {
		User linuxAccountObject = table.getConnector().getLinux().getUser().get(username);
		if (linuxAccountObject == null) throw new SQLException("Unable to find LinuxAccount: " + username);
		return linuxAccountObject;
	}

	public String getPredisablePassword() {
		return predisable_password;
	}

	@SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
	public UnmodifiableTimestamp getCreated() {
		return created;
	}

	public boolean useInbox() {
		return use_inbox;
	}

	/**
	 * Gets the age of trash that will be auto-deleted, in days, or <code>-1</code>
	 * to not auto-delete.
	 */
	public int getTrashEmailRetention() {
		return trash_email_retention;
	}

	/**
	 * Gets the age of junk that will be auto-deleted, in days, or <code>-1</code>
	 * to not auto-delete.
	 */
	public int getJunkEmailRetention() {
		return junk_email_retention;
	}

	public SpamAssassinMode getEmailSpamAssassinIntegrationMode() throws SQLException, IOException {
		SpamAssassinMode esaim=table.getConnector().getEmail().getSpamAssassinMode().get(sa_integration_mode);
		if(esaim==null) throw new SQLException("Unable to find EmailSpamAssassinIntegrationMode: "+sa_integration_mode);
		return esaim;
	}

	public float getSpamAssassinRequiredScore() {
		return sa_required_score;
	}

	/**
	 * Gets the minimum score where spam assassin should discard email or <code>-1</code> if this
	 * feature is disabled.
	 */
	public int getSpamAssassinDiscardScore() {
		return sa_discard_score;
	}

	/**
	 * Gets the <code>sudo</code> setting for this user or {@code null}
	 * when no <code>sudo</code> allowed.
	 */
	public String getSudo() {
		return sudo;
	}

	/**
	 * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
	 *
	 * @exception  SQLException  if the primary group is not found
	 *                           or two or more groups are marked as primary
	 *                           or the primary group does not exist on the same server
	 */
	public GroupServer getPrimaryLinuxServerGroup() throws SQLException, IOException {
		return table.getConnector().getLinux().getGroupServer().getPrimaryLinuxServerGroup(this);
	}

	public int getAoServer_server_id() {
		return ao_server;
	}

	public Server getServer() throws SQLException, IOException {
		Server ao=table.getConnector().getLinux().getServer().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find linux.Server: " + ao_server);
		return ao;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_SERVER_ACCOUNTS;
	}

	public LinuxId getUid() {
		return uid;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos=1;
			pkey=result.getInt(pos++);
			username = User.Name.valueOf(result.getString(pos++));
			ao_server=result.getInt(pos++);
			uid = LinuxId.valueOf(result.getInt(pos++));
			home = PosixPath.valueOf(result.getString(pos++));
			autoresponder_from=result.getInt(pos++);
			if(result.wasNull()) autoresponder_from=-1;
			autoresponder_subject = result.getString(pos++);
			autoresponder_path = result.getString(pos++);
			is_autoresponder_enabled=result.getBoolean(pos++);
			disable_log=result.getInt(pos++);
			if(result.wasNull()) disable_log=-1;
			predisable_password=result.getString(pos++);
			created = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
			use_inbox=result.getBoolean(pos++);
			trash_email_retention=result.getInt(pos++);
			if(result.wasNull()) trash_email_retention=-1;
			junk_email_retention=result.getInt(pos++);
			if(result.wasNull()) junk_email_retention=-1;
			sa_integration_mode=result.getString(pos++);
			sa_required_score=result.getFloat(pos++);
			sa_discard_score = result.getInt(pos++);
			if(result.wasNull()) sa_discard_score = -1;
			sudo = result.getString(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public int isProcmailManual() throws IOException, SQLException {
		return table.getConnector().requestIntQuery(true, AoservProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			username = User.Name.valueOf(in.readUTF()).intern();
			ao_server = in.readCompressedInt();
			uid = LinuxId.valueOf(in.readCompressedInt());
			home = PosixPath.valueOf(in.readUTF());
			autoresponder_from = in.readCompressedInt();
			autoresponder_subject = in.readNullUTF();
			autoresponder_path = in.readNullUTF();
			is_autoresponder_enabled = in.readBoolean();
			disable_log = in.readCompressedInt();
			predisable_password = in.readNullUTF();
			created = SQLStreamables.readUnmodifiableTimestamp(in);
			use_inbox = in.readBoolean();
			trash_email_retention = in.readCompressedInt();
			junk_email_retention = in.readCompressedInt();
			sa_integration_mode = in.readUTF().intern();
			sa_required_score = in.readFloat();
			sa_discard_score = in.readCompressedInt();
			sudo = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public List<com.aoindustries.aoserv.client.email.List> getEmailLists() throws IOException, SQLException {
		return table.getConnector().getEmail().getList().getEmailLists(this);
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		LinuxId uidMin = getServer().getUidMin();
		if(uid.compareTo(uidMin) < 0) reasons.add(new CannotRemoveReason<>("Not allowed to remove accounts with UID less than " + uidMin, this));

		Server ao = getServer();

		// No CVS repositories
		for(CvsRepository cr : ao.getCvsRepositories()) {
			if(cr.getLinuxServerAccount_pkey()==pkey) reasons.add(new CannotRemoveReason<>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerAccount().getServer().getHostname(), cr));
		}

		// No email lists
		for(com.aoindustries.aoserv.client.email.List el : getEmailLists()) {
			reasons.add(new CannotRemoveReason<>("Used by email list "+el.getPath()+" on "+el.getLinuxServerAccount().getServer().getHostname(), el));
		}

		// No httpd_servers
		for(HttpdServer hs : ao.getHttpdServers()) {
			if(hs.getLinuxServerAccount_pkey()==pkey) {
				String name = hs.getName();
				reasons.add(
					new CannotRemoveReason<>(
						name==null
							? "Used by Apache HTTP Server on " + hs.getLinuxServer().getHostname()
							: "Used by Apache HTTP Server (" + name + ") on " + hs.getLinuxServer().getHostname(),
						hs
					)
				);
			}
		}

		// No httpd shared tomcats
		for(SharedTomcat hst : ao.getHttpdSharedTomcats()) {
			if(hst.getLinuxServerAccount_pkey()==pkey) reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getLinuxServer().getHostname(), hst));
		}

		// No majordomo_servers
		for(MajordomoServer ms : ao.getMajordomoServers()) {
			if(ms.getLinuxServerAccount_pkey()==pkey) {
				Domain ed=ms.getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getLinuxServer().getHostname(), ms));
			}
		}

		// No private FTP servers
		for(PrivateServer pfs : ao.getPrivateFTPServers()) {
			if(pfs.getLinuxServerAccount_pkey()==pkey) {
				UserServer lsa = pfs.getLinuxServerAccount();
				reasons.add(new CannotRemoveReason<>("Used by private FTP server "+lsa.getHome()+" on "+lsa.getServer().getHostname(), pfs));
			}
		}

		// No httpd_sites
		for(Site site : ao.getHttpdSites()) {
			if(site.getLinuxAccount_username().equals(username)) reasons.add(new CannotRemoveReason<>("Used by website "+site.getInstallDirectory()+" on "+site.getLinuxServer().getHostname(), site));
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.LINUX_SERVER_ACCOUNTS,
			pkey
		);
	}

	public void setCronTable(String cronTable) throws IOException, SQLException {
		table.getConnector().requestUpdate(true, AoservProtocol.CommandID.SET_CRON_TABLE, pkey, cronTable);
	}

	@Override
	public void setPassword(String password) throws IOException, SQLException {
		AOServConnector connector=table.getConnector();
		if(!connector.isSecure()) throw new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
		connector.requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
	}

	public void setAutoresponder(
		final InboxAddress from,
		final String subject,
		final String content,
		final boolean enabled
	) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_AUTORESPONDER,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(from==null?-1:from.getPkey());
					out.writeBoolean(subject!=null);
					if(subject!=null) out.writeUTF(subject);
					out.writeBoolean(content!=null);
					if(content!=null) out.writeUTF(content);
					out.writeBoolean(enabled);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	public void setTrashEmailRetention(int days) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
	}

	public void setJunkEmailRetention(int days) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
	}

	public void setEmailSpamAssassinIntegrationMode(SpamAssassinMode mode) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.getName());
	}

	public void setSpamAssassinRequiredScore(float required_score) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
	}

	public void setSpamAssassinDiscardScore(int discard_score) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_DISCARD_SCORE, pkey, discard_score);
	}

	public void setUseInbox(boolean useInbox) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
	}

	public void setPredisablePassword(final String password) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD,
			new AOServConnector.UpdateRequest() {
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(password);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return username+" on "+getServer().getHostname();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(uid.getId());
		out.writeUTF(home.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(autoresponder_from);
		out.writeNullUTF(autoresponder_subject);
		out.writeNullUTF(autoresponder_path);
		out.writeBoolean(is_autoresponder_enabled);
		out.writeCompressedInt(disable_log);
		out.writeNullUTF(predisable_password);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(created.getTime());
		} else {
			SQLStreamables.writeTimestamp(created, out);
		}
		out.writeBoolean(use_inbox);
		out.writeCompressedInt(trash_email_retention);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_120)>=0) {
			out.writeCompressedInt(junk_email_retention);
			out.writeUTF(sa_integration_mode);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_124)>=0) {
			out.writeFloat(sa_required_score);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_40)>=0) {
			out.writeCompressedInt(sa_discard_score);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
			out.writeNullUTF(sudo);
		}
	}

	@Override
	public boolean canSetPassword() throws IOException, SQLException {
		return disable_log==-1 && getLinuxAccount().canSetPassword();
	}

	public boolean passwordMatches(String password) throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AoservProtocol.CommandID.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
	}

	public int addEmailAddress(Address address) throws IOException, SQLException {
		return table.getConnector().getEmail().getInboxAddress().addLinuxAccAddress(address, this);
	}
}
