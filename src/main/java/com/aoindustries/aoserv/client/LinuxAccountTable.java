/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.ValidationResult;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  LinuxAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountTable extends CachedTableStringKey<LinuxAccount> {

	LinuxAccountTable(AOServConnector connector) {
		super(connector, LinuxAccount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(LinuxAccount.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addLinuxAccount(
		final Username usernameObject,
		final String primaryGroup,
		final Gecos name,
		final Gecos office_location,
		final Gecos office_phone,
		final Gecos home_phone,
		final String type,
		final String shell
	) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.LINUX_ACCOUNTS.ordinal());
					out.writeUTF(usernameObject.pkey);
					out.writeUTF(primaryGroup);
					out.writeUTF(name.toString());
					out.writeNullUTF(ObjectUtils.toString(office_location));
					out.writeNullUTF(ObjectUtils.toString(office_phone));
					out.writeNullUTF(ObjectUtils.toString(home_phone));
					out.writeUTF(type);
					out.writeUTF(shell);
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
					connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public LinuxAccount get(String username) throws IOException, SQLException {
		return getUniqueRow(LinuxAccount.COLUMN_USERNAME, username);
	}

	public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
		List<LinuxAccount> cached = getRows();
		int len = cached.size();
		List<LinuxAccount> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			LinuxAccount linuxAccount = cached.get(c);
			if (linuxAccount.getType().isEmail()) matches.add(linuxAccount);
		}
		return matches;
	}

	List<LinuxAccount> getMailAccounts(Business business) throws IOException, SQLException {
		AccountingCode accounting=business.pkey;
		List<LinuxAccount> cached = getRows();
		int len = cached.size();
		List<LinuxAccount> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			LinuxAccount linuxAccount = cached.get(c);
			if (
				linuxAccount.getType().isEmail()
				&& linuxAccount.getUsername().getPackage().accounting.equals(accounting)
			) matches.add(linuxAccount);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACCOUNTS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_LINUX_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_LINUX_ACCOUNT, args, 8, err)) {
				connector.getSimpleAOClient().addLinuxAccount(
					args[1],
					args[2],
					AOSH.parseGecos(args[3], "full_name"),
					args[4].length()==0 ? null : AOSH.parseGecos(args[4], "office_location"),
					args[5].length()==0 ? null : AOSH.parseGecos(args[5], "office_phone"),
					args[6].length()==0 ? null : AOSH.parseGecos(args[6], "home_phone"),
					args[7],
					args[8]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.ARE_LINUX_ACCOUNT_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().areLinuxAccountPasswordsSet(args[1]);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_NAME, args, 1, err)) {
				ValidationResult validationResult = Gecos.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_ACCOUNTING+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = connector.getSimpleAOClient().checkLinuxAccountPassword(args[1], args[2]);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_LINUX_ACCOUNT_USERNAME, args, 1, err)) {
				SimpleAOClient.checkLinuxAccountUsername(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_LINUX_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_LINUX_ACCOUNT, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableLinuxAccount(
						args[1],
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_LINUX_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_LINUX_ACCOUNT, args, 1, err)) {
				connector.getSimpleAOClient().enableLinuxAccount(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.GENERATE_PASSWORD, args, 0, err)) {
				out.println(connector.getSimpleAOClient().generatePassword());
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_LINUX_ACCOUNT)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_LINUX_ACCOUNT, args, 1, err)) {
				connector.getSimpleAOClient().removeLinuxAccount(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_HOME_PHONE, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountHomePhone(
					args[1],
					args[2].length()==0 ? null : AOSH.parseGecos(args[2], "phone_number")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_NAME, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountName(
					args[1],
					AOSH.parseGecos(args[2], "full_name")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_LOCATION, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountOfficeLocation(
					args[1],
					args[2].length()==0 ? null : AOSH.parseGecos(args[2], "loation")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_OFFICE_PHONE, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountOfficePhone(
					args[1],
					args[2].length()==0 ? null : AOSH.parseGecos(args[2], "phone_number")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountPassword(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_LINUX_ACCOUNT_SHELL)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_LINUX_ACCOUNT_SHELL, args, 2, err)) {
				connector.getSimpleAOClient().setLinuxAccountShell(args[1], args[2]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_LINUX_ACCOUNT_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForLinuxAccountRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.LINUX_ACCOUNTS,
			aoServer.pkey
		);
	}
}
