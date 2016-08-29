/*
 * Copyright 2001-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  EmailSmtpRelay
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSmtpRelayTable extends CachedTableIntegerKey<EmailSmtpRelay> {

	EmailSmtpRelayTable(AOServConnector connector) {
		super(connector, EmailSmtpRelay.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailSmtpRelay.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(EmailSmtpRelay.COLUMN_HOST_name, ASCENDING),
		new OrderBy(EmailSmtpRelay.COLUMN_PACKAGE_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailSmtpRelay(final Package pack, final AOServer aoServer, final String host, final EmailSmtpRelayType type, final long duration) throws IOException, SQLException {
		return connector.requestResult(
			true,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.EMAIL_SMTP_RELAYS.ordinal());
					out.writeUTF(pack.name);
					out.writeCompressedInt(aoServer==null?-1:aoServer.pkey);
					out.writeUTF(host);
					out.writeUTF(type.pkey);
					out.writeLong(duration);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public EmailSmtpRelay get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailSmtpRelay.COLUMN_PKEY, pkey);
	}

	EmailSmtpRelay getEmailSmtpRelay(Package pk, AOServer ao, HostAddress host) throws IOException, SQLException {
		String packageName=pk.name;
		int aoPKey=ao.pkey;

		List<EmailSmtpRelay> cached = getRows();
		int len = cached.size();
		for (int c = 0; c < len; c++) {
			EmailSmtpRelay relay=cached.get(c);
			if(
				packageName.equals(relay.packageName)
				&& (relay.ao_server==-1 || relay.ao_server==aoPKey)
				&& host.equals(relay.getHost())
			) return relay;
		}
		return null;
	}

	List<EmailSmtpRelay> getEmailSmtpRelays(Package pk) throws IOException, SQLException {
		return getIndexedRows(EmailSmtpRelay.COLUMN_PACKAGE, pk.name);
	}

	List<EmailSmtpRelay> getEmailSmtpRelays(AOServer ao) throws IOException, SQLException {
		int aoPKey=ao.pkey;

		List<EmailSmtpRelay> cached = getRows();
		int len = cached.size();
		List<EmailSmtpRelay> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			EmailSmtpRelay relay = cached.get(c);
			if (relay.ao_server==-1 || relay.ao_server==aoPKey) matches.add(relay);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_SMTP_RELAYS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_SMTP_RELAY)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_SMTP_RELAY, args, 5, err)) {
				String S=args[5].trim();
				int pkey=connector.getSimpleAOClient().addEmailSmtpRelay(
					args[1],
					args[2],
					args[3],
					args[4],
					S.length()==0?-1:AOSH.parseLong(S, "duration")
				);
				out.println(pkey);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_SMTP_RELAY)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_SMTP_RELAY, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableEmailSmtpRelay(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_SMTP_RELAY)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_SMTP_RELAY, args, 1, err)) {
				connector.getSimpleAOClient().enableEmailSmtpRelay(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REFRESH_EMAIL_SMTP_RELAY)) {
			if(AOSH.checkParamCount(AOSHCommand.REFRESH_EMAIL_SMTP_RELAY, args, 1, err)) {
				connector.getSimpleAOClient().refreshEmailSmtpRelay(
					AOSH.parseInt(args[1], "pkey"),
					args[2].trim().length()==0?-1:AOSH.parseLong(args[2], "min_duration")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_SMTP_RELAY)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_SMTP_RELAY, args, 1, err)) {
				connector.getSimpleAOClient().removeEmailSmtpRelay(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		}
		return false;
	}
}
