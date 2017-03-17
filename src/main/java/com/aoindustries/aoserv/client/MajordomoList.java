/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainName;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MajordomoList</code> is one list within a <code>MajordomoServer</code>.
 *
 * @see  MajordomoServer
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoList extends CachedObjectIntegerKey<MajordomoList> {

	static final int
		COLUMN_EMAIL_LIST=0,
		COLUMN_MAJORDOMO_SERVER=1
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_MAJORDOMO_SERVER_name = "majordomo_server";

	/**
	 * The maximum length of an email list name.
	 */
	public static final int MAX_NAME_LENGTH=64;

	int majordomo_server;
	String name;
	int listname_pipe_add;
	int listname_list_add;
	int owner_listname_add;
	int listname_owner_add;
	int listname_approval_add;
	int listname_request_pipe_add;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_EMAIL_LIST: return pkey;
			case COLUMN_MAJORDOMO_SERVER: return majordomo_server;
			case 2: return name;
			case 3: return listname_pipe_add;
			case 4: return listname_list_add;
			case 5: return owner_listname_add;
			case 6: return listname_owner_add;
			case 7: return listname_approval_add;
			case 8: return listname_request_pipe_add;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public static String getDefaultInfoFile(DomainName domain, String listName) {
		return
			  "Information about the "+listName+" mailing list:\n"
			+ "\n"
			+ "HOW TO POST A MESSAGE TO THE LIST:\n"
			+ "Just send an email message to "+listName+"@"+domain+". The message will\n"
			+ "be distributed to all the members of the list.\n"
			+ "\n"
			+ "HOW TO UNSUBSCRIBE:\n"
			+ "Send an email message to majordomo@"+domain+" with one line in the\n"
			+ "body of the message:\n"
			+ "\n"
			+ "unsubscribe "+listName+"\n"
			+ "\n"
			+ "\n"
			+ "FOR QUESTIONS:\n"
			+ "If you ever need to get in contact with the owner of the list,\n"
			+ "(if you have trouble unsubscribing, or have questions about the\n"
			+ "list itself) send email to owner-"+listName+'@'+domain+".\n"
		;
	}

	public String getDefaultInfoFile() throws SQLException, IOException {
		return getDefaultInfoFile(getMajordomoServer().getDomain().getDomain(), name);
	}

	public static String getDefaultIntroFile(DomainName domain, String listName) {
		return
			  "Welcome to the "+listName+" mailing list.\n"
			+ "\n"
			+ "Please save this message for future reference.\n"
			+ "\n"
			+ "HOW TO POST A MESSAGE TO THE LIST:\n"
			+ "Just send an email message to "+listName+'@'+domain+". The message will\n"
			+ "be distributed to all the members of the list.\n"
			+ "\n"
			+ "HOW TO UNSUBSCRIBE:\n"
			+ "Send an email message to majordomo@"+domain+" with one line in the\n"
			+ "body of the message:\n"
			+ "\n"
			+ "unsubscribe "+listName+"\n"
			+ "\n"
			+ "\n"
			+ "FOR QUESTIONS:\n"
			+ "If you ever need to get in contact with the owner of the list,\n"
			+ "(if you have trouble unsubscribing, or have questions about the\n"
			+ "list itself) send email to owner-"+listName+'@'+domain+".\n"
		;
	}

	public String getDefaultIntroFile() throws SQLException, IOException {
		return getDefaultIntroFile(getMajordomoServer().getDomain().getDomain(), name);
	}

	public EmailList getEmailList() throws SQLException, IOException {
		EmailList obj=table.connector.getEmailLists().get(pkey);
		if(obj==null) throw new SQLException("Unable to find EmailList: "+pkey);
		return obj;
	}

	/**
	 * Gets the info file for the list.
	 */
	public String getInfoFile() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_MAJORDOMO_INFO_FILE, pkey);
	}

	/**
	 * Gets the intro file for the list.
	 */
	public String getIntroFile() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_MAJORDOMO_INTRO_FILE, pkey);
	}

	public EmailPipeAddress getListPipeAddress() throws SQLException, IOException {
		EmailPipeAddress pipeAddress=table.connector.getEmailPipeAddresses().get(listname_pipe_add);
		if(pipeAddress==null) throw new SQLException("Unable to find EmailPipeAddress: "+listname_pipe_add);
		return pipeAddress;
	}

	public EmailAddress getListApprovalAddress() throws SQLException, IOException {
		EmailAddress address=table.connector.getEmailAddresses().get(listname_approval_add);
		if(address==null) throw new SQLException("Unable to find EmailAddress: "+listname_approval_add);
		return address;
	}

	public EmailListAddress getListListAddress() throws SQLException, IOException {
		EmailListAddress listAddress=table.connector.getEmailListAddresses().get(listname_list_add);
		if(listAddress==null) throw new SQLException("Unable to find EmailListAddress: "+listname_list_add);
		return listAddress;
	}

	public EmailAddress getListOwnerAddress() throws SQLException, IOException {
		EmailAddress address=table.connector.getEmailAddresses().get(listname_owner_add);
		if(address==null) throw new SQLException("Unable to find EmailAddress: "+listname_owner_add);
		return address;
	}

	public EmailPipeAddress getListRequestPipeAddress() throws SQLException, IOException {
		EmailPipeAddress pipeAddress=table.connector.getEmailPipeAddresses().get(listname_request_pipe_add);
		if(pipeAddress==null) throw new SQLException("Unable to find EmailPipeAddress: "+listname_request_pipe_add);
		return pipeAddress;
	}

	public String getName() {
		return name;
	}

	public EmailAddress getOwnerListAddress() throws SQLException, IOException {
		EmailAddress address=table.connector.getEmailAddresses().get(owner_listname_add);
		if(address==null) throw new SQLException("Unable to find EmailAddress: "+owner_listname_add);
		return address;
	}

	public MajordomoServer getMajordomoServer() throws SQLException, IOException {
		MajordomoServer obj=table.connector.getMajordomoServers().get(majordomo_server);
		if(obj==null) throw new SQLException("Unable to find MajordomoServer: "+majordomo_server);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MAJORDOMO_LISTS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		majordomo_server=result.getInt(2);
		name=result.getString(3);
		listname_pipe_add=result.getInt(4);
		listname_list_add=result.getInt(5);
		owner_listname_add=result.getInt(6);
		listname_owner_add=result.getInt(7);
		listname_approval_add=result.getInt(8);
		listname_request_pipe_add=result.getInt(9);
	}

	/**
	 * Checks the validity of a list name.
	 */
	public static boolean isValidListName(String name) {
		int len = name.length();
		if (len < 1 || len > MAX_NAME_LENGTH) return false;
		for (int c = 0; c < len; c++) {
			char ch = name.charAt(c);
			if (c == 0) {
				if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) return false;
			} else {
				if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != '.' && ch != '-' && ch != '_') return false;
			}
		}
		return true;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		majordomo_server=in.readCompressedInt();
		name=in.readUTF();
		listname_pipe_add=in.readCompressedInt();
		listname_list_add=in.readCompressedInt();
		owner_listname_add=in.readCompressedInt();
		listname_owner_add=in.readCompressedInt();
		listname_approval_add=in.readCompressedInt();
		listname_request_pipe_add=in.readCompressedInt();
	}

	public void setInfoFile(String file) throws IOException, SQLException {
		table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_MAJORDOMO_INFO_FILE, pkey, file);
	}

	public void setIntroFile(String file) throws IOException, SQLException {
		table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_MAJORDOMO_INTRO_FILE, pkey, file);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return name+'@'+getMajordomoServer().getDomain().getDomain().toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(majordomo_server);
		out.writeUTF(name);
		out.writeCompressedInt(listname_pipe_add);
		out.writeCompressedInt(listname_list_add);
		out.writeCompressedInt(owner_listname_add);
		out.writeCompressedInt(listname_owner_add);
		out.writeCompressedInt(listname_approval_add);
		out.writeCompressedInt(listname_request_pipe_add);
	}
}
