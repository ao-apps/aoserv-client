/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
 * @see  BusinessAdministrator
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorTable extends CachedTableStringKey<BusinessAdministrator> {

	BusinessAdministratorTable(AOServConnector connector) {
		super(connector, BusinessAdministrator.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BusinessAdministrator.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
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
		connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.BUSINESS_ADMINISTRATORS.ordinal());
					out.writeUTF(username.pkey);
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

	/**
	 * Gets one BusinessAdministrator from the database.
	 */
	@Override
	public BusinessAdministrator get(String username) throws IOException, SQLException {
		return getUniqueRow(BusinessAdministrator.COLUMN_USERNAME, username);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_ADMINISTRATORS;
	}

	@SuppressWarnings("deprecation")
	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_ADMINISTRATOR, args, 17, err)) {
				connector.getSimpleAOClient().addBusinessAdministrator(
					args[1],
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
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = SimpleAOClient.checkBusinessAdministratorPassword(
					args[1],
					args[2]
				);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_BUSINESS_ADMINISTRATOR_USERNAME, args, 1, err)) {
				SimpleAOClient.checkBusinessAdministratorUsername(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_BUSINESS_ADMINISTRATOR, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableBusinessAdministrator(
						args[1],
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
				connector.getSimpleAOClient().enableBusinessAdministrator(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_BUSINESS_ADMINISTRATOR_PASSWORD_SET, args, 1, err)) {
				out.println(
					connector.getSimpleAOClient().isBusinessAdministratorPasswordSet(
						args[1]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BUSINESS_ADMINISTRATOR)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_BUSINESS_ADMINISTRATOR, args, 1, err)) {
				connector.getSimpleAOClient().removeBusinessAdministrator(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setBusinessAdministratorPassword(
					args[1],
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PROFILE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ADMINISTRATOR_PROFILE, args, 16, err)) {
				connector.getSimpleAOClient().setBusinessAdministratorProfile(
					args[1],
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
		} else if(command.equalsIgnoreCase(AOSHCommand.CRYPT)) {
			if(AOSH.checkRangeParamCount(AOSHCommand.CRYPT, args, 1, 2, err)) {
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
