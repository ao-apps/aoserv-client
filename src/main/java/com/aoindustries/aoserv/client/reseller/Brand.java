/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.reseller;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.HostAddress;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.CachedObjectAccountNameKey;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A brand has separate website, packages, nameservers, and support.
 *
 * @see  Account
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
public final class Brand extends CachedObjectAccountNameKey<Brand> {

  static final int COLUMN_ACCOUNTING = 0;
  static final String COLUMN_ACCOUNTING_name = "accounting";

  private DomainName nameserver1;
  private DomainName nameserver2;
  private DomainName nameserver3;
  private DomainName nameserver4;
  private int smtpLinuxServerAccount;
  private HostAddress smtpHost;
  private String smtpPassword;
  private int imapLinuxServerAccount;
  private HostAddress imapHost;
  private String imapPassword;
  private int supportEailAddress;
  private String supportEmailDisplay;
  private int signupEmailAddress;
  private String signupEmailDisplay;
  private int ticketEncryptionFrom;
  private int ticketEncryptionRecipient;
  private int signupEncryptionFrom;
  private int signupEncryptionRecipient;
  private String supportTollFree;
  private String supportDayPhone;
  private String supportEmergencyPhone1;
  private String supportEmergencyPhone2;
  private String supportFax;
  private String supportMailingAddress1;
  private String supportMailingAddress2;
  private String supportMailingAddress3;
  private String supportMailingAddress4;
  private boolean englishEnabled;
  private boolean japaneseEnabled;
  private String aowebStrutsHttpUrlBase;
  private String aowebStrutsHttpsUrlBase;
  private String aowebStrutsGoogleVerifyContent;
  private boolean aowebStrutsNoindex;
  private String aowebStrutsGoogleAnalyticsNewTrackingCode;
  private String aowebAtrutsSignupAdminAddress;
  private int aowebStrutsVncBind;
  private String aowebStrutsKeystoreType;
  private String aowebStrutsKeystorePassword;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Brand() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ACCOUNTING:
        return pkey;
      case 1:
        return nameserver1;
      case 2:
        return nameserver2;
      case 3:
        return nameserver3;
      case 4:
        return nameserver4;
      case 5:
        return smtpLinuxServerAccount;
      case 6:
        return smtpHost;
      case 7:
        return smtpPassword;
      case 8:
        return imapLinuxServerAccount;
      case 9:
        return imapHost;
      case 10:
        return imapPassword;
      case 11:
        return supportEailAddress;
      case 12:
        return supportEmailDisplay;
      case 13:
        return signupEmailAddress;
      case 14:
        return signupEmailDisplay;
      case 15:
        return ticketEncryptionFrom;
      case 16:
        return ticketEncryptionRecipient;
      case 17:
        return signupEncryptionFrom;
      case 18:
        return signupEncryptionRecipient;
      case 19:
        return supportTollFree;
      case 20:
        return supportDayPhone;
      case 21:
        return supportEmergencyPhone1;
      case 22:
        return supportEmergencyPhone2;
      case 23:
        return supportFax;
      case 24:
        return supportMailingAddress1;
      case 25:
        return supportMailingAddress2;
      case 26:
        return supportMailingAddress3;
      case 27:
        return supportMailingAddress4;
      case 28:
        return englishEnabled;
      case 29:
        return japaneseEnabled;
      case 30:
        return aowebStrutsHttpUrlBase;
      case 31:
        return aowebStrutsHttpsUrlBase;
      case 32:
        return aowebStrutsGoogleVerifyContent;
      case 33:
        return aowebStrutsNoindex;
      case 34:
        return aowebStrutsGoogleAnalyticsNewTrackingCode;
      case 35:
        return aowebAtrutsSignupAdminAddress;
      case 36:
        return aowebStrutsVncBind;
      case 37:
        return aowebStrutsKeystoreType;
      case 38:
        return aowebStrutsKeystorePassword;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Account.Name getAccount_name() {
    return pkey;
  }

