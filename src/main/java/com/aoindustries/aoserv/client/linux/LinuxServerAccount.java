/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2015, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Username;
import com.aoindustries.aoserv.client.email.EmailAddress;
import com.aoindustries.aoserv.client.email.EmailAttachmentBlock;
import com.aoindustries.aoserv.client.email.EmailDomain;
import com.aoindustries.aoserv.client.email.EmailList;
import com.aoindustries.aoserv.client.email.EmailSpamAssassinIntegrationMode;
import com.aoindustries.aoserv.client.email.InboxAttributes;
import com.aoindustries.aoserv.client.email.LinuxAccAddress;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.ftp.PrivateFTPServer;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.validator.LinuxId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.HttpdSite;
import com.aoindustries.aoserv.client.web.tomcat.HttpdSharedTomcat;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>LinuxServerAccount</code> grants a <code>LinuxAccount</code>
 * access to an <code>AOServer</code>.
 *
 * @see  LinuxAccount
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerAccount extends CachedObjectIntegerKey<LinuxServerAccount> implements Removable, PasswordProtected, Disablable {

	/**
	 * The UID of the root user.
	 * 
	 * Note: Copied from UnixFile.java to avoid interproject dependency.
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

	UserId username;
	int ao_server;
	LinuxId uid;
	private UnixPath home;
	int autoresponder_from;
	private String autoresponder_subject;
	private String autoresponder_path;
	private boolean is_autoresponder_enabled;
	int disable_log;
	private String predisable_password;
	private long created;
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
		if(uid.compareTo(getAOServer().getUidMin()) < 0) return false;

		// cvs_repositories
		for(CvsRepository cr : getCvsRepositories()) if(!cr.isDisabled()) return false;

		// httpd_shared_tomcats
		for(HttpdSharedTomcat hst : getHttpdSharedTomcats()) if(!hst.isDisabled()) return false;

		// email_lists
		for(EmailList el : getEmailLists()) if(!el.isDisabled()) return false;

		// httpd_sites
		for(HttpdSite hs : getHttpdSites()) if(!hs.isDisabled()) return false;

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

	public long copyHomeDirectory(final AOServer toServer) throws IOException, SQLException {
		return table.getConnector().requestResult(
			false,
			AOServProtocol.CommandID.COPY_HOME_DIRECTORY,
			new AOServConnector.ResultRequest<Long>() {
				long result;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(toServer.getPkey());
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code!=AOServProtocol.DONE) {
						AOServProtocol.checkResult(code, in);
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

	public void copyPassword(LinuxServerAccount other) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.getPkey());
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.LINUX_SERVER_ACCOUNTS, pkey);
	}

	@Override
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
			case 11: return getCreated();
			case 12: return use_inbox;
			case 13: return trash_email_retention==-1?null:trash_email_retention;
			case 14: return junk_email_retention==-1?null:junk_email_retention;
			case 15: return sa_integration_mode;
			case 16: return sa_required_score;
			case 17: return sa_discard_score==-1 ? null : sa_discard_score;
			case 18: return sudo;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
		return table.getConnector().getCvsRepositories().getCvsRepositories(this);
	}

	public List<EmailAttachmentBlock> getEmailAttachmentBlocks() throws IOException, SQLException {
		return table.getConnector().getEmailAttachmentBlocks().getEmailAttachmentBlocks(this);
	}

	public String getAutoresponderContent() throws IOException, SQLException {
		String content=table.getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AUTORESPONDER_CONTENT, pkey);
		if(content.length()==0) return null;
		return content;
	}

	public LinuxAccAddress getAutoresponderFrom() throws IOException, SQLException {
		if(autoresponder_from==-1) return null;
		// Might be filtered
		return table.getConnector().getLinuxAccAddresses().get(autoresponder_from);
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
		return table.getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_CRON_TABLE, pkey);
	}

	/**
	 * Gets the default non-hashed home directory of <code>/home/<i>username</i></code>.
	 */
	public static UnixPath getDefaultHomeDirectory(UserId username) {
		try {
			return UnixPath.valueOf("/home/" + username);
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Gets the optional hashed home directory of <code>/home/<i>u</i>/<i>username</i></code>.
	 */
	public static UnixPath getHashedHomeDirectory(UserId username) {
		try {
			String usernameStr = username.toString();
			return UnixPath.valueOf("/home/" + usernameStr.charAt(0) + '/' + usernameStr);
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<EmailAddress> getEmailAddresses() throws SQLException, IOException {
		return table.getConnector().getLinuxAccAddresses().getEmailAddresses(this);
	}

	public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
		return table.getConnector().getHttpdSharedTomcats().getHttpdSharedTomcats(this);
	}

	public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
		return table.getConnector().getHttpdSites().getHttpdSites(this);
	}

	public InboxAttributes getInboxAttributes() throws IOException, SQLException {
		return table.getConnector().requestResult(
			true,
			AOServProtocol.CommandID.GET_INBOX_ATTRIBUTES,
			new AOServConnector.ResultRequest<InboxAttributes>() {

				InboxAttributes result;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						InboxAttributes attr;
						if(in.readBoolean()) {
							attr=new InboxAttributes(table.getConnector(), LinuxServerAccount.this);
							attr.read(in);
						} else attr=null;
						result = attr;
					} else {
						AOServProtocol.checkResult(code, in);
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
				AOServProtocol.CommandID.GET_IMAP_FOLDER_SIZES,
				new AOServConnector.UpdateRequest() {
					@Override
					public void writeRequest(CompressedDataOutputStream out) throws IOException {
						out.writeCompressedInt(pkey);
						out.writeCompressedInt(folderNames.length);
						for (String folderName : folderNames) {
							out.writeUTF(folderName);
						}
					}

					@Override
					public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
						int code=in.readByte();
						if(code==AOServProtocol.DONE) {
							for(int c=0;c<folderNames.length;c++) {
								sizes[c]=in.readLong();
							}
						} else {
							AOServProtocol.checkResult(code, in);
							throw new IOException("Unexpected response code: "+code);
						}
					}

					@Override
					public void afterRelease() {
					}
				}
			);
		}
		return sizes;
	}

	public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException, SQLException {
		return table.getConnector().getLinuxAccAddresses().getLinuxAccAddresses(this);
	}

	public UnixPath getHome() {
		return home;
	}

	public UserId getLinuxAccount_username_id() {
		return username;
	}

	public LinuxAccount getLinuxAccount() throws SQLException, IOException {
		Username usernameObj=table.getConnector().getUsernames().get(username);
		if(usernameObj==null) throw new SQLException("Unable to find Username: "+username);
		LinuxAccount linuxAccountObject = usernameObj.getLinuxAccount();
		if (linuxAccountObject == null) throw new SQLException("Unable to find LinuxAccount: " + username);
		return linuxAccountObject;
	}

	public String getPredisablePassword() {
		return predisable_password;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
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

	public EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode() throws SQLException, IOException {
		EmailSpamAssassinIntegrationMode esaim=table.getConnector().getEmailSpamAssassinIntegrationModes().get(sa_integration_mode);
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
	public LinuxServerGroup getPrimaryLinuxServerGroup() throws SQLException, IOException {
		return table.getConnector().getLinuxServerGroups().getPrimaryLinuxServerGroup(this);
	}

	public int getAoServer_server_id() {
		return ao_server;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer ao=table.getConnector().getAoServers().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: " + ao_server);
		return ao;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_SERVER_ACCOUNTS;
	}

	public LinuxId getUid() {
		return uid;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos=1;
			pkey=result.getInt(pos++);
			username = UserId.valueOf(result.getString(pos++));
			ao_server=result.getInt(pos++);
			uid = LinuxId.valueOf(result.getInt(pos++));
			home = UnixPath.valueOf(result.getString(pos++));
			autoresponder_from=result.getInt(pos++);
			if(result.wasNull()) autoresponder_from=-1;
			autoresponder_subject = result.getString(pos++);
			autoresponder_path = result.getString(pos++);
			is_autoresponder_enabled=result.getBoolean(pos++);
			disable_log=result.getInt(pos++);
			if(result.wasNull()) disable_log=-1;
			predisable_password=result.getString(pos++);
			created=result.getTimestamp(pos++).getTime();
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
		return table.getConnector().requestIntQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
	}

	@Override
	public int arePasswordsSet() throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			username = UserId.valueOf(in.readUTF()).intern();
			ao_server=in.readCompressedInt();
			uid=LinuxId.valueOf(in.readCompressedInt());
			home = UnixPath.valueOf(in.readUTF());
			autoresponder_from=in.readCompressedInt();
			autoresponder_subject=in.readNullUTF();
			autoresponder_path=in.readNullUTF();
			is_autoresponder_enabled=in.readBoolean();
			disable_log=in.readCompressedInt();
			predisable_password=in.readNullUTF();
			created=in.readLong();
			use_inbox=in.readBoolean();
			trash_email_retention=in.readCompressedInt();
			junk_email_retention=in.readCompressedInt();
			sa_integration_mode=in.readUTF().intern();
			sa_required_score=in.readFloat();
			sa_discard_score = in.readCompressedInt();
			sudo = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public List<EmailList> getEmailLists() throws IOException, SQLException {
		return table.getConnector().getEmailLists().getEmailLists(this);
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		LinuxId uidMin = getAOServer().getUidMin();
		if(uid.compareTo(uidMin) < 0) reasons.add(new CannotRemoveReason<>("Not allowed to remove accounts with UID less than " + uidMin, this));

		AOServer ao=getAOServer();

		// No CVS repositories
		for(CvsRepository cr : ao.getCvsRepositories()) {
			if(cr.getLinuxServerAccount_pkey()==pkey) reasons.add(new CannotRemoveReason<>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerAccount().getAOServer().getHostname(), cr));
		}

		// No email lists
		for(EmailList el : getEmailLists()) {
			reasons.add(new CannotRemoveReason<>("Used by email list "+el.getPath()+" on "+el.getLinuxServerAccount().getAOServer().getHostname(), el));
		}

		// No httpd_servers
		for(HttpdServer hs : ao.getHttpdServers()) {
			if(hs.getLinuxServerAccount_pkey()==pkey) {
				String name = hs.getName();
				reasons.add(
					new CannotRemoveReason<>(
						name==null
							? "Used by Apache HTTP Server on " + hs.getAOServer().getHostname()
							: "Used by Apache HTTP Server (" + name + ") on " + hs.getAOServer().getHostname(),
						hs
					)
				);
			}
		}

		// No httpd shared tomcats
		for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
			if(hst.getLinuxServerAccount_pkey()==pkey) reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
		}

		// No majordomo_servers
		for(MajordomoServer ms : ao.getMajordomoServers()) {
			if(ms.getLinuxServerAccount_pkey()==pkey) {
				EmailDomain ed=ms.getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
			}
		}

		// No private FTP servers
		for(PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
			if(pfs.getLinuxServerAccount_pkey()==pkey) {
				LinuxServerAccount lsa = pfs.getLinuxServerAccount();
				reasons.add(new CannotRemoveReason<>("Used by private FTP server "+lsa.getHome()+" on "+lsa.getAOServer().getHostname(), pfs));
			}
		}

		// No httpd_sites
		for(HttpdSite site : ao.getHttpdSites()) {
			if(site.getLinuxAccount_username().equals(username)) reasons.add(new CannotRemoveReason<>("Used by website "+site.getInstallDirectory()+" on "+site.getAoServer().getHostname(), site));
		}

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_SERVER_ACCOUNTS,
			pkey
		);
	}

	public void setCronTable(String cronTable) throws IOException, SQLException {
		table.getConnector().requestUpdate(true, AOServProtocol.CommandID.SET_CRON_TABLE, pkey, cronTable);
	}

	@Override
	public void setPassword(String password) throws IOException, SQLException {
		AOServConnector connector=table.getConnector();
		if(!connector.isSecure()) throw new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");
		connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
	}

	public void setAutoresponder(
		final LinuxAccAddress from,
		final String subject,
		final String content,
		final boolean enabled
	) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AOServProtocol.CommandID.SET_AUTORESPONDER,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(from==null?-1:from.getPkey());
					out.writeBoolean(subject!=null);
					if(subject!=null) out.writeUTF(subject);
					out.writeBoolean(content!=null);
					if(content!=null) out.writeUTF(content);
					out.writeBoolean(enabled);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
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
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
	}

	public void setJunkEmailRetention(int days) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
	}

	public void setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode mode) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.getName());
	}

	public void setSpamAssassinRequiredScore(float required_score) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
	}

	public void setSpamAssassinDiscardScore(int discard_score) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_DISCARD_SCORE, pkey, discard_score);
	}

	public void setUseInbox(boolean useInbox) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
	}

	public void setPredisablePassword(final String password) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AOServProtocol.CommandID.SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(password);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
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
		return username+" on "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(uid.getId());
		out.writeUTF(home.toString());
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
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
		out.writeLong(created);
		out.writeBoolean(use_inbox);
		out.writeCompressedInt(trash_email_retention);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_120)>=0) {
			out.writeCompressedInt(junk_email_retention);
			out.writeUTF(sa_integration_mode);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_124)>=0) {
			out.writeFloat(sa_required_score);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_40)>=0) {
			out.writeCompressedInt(sa_discard_score);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80_1) >= 0) {
			out.writeNullUTF(sudo);
		}
	}

	@Override
	public boolean canSetPassword() throws IOException, SQLException {
		return disable_log==-1 && getLinuxAccount().canSetPassword();
	}

	public boolean passwordMatches(String password) throws IOException, SQLException {
		return table.getConnector().requestBooleanQuery(true, AOServProtocol.CommandID.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
	}

	public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
		return table.getConnector().getLinuxAccAddresses().addLinuxAccAddress(address, this);
	}
}
