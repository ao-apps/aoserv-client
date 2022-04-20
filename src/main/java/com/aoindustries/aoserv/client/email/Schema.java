/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

  private final AddressTable Address;
  public AddressTable getAddress() {return Address;}

  private final AttachmentBlockTable AttachmentBlock;
  public AttachmentBlockTable getAttachmentBlock() {return AttachmentBlock;}

  private final AttachmentTypeTable AttachmentType;
  public AttachmentTypeTable getAttachmentType() {return AttachmentType;}

  private final BlackholeAddressTable BlackholeAddress;
  public BlackholeAddressTable getBlackholeAddress() {return BlackholeAddress;}

  private final CyrusImapdBindTable CyrusImapdBind;
  public CyrusImapdBindTable getCyrusImapdBind() {return CyrusImapdBind;}

  private final CyrusImapdServerTable CyrusImapdServer;
  public CyrusImapdServerTable getCyrusImapdServer() {return CyrusImapdServer;}

  private final DomainTable Domain;
  public DomainTable getDomain() {return Domain;}

  private final ForwardingTable Forwarding;
  public ForwardingTable getForwarding() {return Forwarding;}

  private final InboxAddressTable InboxAddress;
  public InboxAddressTable getInboxAddress() {return InboxAddress;}

  private final ListTable List;
  public ListTable getList() {return List;}

  private final ListAddressTable ListAddress;
  public ListAddressTable getListAddress() {return ListAddress;}

  private final MajordomoListTable MajordomoList;
  public MajordomoListTable getMajordomoList() {return MajordomoList;}

  private final MajordomoServerTable MajordomoServer;
  public MajordomoServerTable getMajordomoServer() {return MajordomoServer;}

  private final MajordomoVersionTable MajordomoVersion;
  public MajordomoVersionTable getMajordomoVersion() {return MajordomoVersion;}

  private final PipeTable Pipe;
  public PipeTable getPipe() {return Pipe;}

  private final PipeAddressTable PipeAddress;
  public PipeAddressTable getPipeAddress() {return PipeAddress;}

  private final SendmailBindTable SendmailBind;
  public SendmailBindTable getSendmailBind() {return SendmailBind;}

  private final SendmailServerTable SendmailServer;
  public SendmailServerTable getSendmailServer() {return SendmailServer;}

  private final SmtpRelayTable SmtpRelay;
  public SmtpRelayTable getSmtpRelay() {return SmtpRelay;}

  private final SmtpRelayTypeTable SmtpRelayType;
  public SmtpRelayTypeTable getSmtpRelayType() {return SmtpRelayType;}

  private final SmtpSmartHostTable SmtpSmartHost;
  public SmtpSmartHostTable getSmtpSmartHost() {return SmtpSmartHost;}

  private final SmtpSmartHostDomainTable SmtpSmartHostDomain;
  public SmtpSmartHostDomainTable getSmtpSmartHostDomain() {return SmtpSmartHostDomain;}

  private final SpamAssassinModeTable SpamAssassinMode;
  public SpamAssassinModeTable getSpamAssassinMode() {return SpamAssassinMode;}

  private final SpamMessageTable SpamMessage;
  public SpamMessageTable getSpamMessage() {return SpamMessage;}

  private final SystemAliasTable SystemAlias;
  public SystemAliasTable getSystemAlias() {return SystemAlias;}

  private final List<? extends AOServTable<?, ?>> tables;

  public Schema(AOServConnector connector) {
    super(connector);

    ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
    newTables.add(Address = new AddressTable(connector));
    newTables.add(AttachmentBlock = new AttachmentBlockTable(connector));
    newTables.add(AttachmentType = new AttachmentTypeTable(connector));
    newTables.add(BlackholeAddress = new BlackholeAddressTable(connector));
    newTables.add(CyrusImapdBind = new CyrusImapdBindTable(connector));
    newTables.add(CyrusImapdServer = new CyrusImapdServerTable(connector));
    newTables.add(Domain = new DomainTable(connector));
    newTables.add(Forwarding = new ForwardingTable(connector));
    newTables.add(InboxAddress = new InboxAddressTable(connector));
    newTables.add(List = new ListTable(connector));
    newTables.add(ListAddress = new ListAddressTable(connector));
    newTables.add(MajordomoList = new MajordomoListTable(connector));
    newTables.add(MajordomoServer = new MajordomoServerTable(connector));
    newTables.add(MajordomoVersion = new MajordomoVersionTable(connector));
    newTables.add(Pipe = new PipeTable(connector));
    newTables.add(PipeAddress = new PipeAddressTable(connector));
    newTables.add(SendmailBind = new SendmailBindTable(connector));
    newTables.add(SendmailServer = new SendmailServerTable(connector));
    newTables.add(SmtpRelay = new SmtpRelayTable(connector));
    newTables.add(SmtpRelayType = new SmtpRelayTypeTable(connector));
    newTables.add(SmtpSmartHost = new SmtpSmartHostTable(connector));
    newTables.add(SmtpSmartHostDomain = new SmtpSmartHostDomainTable(connector));
    newTables.add(SpamAssassinMode = new SpamAssassinModeTable(connector));
    newTables.add(SpamMessage = new SpamMessageTable(connector));
    newTables.add(SystemAlias = new SystemAliasTable(connector));
    newTables.trimToSize();
    tables = Collections.unmodifiableList(newTables);
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public List<? extends AOServTable<?, ?>> getTables() {
    return tables;
  }

  @Override
  public String getName() {
    return "email";
  }
}