  public Account getAccount() throws IOException, SQLException {
    Account obj = table.getConnector().getAccount().getAccount().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find Account: " + pkey);
    }
    return obj;
  }

  public DomainName getNameserver1() {
    return nameserver1;
  }

  public DomainName getNameserver2() {
    return nameserver2;
  }

  public DomainName getNameserver3() {
    return nameserver3;
  }

  public DomainName getNameserver4() {
    return nameserver4;
  }

  public UserServer getSmtpLinuxServerAccount() throws IOException, SQLException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(smtpLinuxServerAccount);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + smtpLinuxServerAccount);
    }
    return lsa;
  }

  /**
   * Gets the host that should be used for SMTP.  Will use the hostname
   * of the SmtpLinuxServerAccount's {@link Server} if smtp_host is null.
   */
  public HostAddress getSmtpHost() throws IOException, SQLException {
    return smtpHost != null ? smtpHost : HostAddress.valueOf(getSmtpLinuxServerAccount().getServer().getHostname());
  }

  public String getSmtpPassword() {
    return smtpPassword;
  }

  public UserServer getImapLinuxServerAccount() throws SQLException, IOException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(imapLinuxServerAccount);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + imapLinuxServerAccount);
    }
    return lsa;
  }

  /**
   * Gets the host that should be used for IMAP.  Will use the hostname
   * of the ImapLinuxServerAccount's {@link Server} if imap_host is null.
   */
  public HostAddress getImapHost() throws IOException, SQLException {
    return imapHost != null ? imapHost : HostAddress.valueOf(getImapLinuxServerAccount().getServer().getHostname());
  }

  public String getImapPassword() {
    return imapPassword;
  }

  public Address getSupportEmailAddress() throws IOException, SQLException {
    Address ea = table.getConnector().getEmail().getAddress().get(supportEailAddress);
    if (ea == null) {
      throw new SQLException("Unable to find EmailAddress: " + supportEailAddress);
    }
    return ea;
  }

  public String getSupportEmailDisplay() {
    return supportEmailDisplay;
  }

  public Address getSignupEmailAddress() throws IOException, SQLException {
    Address ea = table.getConnector().getEmail().getAddress().get(signupEmailAddress);
    if (ea == null) {
      throw new SQLException("Unable to find EmailAddress: " + signupEmailAddress);
    }
    return ea;
  }

  public String getSignupEmailDisplay() {
    return signupEmailDisplay;
  }

  public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
    EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(ticketEncryptionFrom);
    if (ek == null) {
      throw new SQLException("Unable to find EncryptionKey: " + ticketEncryptionFrom);
    }
    return ek;
  }

  public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
    EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(ticketEncryptionRecipient);
    if (ek == null) {
      throw new SQLException("Unable to find EncryptionKey: " + ticketEncryptionRecipient);
    }
    return ek;
  }

  public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
    EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(signupEncryptionFrom);
    if (ek == null) {
      throw new SQLException("Unable to find EncryptionKey: " + signupEncryptionFrom);
    }
    return ek;
  }

  public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
    EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(signupEncryptionRecipient);
    if (ek == null) {
      throw new SQLException("Unable to find EncryptionKey: " + signupEncryptionRecipient);
    }
    return ek;
  }

  public String getSupportTollFree() {
    return supportTollFree;
  }

  public String getSupportDayPhone() {
    return supportDayPhone;
  }

  public String getSupportEmergencyPhone1() {
    return supportEmergencyPhone1;
  }

  public String getSupportEmergencyPhone2() {
    return supportEmergencyPhone2;
  }

  public String getSupportFax() {
    return supportFax;
  }

  public String getSupportMailingAddress1() {
    return supportMailingAddress1;
  }

  public String getSupportMailingAddress2() {
    return supportMailingAddress2;
  }

  public String getSupportMailingAddress3() {
    return supportMailingAddress3;
  }

  public String getSupportMailingAddress4() {
    return supportMailingAddress4;
  }

  public boolean getEnglishEnabled() {
    return englishEnabled;
  }

  public boolean getJapaneseEnabled() {
    return japaneseEnabled;
  }

  public String getAowebStrutsHttpUrlBase() {
    return aowebStrutsHttpUrlBase;
  }

  public URL getAowebStrutsHttpUrl() throws MalformedURLException {
    return new URL(aowebStrutsHttpUrlBase);
  }

  public String getAowebStrutsHttpsUrlBase() {
    return aowebStrutsHttpsUrlBase;
  }

  public URL getAowebStrutsHttpsUrl() throws MalformedURLException {
    return new URL(aowebStrutsHttpsUrlBase);
  }

  public String getAowebStrutsGoogleVerifyContent() {
    return aowebStrutsGoogleVerifyContent;
  }

  public boolean getAowebStrutsNoindex() {
    return aowebStrutsNoindex;
  }

  public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
    return aowebStrutsGoogleAnalyticsNewTrackingCode;
  }

  public String getAowebStrutsSignupAdminAddress() {
    return aowebAtrutsSignupAdminAddress;
  }

  public Bind getAowebStrutsVncBind() throws IOException, SQLException {
    Bind nb = table.getConnector().getNet().getBind().get(aowebStrutsVncBind);
    if (nb == null) {
      throw new SQLException("Unable to find NetBind: " + aowebStrutsVncBind);
    }
    return nb;
  }

  public String getAowebStrutsKeystoreType() {
    return aowebStrutsKeystoreType;
  }

  public String getAowebStrutsKeystorePassword() {
    return aowebStrutsKeystorePassword;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BRANDS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = Account.Name.valueOf(result.getString(pos++));
      nameserver1 = DomainName.valueOf(result.getString(pos++));
      nameserver2 = DomainName.valueOf(result.getString(pos++));
      nameserver3 = DomainName.valueOf(result.getString(pos++));
      nameserver4 = DomainName.valueOf(result.getString(pos++));
      smtpLinuxServerAccount = result.getInt(pos++);
      smtpHost = HostAddress.valueOf(result.getString(pos++));
      smtpPassword = result.getString(pos++);
      imapLinuxServerAccount = result.getInt(pos++);
      imapHost = HostAddress.valueOf(result.getString(pos++));
      imapPassword = result.getString(pos++);
      supportEailAddress = result.getInt(pos++);
      supportEmailDisplay = result.getString(pos++);
      signupEmailAddress = result.getInt(pos++);
      signupEmailDisplay = result.getString(pos++);
      ticketEncryptionFrom = result.getInt(pos++);
      ticketEncryptionRecipient = result.getInt(pos++);
      signupEncryptionFrom = result.getInt(pos++);
      signupEncryptionRecipient = result.getInt(pos++);
      supportTollFree = result.getString(pos++);
      supportDayPhone = result.getString(pos++);
      supportEmergencyPhone1 = result.getString(pos++);
      supportEmergencyPhone2 = result.getString(pos++);
      supportFax = result.getString(pos++);
      supportMailingAddress1 = result.getString(pos++);
      supportMailingAddress2 = result.getString(pos++);
      supportMailingAddress3 = result.getString(pos++);
      supportMailingAddress4 = result.getString(pos++);
      englishEnabled = result.getBoolean(pos++);
      japaneseEnabled = result.getBoolean(pos++);
      aowebStrutsHttpUrlBase = result.getString(pos++);
      aowebStrutsHttpsUrlBase = result.getString(pos++);
      aowebStrutsGoogleVerifyContent = result.getString(pos++);
      aowebStrutsNoindex = result.getBoolean(pos++);
      aowebStrutsGoogleAnalyticsNewTrackingCode = result.getString(pos++);
      aowebAtrutsSignupAdminAddress = result.getString(pos++);
      aowebStrutsVncBind = result.getInt(pos++);
      aowebStrutsKeystoreType = result.getString(pos++);
      aowebStrutsKeystorePassword = result.getString(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = Account.Name.valueOf(in.readUTF()).intern();
      nameserver1 = DomainName.valueOf(in.readUTF());
      nameserver2 = DomainName.valueOf(in.readUTF());
      nameserver3 = DomainName.valueOf(in.readNullUTF());
      nameserver4 = DomainName.valueOf(in.readNullUTF());
      smtpLinuxServerAccount = in.readCompressedInt();
      smtpHost = HostAddress.valueOf(in.readNullUTF());
      smtpPassword = in.readUTF();
      imapLinuxServerAccount = in.readCompressedInt();
      imapHost = HostAddress.valueOf(in.readNullUTF());
      imapPassword = in.readUTF();
      supportEailAddress = in.readCompressedInt();
      supportEmailDisplay = in.readUTF();
      signupEmailAddress = in.readCompressedInt();
      signupEmailDisplay = in.readUTF();
      ticketEncryptionFrom = in.readCompressedInt();
      ticketEncryptionRecipient = in.readCompressedInt();
      signupEncryptionFrom = in.readCompressedInt();
      signupEncryptionRecipient = in.readCompressedInt();
      supportTollFree = in.readNullUTF();
      supportDayPhone = in.readNullUTF();
      supportEmergencyPhone1 = in.readNullUTF();
      supportEmergencyPhone2 = in.readNullUTF();
      supportFax = in.readNullUTF();
      supportMailingAddress1 = in.readNullUTF();
      supportMailingAddress2 = in.readNullUTF();
      supportMailingAddress3 = in.readNullUTF();
      supportMailingAddress4 = in.readNullUTF();
      englishEnabled = in.readBoolean();
      japaneseEnabled = in.readBoolean();
      aowebStrutsHttpUrlBase = in.readUTF();
      aowebStrutsHttpsUrlBase = in.readUTF();
      aowebStrutsGoogleVerifyContent = in.readNullUTF();
      aowebStrutsNoindex = in.readBoolean();
      aowebStrutsGoogleAnalyticsNewTrackingCode = in.readNullUTF();
      aowebAtrutsSignupAdminAddress = in.readUTF();
      aowebStrutsVncBind = in.readCompressedInt();
      aowebStrutsKeystoreType = in.readUTF();
      aowebStrutsKeystorePassword = in.readUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeUTF(nameserver1.toString());
    out.writeUTF(nameserver2.toString());
    out.writeNullUTF(Objects.toString(nameserver3, null));
    out.writeNullUTF(Objects.toString(nameserver4, null));
    out.writeCompressedInt(smtpLinuxServerAccount);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_46) >= 0) {
      out.writeNullUTF(Objects.toString(smtpHost, null));
    }
    out.writeUTF(smtpPassword);
    out.writeCompressedInt(imapLinuxServerAccount);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_46) >= 0) {
      out.writeNullUTF(Objects.toString(imapHost, null));
    }
    out.writeUTF(imapPassword);
    out.writeCompressedInt(supportEailAddress);
    out.writeUTF(supportEmailDisplay);
    out.writeCompressedInt(signupEmailAddress);
    out.writeUTF(signupEmailDisplay);
    out.writeCompressedInt(ticketEncryptionFrom);
    out.writeCompressedInt(ticketEncryptionRecipient);
    out.writeCompressedInt(signupEncryptionFrom);
    out.writeCompressedInt(signupEncryptionRecipient);
    out.writeNullUTF(supportTollFree);
    out.writeNullUTF(supportDayPhone);
    out.writeNullUTF(supportEmergencyPhone1);
    out.writeNullUTF(supportEmergencyPhone2);
    out.writeNullUTF(supportFax);
    out.writeNullUTF(supportMailingAddress1);
    out.writeNullUTF(supportMailingAddress2);
    out.writeNullUTF(supportMailingAddress3);
    out.writeNullUTF(supportMailingAddress4);
    out.writeBoolean(englishEnabled);
    out.writeBoolean(japaneseEnabled);
    out.writeUTF(aowebStrutsHttpUrlBase);
    out.writeUTF(aowebStrutsHttpsUrlBase);
    out.writeNullUTF(aowebStrutsGoogleVerifyContent);
    out.writeBoolean(aowebStrutsNoindex);
    out.writeNullUTF(aowebStrutsGoogleAnalyticsNewTrackingCode);
    out.writeUTF(aowebAtrutsSignupAdminAddress);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_52) >= 0) {
      out.writeCompressedInt(aowebStrutsVncBind);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_53) >= 0) {
      out.writeUTF(aowebStrutsKeystoreType);
      out.writeUTF(aowebStrutsKeystorePassword);
    }
  }

  /**
   * Gets the Reseller for this Brand or {@code null} if not a reseller.
   */
  public Reseller getReseller() throws IOException, SQLException {
    return table.getConnector().getReseller().getReseller().getReseller(this);
  }

  public List<BrandCategory> getTicketBrandCategories() throws IOException, SQLException {
    return table.getConnector().getReseller().getBrandCategory().getTicketBrandCategories(this);
  }

  /**
   * Gets the immediate parent of this brand or {@code null} if none available.
   */
  public Brand getParentBrand() throws IOException, SQLException {
    Account account = getAccount();
    Account parent = account.getParent();
    while (parent != null) {
      Brand parentBrand = parent.getBrand();
      if (parentBrand != null) {
        return parentBrand;
      }
    }
    return null;
  }

  /**
   * The children of the brand are any brands that have their closest parent
   * business (that is a brand) equal to this one.
   */
  public List<Brand> getChildBrands() throws IOException, SQLException {
    List<Brand> children = new ArrayList<>();
    for (Brand brand : table.getConnector().getReseller().getBrand().getRows()) {
      if (!brand.equals(this) && this.equals(brand.getParentBrand())) {
        children.add(brand);
      }
    }
    return children;
  }
}
