/*
 * aoserv-client - Java client for the AOServ Platform.
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
import com.aoindustries.net.Email;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>EmailAddress</code> represents a unique email
 * address hosted on an AOServ server.  This email address
 * may only go to one destination,
 *
 *
 * @see  BlackholeEmailAddress
 * @see  EmailForwarding
 * @see  EmailListAddress
 * @see  EmailPipeAddress
 * @see  LinuxAccAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailAddress extends CachedObjectIntegerKey<EmailAddress> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_DOMAIN=2
	;
	static final String COLUMN_DOMAIN_name = "domain";
	static final String COLUMN_ADDRESS_name = "address";

	String address;
	int domain;

	public int addEmailForwarding(Email destination) throws IOException, SQLException {
		return table.connector.getEmailForwardings().addEmailForwarding(this, destination);
	}

	public String getAddress() {
		return address;
	}

	public BlackholeEmailAddress getBlackholeEmailAddress() throws IOException, SQLException {
		return table.connector.getBlackholeEmailAddresses().get(pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return address;
			case COLUMN_DOMAIN: return domain;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public EmailDomain getDomain() throws SQLException, IOException {
		EmailDomain domainObject = table.connector.getEmailDomains().get(domain);
		if (domainObject == null) throw new SQLException("Unable to find EmailDomain: " + domain);
		return domainObject;
	}

	public List<EmailForwarding> getEmailForwardings() throws IOException, SQLException {
		return table.connector.getEmailForwardings().getEmailForwardings(this);
	}

	public List<EmailForwarding> getEnabledEmailForwardings() throws SQLException, IOException {
		return table.connector.getEmailForwardings().getEnabledEmailForwardings(this);
	}

	public EmailForwarding getEmailForwarding(Email destination) throws IOException, SQLException {
		return table.connector.getEmailForwardings().getEmailForwarding(this, destination);
	}

	public List<EmailList> getEmailLists() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailLists(this);
	}

	public List<EmailListAddress> getEmailListAddresses() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailListAddresses(this);
	}

	public List<EmailListAddress> getEnabledEmailListAddresses() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEnabledEmailListAddresses(this);
	}

	public EmailListAddress getEmailListAddress(EmailList list) throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailListAddress(this, list);
	}

	public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().getEmailPipes(this);
	}

	public List<EmailPipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().getEmailPipeAddresses(this);
	}

	public List<EmailPipeAddress> getEnabledEmailPipeAddresses() throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().getEnabledEmailPipeAddresses(this);
	}

	public EmailPipeAddress getEmailPipeAddress(EmailPipe pipe) throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().getEmailPipeAddress(this, pipe);
	}

	public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
		return table.connector.getLinuxAccAddresses().getLinuxServerAccounts(this);
	}

	public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException, SQLException {
		return table.connector.getLinuxAccAddresses().getLinuxAccAddresses(this);
	}

	public LinuxAccAddress getLinuxAccAddress(LinuxServerAccount lsa) throws IOException, SQLException {
		return table.connector.getLinuxAccAddresses().getLinuxAccAddress(this, lsa);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_ADDRESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		address = result.getString(2);
		domain = result.getInt(3);
	}

	public boolean isUsed() throws IOException, SQLException {
		// Anything using this address must be removable
		return
			getBlackholeEmailAddress()!=null
			|| !getEmailForwardings().isEmpty()
			|| !getEmailListAddresses().isEmpty()
			|| !getEmailPipeAddresses().isEmpty()
			|| !getLinuxAccAddresses().isEmpty()
		;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		address=in.readUTF();
		domain=in.readCompressedInt();
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons = new ArrayList<>();

		// Everything using this address must be removable
		BlackholeEmailAddress bea=getBlackholeEmailAddress();
		if(bea!=null) reasons.addAll(bea.getCannotRemoveReasons());

		for(EmailForwarding ef : getEmailForwardings()) reasons.addAll(ef.getCannotRemoveReasons());

		for(EmailListAddress ela : table.connector.getEmailListAddresses().getEmailListAddresses(this)) reasons.addAll(ela.getCannotRemoveReasons());

		for(EmailPipeAddress epa : getEmailPipeAddresses()) reasons.addAll(epa.getCannotRemoveReasons());

		for(LinuxAccAddress laa : getLinuxAccAddresses()) reasons.addAll(laa.getCannotRemoveReasons());

		// Cannot be used as any part of a majordomo list
		for(MajordomoList ml : table.connector.getMajordomoLists().getRows()) {
			if(
				ml.owner_listname_add==pkey
				|| ml.listname_owner_add==pkey
				|| ml.listname_approval_add==pkey
			) {
				EmailDomain ed=ml.getMajordomoServer().getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
			}
		}

		// Cannot be used as any part of a majordomo server
		for(MajordomoServer ms : table.connector.getMajordomoServers().getRows()) {
			if(
				ms.owner_majordomo_add==pkey
				|| ms.majordomo_owner_add==pkey
			) {
				EmailDomain ed=ms.getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
			}
		}

		return reasons;
	}

	/**
	 * Removes this <code>EmailAddress</code> any any references to it.  For example, if
	 * this address is sent to a <code>LinuxAccount</code>, the address is disassociated
	 * before it is removed.  For integrity, this entire operation is handled in a single
	 * database transaction.
	 */
	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_ADDRESSES,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return address+'@'+getDomain().getDomain().toString();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(address);
		out.writeCompressedInt(domain);
	}
}
