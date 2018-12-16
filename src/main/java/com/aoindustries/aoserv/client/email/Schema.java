/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final AddressTable addressTable;
	public AddressTable getEmailAddresses() {return addressTable;}

	private final AttachmentBlockTable attachmentBlockTable;
	public AttachmentBlockTable getEmailAttachmentBlocks() {return attachmentBlockTable;}

	private final AttachmentTypeTable attachmentTypeTable;
	public AttachmentTypeTable getEmailAttachmentTypes() {return attachmentTypeTable;}

	private final BlackholeAddressTable blackholeAddressTable;
	public BlackholeAddressTable getBlackholeEmailAddresses() {return blackholeAddressTable;}

	private final CyrusImapdBindTable cyrusImapdBindTable;
	public CyrusImapdBindTable getCyrusImapdBinds() {return cyrusImapdBindTable;}

	private final CyrusImapdServerTable cyrusImapdServerTable;
	public CyrusImapdServerTable getCyrusImapdServers() {return cyrusImapdServerTable;}

	private final DomainTable domainTable;
	public DomainTable getEmailDomains() {return domainTable;}

	private final ForwardingTable forwardingTable;
	public ForwardingTable getEmailForwardings() {return forwardingTable;}

	private final InboxAddressTable inboxAddressTable;
	public InboxAddressTable getLinuxAccAddresses() {return inboxAddressTable;}

	private final ListTable listTable;
	public ListTable getEmailLists() {return listTable;}

	private final ListAddressTable listAddressTable;
	public ListAddressTable getEmailListAddresses() {return listAddressTable;}

	private final MajordomoListTable majordomoListTable;
	public MajordomoListTable getMajordomoLists() {return majordomoListTable;}

	private final MajordomoServerTable majordomoServerTable;
	public MajordomoServerTable getMajordomoServers() {return majordomoServerTable;}

	private final MajordomoVersionTable majordomoVersionTable;
	public MajordomoVersionTable getMajordomoVersions() {return majordomoVersionTable;}

	private final PipeTable pipeTable;
	public PipeTable getEmailPipes() {return pipeTable;}

	private final PipeAddressTable pipeAddressTable;
	public PipeAddressTable getEmailPipeAddresses() {return pipeAddressTable;}

	private final SendmailBindTable sendmailBindTable;
	public SendmailBindTable getSendmailBinds() {return sendmailBindTable;}

	private final SendmailServerTable sendmailServerTable;
	public SendmailServerTable getSendmailServers() {return sendmailServerTable;}

	private final SmtpRelayTable smtpRelayTable;
	public SmtpRelayTable getEmailSmtpRelays() {return smtpRelayTable;}

	private final SmtpRelayTypeTable smtpRelayTypeTable;
	public SmtpRelayTypeTable getEmailSmtpRelayTypes() {return smtpRelayTypeTable;}

	private final SmtpSmartHostTable smtpSmartHostTable;
	public SmtpSmartHostTable getEmailSmtpSmartHosts() {return smtpSmartHostTable;}

	private final SmtpSmartHostDomainTable smtpSmartHostDomainTable;
	public SmtpSmartHostDomainTable getEmailSmtpSmartHostDomains() {return smtpSmartHostDomainTable;}

	private final SpamAssassinModeTable spamAssassinModeTable;
	public SpamAssassinModeTable getEmailSpamAssassinIntegrationModes() {return spamAssassinModeTable;}

	private final SpamMessageTable spamMessageTable;
	public SpamMessageTable getSpamEmailMessages() {return spamMessageTable;}

	private final SystemAliasTable systemAliasTable;
	public SystemAliasTable getSystemEmailAliases() {return systemAliasTable;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(addressTable = new AddressTable(connector));
		newTables.add(attachmentBlockTable = new AttachmentBlockTable(connector));
		newTables.add(attachmentTypeTable = new AttachmentTypeTable(connector));
		newTables.add(blackholeAddressTable = new BlackholeAddressTable(connector));
		newTables.add(cyrusImapdBindTable = new CyrusImapdBindTable(connector));
		newTables.add(cyrusImapdServerTable = new CyrusImapdServerTable(connector));
		newTables.add(domainTable = new DomainTable(connector));
		newTables.add(forwardingTable = new ForwardingTable(connector));
		newTables.add(inboxAddressTable = new InboxAddressTable(connector));
		newTables.add(listTable = new ListTable(connector));
		newTables.add(listAddressTable = new ListAddressTable(connector));
		newTables.add(majordomoListTable = new MajordomoListTable(connector));
		newTables.add(majordomoServerTable = new MajordomoServerTable(connector));
		newTables.add(majordomoVersionTable = new MajordomoVersionTable(connector));
		newTables.add(pipeTable = new PipeTable(connector));
		newTables.add(pipeAddressTable = new PipeAddressTable(connector));
		newTables.add(sendmailBindTable = new SendmailBindTable(connector));
		newTables.add(sendmailServerTable = new SendmailServerTable(connector));
		newTables.add(smtpRelayTable = new SmtpRelayTable(connector));
		newTables.add(smtpRelayTypeTable = new SmtpRelayTypeTable(connector));
		newTables.add(smtpSmartHostTable = new SmtpSmartHostTable(connector));
		newTables.add(smtpSmartHostDomainTable = new SmtpSmartHostDomainTable(connector));
		newTables.add(spamAssassinModeTable = new SpamAssassinModeTable(connector));
		newTables.add(spamMessageTable = new SpamMessageTable(connector));
		newTables.add(systemAliasTable = new SystemAliasTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "email";
	}
}
