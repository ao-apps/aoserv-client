/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableUserIdKey;
import com.aoindustries.aoserv.client.SimpleAOClient;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Administrator
 *
 * @author  AO Industries, Inc.
 */
final public class AdministratorTable extends CachedTableUserIdKey<Administrator> {

	AdministratorTable(AOServConnector connector) {
		super(connector, Administrator.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Administrator.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addBusinessAdministrator(
		final Username username,
		final String name,
		String title,
		final Date birthday,
		final boolean isPrivate,
		final String workPhone,
		String homePhone,
		String cellPhone,
		String fax,
		final String email,
		String address1,
		String address2,
		String city,
		String state,
		String country,
		String zip,
		final boolean enableEmailSupport
	) throws IOException, SQLException {
		if(title!=null && title.length()==0) title=null;
		final String finalTitle = title;
		if(homePhone!=null && homePhone.length()==0) homePhone=null;
		final String finalHomePhone = homePhone;
		if(cellPhone!=null && cellPhone.length()==0) cellPhone=null;
		final String finalCellPhone = cellPhone;
		if(fax!=null && fax.length()==0) fax=null;
		final String finalFax = fax;
		if(address1!=null && address1.length()==0) address1=null;
		final String finalAddress1 = address1;
		if(address2!=null && address2.length()==0) address2=null;
		final String finalAddress2 = address2;
		if(city!=null && city.length()==0) city=null;
		final String finalCity = city;
		if(state!=null && state.length()==0) state=null;
		final String finalState = state;
		if(country!=null && country.length()==0) country=null;
		final String finalCountry = country;
		if(zip!=null && zip.length()==0) zip=null;
		final String finalZip = zip;
		connector.requestUpdate(true,
			AoservProtocol.CommandID.ADD,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(Table.TableID.BUSINESS_ADMINISTRATORS.ordinal());
					out.writeUTF(username.getUsername().toString());
					out.writeUTF(name);
					out.writeBoolean(finalTitle!=null); if(finalTitle!=null) out.writeUTF(finalTitle);
					out.writeLong(birthday==null ? -1 : birthday.getTime());
					out.writeBoolean(isPrivate);
					out.writeUTF(workPhone);
					out.writeBoolean(finalHomePhone!=null); if(finalHomePhone!=null) out.writeUTF(finalHomePhone);
					out.writeBoolean(finalCellPhone!=null); if(finalCellPhone!=null) out.writeUTF(finalCellPhone);
					out.writeBoolean(finalFax!=null); if(finalFax!=null) out.writeUTF(finalFax);
					out.writeUTF(email);
					out.writeBoolean(finalAddress1!=null); if(finalAddress1!=null) out.writeUTF(finalAddress1);
					out.writeBoolean(finalAddress2!=null); if(finalAddress2!=null) out.writeUTF(finalAddress2);
					out.writeBoolean(finalCity!=null); if(finalCity!=null) out.writeUTF(finalCity);
					out.writeBoolean(finalState!=null); if(finalState!=null) out.writeUTF(finalState);
					out.writeBoolean(finalCountry!=null); if(finalCountry!=null) out.writeUTF(finalCountry);
					out.writeBoolean(finalZip!=null); if(finalZip!=null) out.writeUTF(finalZip);
					out.writeBoolean(enableEmailSupport);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
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

	/**
	 * Gets one BusinessAdministrator from the database.
	 */
	@Override
	public Administrator get(UserId username) throws IOException, SQLException {
		return getUniqueRow(Administrator.COLUMN_USERNAME, username);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BUSINESS_ADMINISTRATORS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(Command.ADD_BUSINESS_ADMINISTRATOR, args, 17, err)) {
				connector.getSimpleAOClient().addBusinessAdministrator(
					AOSH.parseUserId(args[1], "username"),
					args[2],
					args[3],
					args[4].length()==0?null:AOSH.parseDate(args[4], "birthday"),
					AOSH.parseBoolean(args[5], "is_private"),
					args[6],
					args[7],
					args[8],
					args[9],
					args[10],
					args[11],
					args[12],
					args[13],
					args[14],
					args[15],
					args[16],
					AOSH.parseBoolean(args[17], "enable_email_support")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD)) {
			if(AOSH.checkParamCount(Command.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = SimpleAOClient.checkBusinessAdministratorPassword(
					AOSH.parseUserId(args[1], "username"),
					args[2]
				);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(Command.DISABLE_BUSINESS_ADMINISTRATOR, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableBusinessAdministrator(
						AOSH.parseUserId(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(Command.ENABLE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
				connector.getSimpleAOClient().enableBusinessAdministrator(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET)) {
			if(AOSH.checkParamCount(Command.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().isBusinessAdministratorPasswordSet(
						AOSH.parseUserId(args[1], "username")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(Command.REMOVE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
				connector.getSimpleAOClient().removeBusinessAdministrator(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_BUSINESS_ADMINISTRATOR_PASSWORD)) {
			if(AOSH.checkParamCount(Command.SET_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setBusinessAdministratorPassword(
					AOSH.parseUserId(args[1], "username"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_BUSINESS_ADMINISTRATOR_PROFILE)) {
			if(AOSH.checkParamCount(Command.SET_BUSINESS_ADMINISTRATOR_PROFILE, args, 16, err)) {
				connector.getSimpleAOClient().setBusinessAdministratorProfile(
					AOSH.parseUserId(args[1], "username"),
					args[2],
					args[3],
					AOSH.parseDate(args[4], "birthday"),
					AOSH.parseBoolean(args[5], "is_private"),
					args[6],
					args[7],
					args[8],
					args[9],
					args[10],
					args[11],
					args[12],
					args[13],
					args[14],
					args[15],
					args[16]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CRYPT)) {
			if(AOSH.checkRangeParamCount(Command.CRYPT, args, 1, 2, err)) {
				String encrypted=SimpleAOClient.crypt(
					args[1],
					args.length==3?args[2]:null
				);
				out.println(encrypted);
			}
			return true;
		}
		return false;
	}
}
