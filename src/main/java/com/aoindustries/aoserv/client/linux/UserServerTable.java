/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.InboxAttributes;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.DomainName;
import com.aoindustries.security.AccountDisabledException;
import com.aoindustries.security.BadPasswordException;
import com.aoindustries.security.LoginException;
import com.aoindustries.sql.SQLUtility;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class UserServerTable extends CachedTableIntegerKey<UserServer> {

	UserServerTable(AOServConnector connector) {
		super(connector, UserServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(UserServer.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(UserServer.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addLinuxServerAccount(User linuxAccount, Server aoServer, PosixPath home) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.LINUX_SERVER_ACCOUNTS,
			linuxAccount.getUsername_id(),
			aoServer.getPkey(),
			home
		);
		return pkey;
	}

	int addSystemUser(
		Server aoServer,
		User.Name username,
		int uid,
		int gid,
		User.Gecos fullName,
		User.Gecos officeLocation,
		User.Gecos officePhone,
		User.Gecos homePhone,
		PosixPath home,
		PosixPath shell
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD_SYSTEM_USER,
			aoServer.getPkey(),
			username,
			uid,
			gid,
			fullName==null ? "" : fullName.toString(),
			officeLocation==null ? "" : officeLocation.toString(),
			officePhone==null ? "" : officePhone.toString(),
			homePhone==null ? "" : homePhone.toString(),
			home,
			shell
		);
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(uidHash) {
			uidHashBuilt=false;
		}
		synchronized(nameHash) {
			nameHashBuilt=false;
		}
	}

	@Override
	public UserServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(UserServer.COLUMN_PKEY, pkey);
	}

	List<UserServer> getAlternateLinuxServerAccounts(GroupServer group) throws SQLException, IOException {
		Server aoServer = group.getServer();
		int osv = aoServer.getHost().getOperatingSystemVersion_id();
		Group.Name groupName = group.getLinuxGroup().getName();

		List<UserServer> rows = getRows();
		int cachedLen = rows.size();
		List<UserServer> matches=new ArrayList<>(cachedLen);
		for(int c = 0; c < cachedLen; c++) {
			UserServer linuxServerAccount = rows.get(c);
			if(linuxServerAccount.ao_server == aoServer.getPkey()) {
				User.Name username = linuxServerAccount.username;

				// Must also have a non-primary entry in the LinuxGroupAccounts that is also a group on this server
				for(GroupUser lga : connector.getLinux().getGroupUser().getLinuxGroupAccounts(groupName, username)) {
					if(
						!lga.isPrimary()
						&& (
							lga.getOperatingSystemVersion_pkey() == null
							|| lga.getOperatingSystemVersion_pkey() == osv
						)
					) {
						matches.add(linuxServerAccount);
						break;
					}
				}
			}
		}
		return matches;
	}

	private boolean nameHashBuilt=false;
	private final Map<Integer,Map<User.Name,UserServer>> nameHash=new HashMap<>();

	UserServer getLinuxServerAccount(Server aoServer, User.Name username) throws IOException, SQLException {
		synchronized(nameHash) {
			if(!nameHashBuilt) {
				nameHash.clear();

				List<UserServer> list=getRows();
				int len=list.size();
				for(int c=0; c<len; c++) {
					UserServer lsa=list.get(c);
					Integer I=lsa.getServer().getPkey();
					Map<User.Name,UserServer> serverHash=nameHash.get(I);
					if(serverHash==null) nameHash.put(I, serverHash=new HashMap<>());
					if(serverHash.put(lsa.username, lsa)!=null) throw new SQLException("LinuxServerAccount username exists more than once on server: "+lsa.username+" on "+I);
				}
				nameHashBuilt=true;
			}
			Map<User.Name,UserServer> serverHash=nameHash.get(aoServer.getPkey());
			if(serverHash==null) return null;
			return serverHash.get(username);
		}
	}

	/**
	 * Finds a LinuxServerAccount by a username and password combination.  It tries to return an account that is not disabled and
	 * matches both username and password.
	 *
	 * @return   the <code>LinuxServerAccount</code> found if a non-disabled username and password are found, or {@code null} if no match found
	 *
	 * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
	 */
	public UserServer getLinuxServerAccountFromUsernamePassword(User.Name username, String password, boolean emailOnly) throws LoginException, IOException, SQLException {
		List<UserServer> list = getRows();
		UserServer badPasswordLSA=null;
		UserServer disabledLSA=null;
		int len = list.size();
		for (int c = 0; c < len; c++) {
			UserServer account=list.get(c);
			if(
				account.username.equals(username)
				&& (!emailOnly || account.getLinuxAccount().getType().isEmail())
			) {
				if(account.disable_log!=-1) {
					if(disabledLSA==null) disabledLSA=account;
				} else {
					if(account.passwordMatches(password)) return account;
					else {
						if(badPasswordLSA==null) badPasswordLSA=account;
					}
				}
			}
		}
		if(badPasswordLSA!=null) throw new BadPasswordException("The password does not match the password for the \""+badPasswordLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+badPasswordLSA.getServer().getHostname()+"\" server.");
		if(disabledLSA!=null) {
			DisableLog dl=disabledLSA.getDisableLog();
			String reason=dl==null?null:dl.getDisableReason();
			if(reason==null) throw new AccountDisabledException("The \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getServer().getHostname()+"\" server has been disabled for an unspecified reason.");
			else throw new AccountDisabledException("The \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getServer().getHostname()+"\" server has been disabled for the following reason: "+reason);
		}
		return null;
	}

	/**
	 * Finds a LinuxServerAccount by an email address and password combination.  It tries to return an account that is not disabled and
	 * matches both email address and password.
	 *
	 * @return   the <code>LinuxServerAccount</code> found if a non-disabled email address and password are found, or {@code null} if no match found
	 *
	 * @exception  LoginException  if a possible account match is found but the account is disabled or has a different password
	 */
	public UserServer getLinuxServerAccountFromEmailAddress(String address, DomainName domain, String password) throws LoginException, IOException, SQLException {
		UserServer badPasswordLSA=null;
		UserServer disabledLSA=null;

		List<Domain> domains=connector.getEmail().getDomain().getRows();
		int domainsLen=domains.size();
		for(int c=0;c<domainsLen;c++) {
			Domain ed=domains.get(c);
			if(ed.getDomain().equals(domain)) {
				Server ao = ed.getLinuxServer();
				Address ea=ed.getEmailAddress(address);
				if(ea!=null) {
					List<UserServer> lsas=ea.getLinuxServerAccounts();
					int lsasLen=lsas.size();
					for(int d=0;d<lsasLen;d++) {
						UserServer lsa=lsas.get(d);
						if(lsa.disable_log!=-1) {
							if(disabledLSA==null) disabledLSA=lsa;
						} else {
							if(lsa.passwordMatches(password)) return lsa;
							else {
								if(badPasswordLSA==null) badPasswordLSA=lsa;
							}
						}
					}
				}
			}
		}

		if(badPasswordLSA!=null) throw new BadPasswordException("The \""+address+"@"+domain+"\" address resolves to the \""+badPasswordLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+badPasswordLSA.getServer().getHostname()+"\" server, but the password does not match.");
		if(disabledLSA!=null) {
			DisableLog dl=disabledLSA.getDisableLog();
			String reason=dl==null?null:dl.getDisableReason();
			if(reason==null) throw new AccountDisabledException("The \""+address+"@"+domain+"\" address resolves to the \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getServer().getHostname()+"\" server, but the account has been disabled for an unspecified reason.");
			else throw new AccountDisabledException("The \""+address+"@"+domain+"\" address resolves to the \""+disabledLSA.getLinuxAccount().getUsername().getUsername()+"\" account on the \""+disabledLSA.getServer().getHostname()+"\" server, but the account has been disabled for the following reason: "+reason);
		}
		return null;
	}

	private boolean uidHashBuilt=false;
	private final Map<Integer,Map<LinuxId,UserServer>> uidHash=new HashMap<>();

	UserServer getLinuxServerAccount(Server aoServer, LinuxId uid) throws IOException, SQLException {
		synchronized(uidHash) {
			if(!uidHashBuilt) {
				uidHash.clear();

				List<UserServer> list = getRows();
				int len = list.size();
				for (int c = 0; c < len; c++) {
					UserServer lsa=list.get(c);
					LinuxId lsaUID = lsa.getUid();
					// Only hash the root user for uid of 0
					if(lsaUID.getId() != UserServer.ROOT_UID || lsa.username.equals(User.ROOT)) {
						Integer aoI=lsa.getServer().getPkey();
						Map<LinuxId,UserServer> serverHash=uidHash.get(aoI);
						if(serverHash==null) uidHash.put(aoI, serverHash=new HashMap<>());
						LinuxId I=lsaUID;
						if(!serverHash.containsKey(I)) serverHash.put(I, lsa);
					}
				}
				uidHashBuilt=true;
			}
			Map<LinuxId,UserServer> serverHash=uidHash.get(aoServer.getPkey());
			if(serverHash==null) return null;
			return serverHash.get(uid);
		}
	}

	List<UserServer> getLinuxServerAccounts(User linuxAccount) throws IOException, SQLException {
		return getIndexedRows(UserServer.COLUMN_USERNAME, linuxAccount.getUsername_id());
	}

	List<UserServer> getLinuxServerAccounts(Server aoServer) throws IOException, SQLException {
		return getIndexedRows(UserServer.COLUMN_AO_SERVER, aoServer.getPkey());
	}

	public List<UserServer> getMailAccounts() throws IOException, SQLException {
		List<UserServer> cached = getRows();
		int len = cached.size();
		List<UserServer> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			UserServer lsa=cached.get(c);
			if(lsa.getLinuxAccount().getType().isEmail()) matches.add(lsa);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_SERVER_ACCOUNTS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_LINUX_SERVER_ACCOUNT)) {
			if(AOSH.checkParamCount(Command.ADD_LINUX_SERVER_ACCOUNT, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addLinuxServerAccount(
						AOSH.parseLinuxUserName(args[1], "username"),
						args[2],
						args[3].isEmpty() ? null : AOSH.parseUnixPath(args[3], "home_directory")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD)) {
			if(AOSH.checkParamCount(Command.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, args, 3, err)) {
				boolean result=connector.getSimpleAOClient().compareLinuxServerAccountPassword(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]
				);
				out.println(result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.COPY_HOME_DIRECTORY)) {
			if(AOSH.checkParamCount(Command.COPY_HOME_DIRECTORY, args, 3, err)) {
				long byteCount=connector.getSimpleAOClient().copyHomeDirectory(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]
				);
				if(isInteractive) {
					out.print(byteCount);
					out.println(byteCount==1?" byte":" bytes");
				} else out.println(byteCount);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.COPY_LINUX_SERVER_ACCOUNT_PASSWORD)) {
			if(AOSH.checkParamCount(Command.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, args, 4, err)) {
				connector.getSimpleAOClient().copyLinuxServerAccountPassword(
					AOSH.parseLinuxUserName(args[1], "from_username"),
					args[2],
					AOSH.parseLinuxUserName(args[3], "to_username"),
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_LINUX_SERVER_ACCOUNT)) {
			if(AOSH.checkParamCount(Command.DISABLE_LINUX_SERVER_ACCOUNT, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().disableLinuxServerAccount(
						AOSH.parseLinuxUserName(args[1], "username"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_LINUX_SERVER_ACCOUNT)) {
			if(AOSH.checkParamCount(Command.ENABLE_LINUX_SERVER_ACCOUNT, args, 2, err)) {
				connector.getSimpleAOClient().enableLinuxServerAccount(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_CRON_TABLE)) {
			if(AOSH.checkParamCount(Command.GET_CRON_TABLE, args, 2, err)) {
				out.print(
					connector.getSimpleAOClient().getCronTable(
						AOSH.parseLinuxUserName(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_IMAP_FOLDER_SIZES)) {
			if(AOSH.checkMinParamCount(Command.GET_IMAP_FOLDER_SIZES, args, 3, err)) {
				String[] folderNames=new String[args.length-3];
				System.arraycopy(args, 3, folderNames, 0, folderNames.length);
				long[] sizes=connector.getSimpleAOClient().getImapFolderSizes(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					folderNames
				);
				for(int c=0;c<folderNames.length;c++) {
					out.print(folderNames[c]);
					out.print('\t');
					out.print(sizes[c]);
					out.println();
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_INBOX_ATTRIBUTES)) {
			if(AOSH.checkParamCount(Command.GET_INBOX_ATTRIBUTES, args, 2, err)) {
				InboxAttributes attr=connector.getSimpleAOClient().getInboxAttributes(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2]
				);
				out.print("System Time..: ");
				out.println(attr==null ? "Server Unavailable" : SQLUtility.formatDateTime(attr.getSystemTime()));
				out.print("File Size....: ");
				out.println(attr==null ? "Server Unavailable" : attr.getFileSize());
				out.print("Last Modified: ");
				if(attr==null) out.println("Server Unavailable");
				else {
					long lastModified = attr.getLastModified();
					if(lastModified==0) out.println("Unknown");
					else out.println(SQLUtility.formatDateTime(lastModified));
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET)) {
			if(AOSH.checkParamCount(Command.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().isLinuxServerAccountPasswordSet(
						AOSH.parseLinuxUserName(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL)) {
			if(AOSH.checkParamCount(Command.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().isLinuxServerAccountProcmailManual(
						AOSH.parseLinuxUserName(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_LINUX_SERVER_ACCOUNT)) {
			if(AOSH.checkParamCount(Command.REMOVE_LINUX_SERVER_ACCOUNT, args, 2, err)) {
				connector.getSimpleAOClient().removeLinuxServerAccount(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_AUTORESPONDER)) {
			if(AOSH.checkParamCount(Command.SET_AUTORESPONDER, args, 7, err)) {
				connector.getSimpleAOClient().setAutoresponder(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3],
					args[4].length()==0 ? null : AOSH.parseDomainName(args[4], "from_domain"),
					args[5],
					args[6],
					AOSH.parseBoolean(args[7], "enabled")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_CRON_TABLE)) {
			if(AOSH.checkParamCount(Command.SET_CRON_TABLE, args, 3, err)) {
				connector.getSimpleAOClient().setCronTable(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountJunkEmailRetention(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "junk_email_retention")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_PASSWORD)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_PASSWORD, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountPassword(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_INTEGRATION_MODE, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountSpamAssassinIntegrationMode(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountSpamAssassinRequiredScore(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					AOSH.parseFloat(args[3], "required_score")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountTrashEmailRetention(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					args[3]==null||args[3].length()==0?-1:AOSH.parseInt(args[3], "trash_email_retention")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_LINUX_SERVER_ACCOUNT_USE_INBOX)) {
			if(AOSH.checkParamCount(Command.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, args, 3, err)) {
				connector.getSimpleAOClient().setLinuxServerAccountUseInbox(
					AOSH.parseLinuxUserName(args[1], "username"),
					args[2],
					AOSH.parseBoolean(args[3], "use_inbox")
				);
			}
			return true;
		}
		return false;
	}

	boolean isHomeUsed(Server aoServer, PosixPath directory) throws IOException, SQLException {
		int pkey=aoServer.getPkey();

		String directoryStr = directory.toString();
		String startsWith = directoryStr + '/';

		List<UserServer> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			UserServer lsa=cached.get(c);
			if(lsa.ao_server==pkey) {
				PosixPath home=lsa.getHome();
				if(
					home.equals(directory)
					|| home.toString().startsWith(startsWith)
				) return true;
			}
		}
		return false;
	}
}
