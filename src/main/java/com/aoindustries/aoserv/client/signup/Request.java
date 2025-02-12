/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.signup;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoapps.net.InetAddress;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Stores a single sign-up request.
 *
 * @author  AO Industries, Inc.
 */
public final class Request extends CachedObjectIntegerKey<Request> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_BRAND = 1;
  static final String COLUMN_BRAND_name = "brand";
  static final String COLUMN_TIME_name = "time";

  private Account.Name brand;
  private UnmodifiableTimestamp time;
  private InetAddress ipAddress;
  private int packageDefinition;
  private String businessName;
  private String businessPhone;
  private String businessFax;
  private String businessAddress1;
  private String businessAddress2;
  private String businessCity;
  private String businessState;
  private String businessCountry;
  private String businessZip;
  private String baName;
  private String baTitle;
  private String baWorkPhone;
  private String baCellPhone;
  private String baHomePhone;
  private String baFax;
  private Email baEmail;
  private String baAddress1;
  private String baAddress2;
  private String baCity;
  private String baState;
  private String baCountry;
  private String baZip;
  private User.Name baUsername;
  private String billingContact;
  private Email billingEmail;
  private boolean billingUseMonthly;
  private boolean billingPayOneYear;
  private String encryptedData;
  private int encryptionFrom;
  private int encryptionRecipient;
  private User.Name completedBy;
  private UnmodifiableTimestamp completedTime;

  // These are not pulled from the database, but are decrypted from encrypted_data by GPG
  private /*transient*/ String decryptPassphrase;
  private /*transient*/ String baPassword;
  private /*transient*/ String billingCardholderName;
  private /*transient*/ String billingCardNumber;
  private /*transient*/ String billingExpirationMonth;
  private /*transient*/ String billingExpirationYear;
  private /*transient*/ String billingStreetAddress;
  private /*transient*/ String billingCity;
  private /*transient*/ String billingState;
  private /*transient*/ String billingZip;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Request() {
    // Do nothing
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_BRAND:
        return brand;
      case 2:
        return time;
      case 3:
        return ipAddress;
      case 4:
        return packageDefinition;
      case 5:
        return businessName;
      case 6:
        return businessPhone;
      case 7:
        return businessFax;
      case 8:
        return businessAddress1;
      case 9:
        return businessAddress2;
      case 10:
        return businessCity;
      case 11:
        return businessState;
      case 12:
        return businessCountry;
      case 13:
        return businessZip;
      case 14:
        return baName;
      case 15:
        return baTitle;
      case 16:
        return baWorkPhone;
      case 17:
        return baCellPhone;
      case 18:
        return baHomePhone;
      case 19:
        return baFax;
      case 20:
        return baEmail;
      case 21:
        return baAddress1;
      case 22:
        return baAddress2;
      case 23:
        return baCity;
      case 24:
        return baState;
      case 25:
        return baCountry;
      case 26:
        return baZip;
      case 27:
        return baUsername;
      case 28:
        return billingContact;
      case 29:
        return billingEmail;
      case 30:
        return billingUseMonthly;
      case 31:
        return billingPayOneYear;
      case 32:
        return encryptedData;
      case 33:
        return encryptionFrom;
      case 34:
        return encryptionRecipient;
      case 35:
        return completedBy;
      case 36:
        return completedTime;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SIGNUP_REQUESTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      brand = Account.Name.valueOf(result.getString(pos++));
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      ipAddress = InetAddress.valueOf(result.getString(pos++));
      packageDefinition = result.getInt(pos++);
      businessName = result.getString(pos++);
      businessPhone = result.getString(pos++);
      businessFax = result.getString(pos++);
      businessAddress1 = result.getString(pos++);
      businessAddress2 = result.getString(pos++);
      businessCity = result.getString(pos++);
      businessState = result.getString(pos++);
      businessCountry = result.getString(pos++);
      businessZip = result.getString(pos++);
      baName = result.getString(pos++);
      baTitle = result.getString(pos++);
      baWorkPhone = result.getString(pos++);
      baCellPhone = result.getString(pos++);
      baHomePhone = result.getString(pos++);
      baFax = result.getString(pos++);
      baEmail = Email.valueOf(result.getString(pos++));
      baAddress1 = result.getString(pos++);
      baAddress2 = result.getString(pos++);
      baCity = result.getString(pos++);
      baState = result.getString(pos++);
      baCountry = result.getString(pos++);
      baZip = result.getString(pos++);
      baUsername = User.Name.valueOf(result.getString(pos++));
      billingContact = result.getString(pos++);
      billingEmail = Email.valueOf(result.getString(pos++));
      billingUseMonthly = result.getBoolean(pos++);
      billingPayOneYear = result.getBoolean(pos++);
      encryptedData = result.getString(pos++);
      encryptionFrom = result.getInt(pos++);
      encryptionRecipient = result.getInt(pos++);
      completedBy = User.Name.valueOf(result.getString(pos++));
      completedTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      brand = Account.Name.valueOf(in.readUTF()).intern();
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      ipAddress = InetAddress.valueOf(in.readUTF());
      packageDefinition = in.readCompressedInt();
      businessName = in.readUTF();
      businessPhone = in.readUTF();
      businessFax = in.readNullUTF();
      businessAddress1 = in.readUTF();
      businessAddress2 = in.readNullUTF();
      businessCity = in.readUTF();
      businessState = InternUtils.intern(in.readNullUTF());
      businessCountry = in.readUTF().intern();
      businessZip = in.readNullUTF();
      baName = in.readUTF();
      baTitle = in.readNullUTF();
      baWorkPhone = in.readUTF();
      baCellPhone = in.readNullUTF();
      baHomePhone = in.readNullUTF();
      baFax = in.readNullUTF();
      baEmail = Email.valueOf(in.readUTF());
      baAddress1 = in.readNullUTF();
      baAddress2 = in.readNullUTF();
      baCity = in.readNullUTF();
      baState = InternUtils.intern(in.readNullUTF());
      baCountry = InternUtils.intern(in.readNullUTF());
      baZip = in.readNullUTF();
      baUsername = User.Name.valueOf(in.readUTF()).intern();
      billingContact = in.readUTF();
      billingEmail = Email.valueOf(in.readUTF());
      billingUseMonthly = in.readBoolean();
      billingPayOneYear = in.readBoolean();
      encryptedData = in.readUTF();
      encryptionFrom = in.readCompressedInt();
      encryptionRecipient = in.readCompressedInt();
      completedBy = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      completedTime = SQLStreamables.readNullUnmodifiableTimestamp(in);
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(brand.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(time.getTime());
    } else {
      SQLStreamables.writeTimestamp(time, out);
    }
    out.writeUTF(ipAddress.toString());
    out.writeCompressedInt(packageDefinition);
    out.writeUTF(businessName);
    out.writeUTF(businessPhone);
    out.writeNullUTF(businessFax);
    out.writeUTF(businessAddress1);
    out.writeNullUTF(businessAddress2);
    out.writeUTF(businessCity);
    out.writeNullUTF(businessState);
    out.writeUTF(businessCountry);
    out.writeNullUTF(businessZip);
    out.writeUTF(baName);
    out.writeNullUTF(baTitle);
    out.writeUTF(baWorkPhone);
    out.writeNullUTF(baCellPhone);
    out.writeNullUTF(baHomePhone);
    out.writeNullUTF(baFax);
    out.writeUTF(baEmail.toString());
    out.writeNullUTF(baAddress1);
    out.writeNullUTF(baAddress2);
    out.writeNullUTF(baCity);
    out.writeNullUTF(baState);
    out.writeNullUTF(baCountry);
    out.writeNullUTF(baZip);
    out.writeUTF(baUsername.toString());
    out.writeUTF(billingContact);
    out.writeUTF(billingEmail.toString());
    out.writeBoolean(billingUseMonthly);
    out.writeBoolean(billingPayOneYear);
    out.writeUTF(encryptedData);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31) >= 0) {
      out.writeCompressedInt(encryptionFrom);
    }
    out.writeCompressedInt(encryptionRecipient); // Used to be called encryption_key
    out.writeNullUTF(Objects.toString(completedBy, null));
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(completedTime == null ? -1 : completedTime.getTime());
    } else {
      SQLStreamables.writeNullTimestamp(completedTime, out);
    }
  }

  public Brand getBrand() throws SQLException, IOException {
    Brand br = table.getConnector().getReseller().getBrand().get(brand);
    if (br == null) {
      throw new SQLException("Unable to find Brand: " + brand);
    }
    return br;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  public InetAddress getIpAddress() {
    return ipAddress;
  }

  public PackageDefinition getPackageDefinition() throws SQLException, IOException {
    PackageDefinition pd = table.getConnector().getBilling().getPackageDefinition().get(packageDefinition);
    if (pd == null) {
      throw new SQLException("Unable to find PackageDefinition: " + packageDefinition);
    }
    return pd;
  }

  public String getBusinessName() {
    return businessName;
  }

  public String getBusinessPhone() {
    return businessPhone;
  }

  public String getBusinessFax() {
    return businessFax;
  }

  public String getBusinessAddress1() {
    return businessAddress1;
  }

  public String getBusinessAddress2() {
    return businessAddress2;
  }

  public String getBusinessCity() {
    return businessCity;
  }

  public String getBusinessState() {
    return businessState;
  }

  public String getBusinessCountry() {
    return businessCountry;
  }

  public String getBusinessZip() {
    return businessZip;
  }

  public String getBaName() {
    return baName;
  }

  public String getBaTitle() {
    return baTitle;
  }

  public String getBaWorkPhone() {
    return baWorkPhone;
  }

  public String getBaCellPhone() {
    return baCellPhone;
  }

  public String getBaHomePhone() {
    return baHomePhone;
  }

  public String getBaFax() {
    return baFax;
  }

  public Email getBaEmail() {
    return baEmail;
  }

  public String getBaAddress1() {
    return baAddress1;
  }

  public String getBaAddress2() {
    return baAddress2;
  }

  public String getBaCity() {
    return baCity;
  }

  public String getBaState() {
    return baState;
  }

  public String getBaCountry() {
    return baCountry;
  }

  public String getBaZip() {
    return baZip;
  }

  public User.Name getBaUsername() {
    return baUsername;
  }

  public String getBillingContact() {
    return billingContact;
  }

  public Email getBillingEmail() {
    return billingEmail;
  }

  public boolean getBillingUseMonthly() {
    return billingUseMonthly;
  }

  public boolean getBillingPayOneYear() {
    return billingPayOneYear;
  }

  public EncryptionKey getEncryptionFrom() throws SQLException, IOException {
    EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(encryptionFrom);
    if (ek == null) {
      throw new SQLException("Unable to find EncryptionKey: " + encryptionFrom);
    }
    return ek;
  }

  public EncryptionKey getEncryptionRecipient() throws SQLException, IOException {
    EncryptionKey er = table.getConnector().getPki().getEncryptionKey().get(encryptionRecipient);
    if (er == null) {
      throw new SQLException("Unable to find EncryptionKey: " + encryptionRecipient);
    }
    return er;
  }

  public Administrator getCompletedBy() throws IOException, SQLException {
    if (completedBy == null) {
      return null;
    }
    // May be filtered, null is OK
    return table.getConnector().getAccount().getAdministrator().get(completedBy);
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCompletedTime() {
    return completedTime;
  }

  public synchronized String getBaPassword(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return baPassword;
  }

  public synchronized String getBillingCardholderName(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingCardholderName;
  }

  public synchronized String getBillingCardNumber(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingCardNumber;
  }

  public synchronized String getBillingExpirationMonth(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingExpirationMonth;
  }

  public synchronized String getBillingExpirationYear(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingExpirationYear;
  }

  public synchronized String getBillingStreetAddress(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingStreetAddress;
  }

  public synchronized String getBillingCity(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingCity;
  }

  public synchronized String getBillingState(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingState;
  }

  public synchronized String getBillingZip(String passphrase) throws IOException, SQLException {
    decrypt(passphrase);
    return billingZip;
  }

  private synchronized void decrypt(String passphrase) throws IOException, SQLException {
    // If a different passphrase is provided, don't use the cached values, clear, and re-decrypt
    if (decryptPassphrase == null || !passphrase.equals(decryptPassphrase)) {
      // Clear first just in case there is a problem in part of the decryption
      decryptPassphrase = null;
      baPassword = null;
      billingCardholderName = null;
      billingCardNumber = null;
      billingExpirationMonth = null;
      billingExpirationYear = null;
      billingStreetAddress = null;
      billingCity = null;
      billingState = null;
      billingZip = null;

      // Perform the decryption
      String decrypted = getEncryptionRecipient().decrypt(encryptedData, passphrase);

      // Parse
      List<String> lines = Strings.splitLines(decrypted);

      // Store the values
      if (lines.size() == 9) {
        // 9-line format
        baPassword = lines.get(0);
        billingCardholderName = lines.get(1);
        billingCardNumber = lines.get(2);
        billingExpirationMonth = lines.get(3);
        billingExpirationYear = lines.get(4);
        billingStreetAddress = lines.get(5);
        billingCity = lines.get(6);
        billingState = lines.get(7);
        billingZip = lines.get(8);
      } else {
        throw new IOException("Unexpected number of lines after decryption: " + lines.size());
      }
      decryptPassphrase = passphrase;
    }
  }
}
