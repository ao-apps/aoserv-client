/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.account;

import com.aoapps.collections.AoCollections;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Contact information associated with an {@link Account}.
 *
 * @author  AO Industries, Inc.
 */
public final class Profile extends CachedObjectIntegerKey<Profile> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_ACCOUNTING = 1;
  static final String COLUMN_ACCOUNTING_name = "accounting";
  static final String COLUMN_PRIORITY_name = "priority";

  private Account.Name accounting;
  private int priority;

  private String name;

  private boolean isPrivate;
  private String phone;
  private String fax;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String country;
  private String zip;

  private boolean sendInvoice;

  private UnmodifiableTimestamp created;

  /**
   * The set of possible units.
   */
  // Matches aoserv-master-db/aoindustries/account/Profile.EmailFormat-type.sql
  public enum EmailFormat {
    /**
     * HTML allowing embedded images.
     */
    HTML,

    /**
     * HTML without any embedded images.
     */
    HTML_ONLY,

    /**
     * Plaintext only.
     */
    TEXT
  }

  private String billingContact;
  private Set<Email> billingEmail;
  private EmailFormat billingEmailFormat;
  private String technicalContact;
  private Set<Email> technicalEmail;
  private EmailFormat technicalEmailFormat;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Profile() {
    // Do nothing
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getBillingContact() {
    return billingContact;
  }

  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public Set<Email> getBillingEmail() {
    return billingEmail;
  }

  public EmailFormat getBillingEmailFormat() {
    return billingEmailFormat;
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  public Account getAccount() throws SQLException, IOException {
    Account obj = table.getConnector().getAccount().getAccount().get(accounting);
    if (obj == null) {
      throw new SQLException("Unable to find Account: " + accounting);
    }
    return obj;
  }

  public String getCity() {
    return city;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_ACCOUNTING:
        return accounting;
      case 2:
        return priority;
      case 3:
        return name;
      case 4:
        return isPrivate;
      case 5:
        return phone;
      case 6:
        return fax;
      case 7:
        return address1;
      case 8:
        return address2;
      case 9:
        return city;
      case 10:
        return state;
      case 11:
        return country;
      case 12:
        return zip;
      case 13:
        return sendInvoice;
      case 14:
        return created;
      case 15:
        return billingContact;
      // TODO: Support array types
      case 16:
        return Strings.join(billingEmail, ", ");
      case 17:
        return billingEmailFormat;
      case 18:
        return technicalContact;
      // TODO: Support array types
      case 19:
        return Strings.join(technicalEmail, ", ");
      case 20:
        return technicalEmailFormat;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getCountry_code() {
    return country;
  }

  public CountryCode getCountry() throws SQLException, IOException {
    CountryCode countryCode = table.getConnector().getPayment().getCountryCode().get(country);
    if (countryCode == null) {
      throw new SQLException("CountryCode not found: " + country);
    }
    return countryCode;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  public String getFax() {
    return fax;
  }

  public String getName() {
    return name;
  }

  public String getPhone() {
    return phone;
  }

  public int getPriority() {
    return priority;
  }

  public String getState() {
    return state;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BUSINESS_PROFILES;
  }

  public String getTechnicalContact() {
    return technicalContact;
  }

  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public Set<Email> getTechnicalEmail() {
    return technicalEmail;
  }

  public EmailFormat getTechnicalEmailFormat() {
    return technicalEmailFormat;
  }

  public String getZip() {
    return zip;
  }

  /**
   * Splits emails into an unmodifiable set.
   */
  @SuppressWarnings("AssignmentToForLoopParameter")
  public static Set<Email> splitEmails(String value) throws ValidationException {
    List<String> split = Strings.splitCommaSpace(value);
    Set<Email> emails = AoCollections.newLinkedHashSet(split.size());
    for (String s : split) {
      s = s.trim();
      if (!s.isEmpty()) {
        emails.add(Email.valueOf(s));
      }
    }
    return AoCollections.optimalUnmodifiableSet(emails);
  }

  /**
   * @param  array  is freed via {@link Array#free()}
   */
  private static Set<Email> getEmailSet(Array array) throws SQLException, ValidationException {
    try {
      if (USE_SQL_DATA && USE_ARRAY_OF_DOMAIN) {
        // This does not locate duplicates like the ResultSet implementation below
        return new LinkedHashSet<>(Arrays.asList((Email[]) array.getArray()));
      } else {
        Set<Email> set = new LinkedHashSet<>();
        try (ResultSet result = array.getResultSet()) {
          while (result.next()) {
            Email email = Email.valueOf(result.getString(2));
            if (!set.add(email)) {
              throw new SQLException("Email not unique: " + email);
            }
          }
        }
        return set;
      }
    } finally {
      array.free();
    }
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      accounting = Account.Name.valueOf(result.getString(pos++));
      priority = result.getInt(pos++);
      name = result.getString(pos++);
      isPrivate = result.getBoolean(pos++);
      phone = result.getString(pos++);
      fax = result.getString(pos++);
      address1 = result.getString(pos++);
      address2 = result.getString(pos++);
      city = result.getString(pos++);
      state = result.getString(pos++);
      country = result.getString(pos++);
      zip = result.getString(pos++);
      sendInvoice = result.getBoolean(pos++);
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      billingContact = result.getString(pos++);
      // TODO: Array in PostgreSQL
      String billingEmailArray = result.getString(pos++);
      try {
        billingEmail = splitEmails(billingEmailArray);
      } catch (ValidationException e) {
        throw new SQLException("billing_email = " + billingEmailArray, e);
      }
      billingEmailFormat = EmailFormat.valueOf(result.getString(pos++));
      technicalContact = result.getString(pos++);
      // TODO: Array in PostgreSQL
      String technicalEmailArray = result.getString(pos++);
      try {
        technicalEmail = splitEmails(technicalEmailArray);
      } catch (ValidationException e) {
        throw new SQLException("technical_email = " + technicalEmailArray, e);
      }
      technicalEmailFormat = EmailFormat.valueOf(result.getString(pos++));
      Set<Email> billingEmailNew = getEmailSet(result.getArray("billingEmail{}"));
      if (!billingEmailNew.equals(billingEmail)) {
        throw new SQLException("billingEmailNew != billingEmail: " + billingEmailNew + " != " + billingEmail);
      }
      Set<Email> technicalEmailNew = getEmailSet(result.getArray("technicalEmail{}"));
      if (!technicalEmailNew.equals(technicalEmail)) {
        throw new SQLException("technicalEmailNew != technicalEmail: " + technicalEmailNew + " != " + technicalEmail);
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  /*
  private static String makeEmailList(String[] addresses) {
    StringBuilder sb = new StringBuilder();
    int len = addresses.length;
    for (int c = 0; c < len; c++) {
      if (c > 0) {
        sb.append(' ');
      }
      sb.append(addresses[c]);
    }
    return sb.toString();
  }*/

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      accounting = Account.Name.valueOf(in.readUTF()).intern();
      priority = in.readCompressedInt();
      name = in.readUTF();
      isPrivate = in.readBoolean();
      phone = in.readUTF();
      fax = in.readNullUTF();
      address1 = in.readUTF();
      address2 = in.readNullUTF();
      city = in.readUTF();
      state = InternUtils.intern(in.readNullUTF());
      country = in.readUTF().intern();
      zip = in.readNullUTF();
      sendInvoice = in.readBoolean();
      created = SQLStreamables.readUnmodifiableTimestamp(in);
      billingContact = in.readUTF();
      {
        int size = in.readCompressedInt();
        Set<Email> emails = AoCollections.newLinkedHashSet(size);
        for (int i = 0; i < size; i++) {
          emails.add(Email.valueOf(in.readUTF()));
        }
        billingEmail = AoCollections.optimalUnmodifiableSet(emails);
      }
      billingEmailFormat = in.readEnum(EmailFormat.class);
      technicalContact = in.readUTF();
      {
        int size = in.readCompressedInt();
        Set<Email> emails = AoCollections.newLinkedHashSet(size);
        for (int i = 0; i < size; i++) {
          emails.add(Email.valueOf(in.readUTF()));
        }
        technicalEmail = AoCollections.optimalUnmodifiableSet(emails);
      }
      technicalEmailFormat = in.readEnum(EmailFormat.class);
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public boolean sendInvoice() {
    return sendInvoice;
  }

  @Override
  public String toStringImpl() {
    return name + " (" + priority + ')';
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(accounting.toString());
    out.writeCompressedInt(priority);
    out.writeUTF(name);
    out.writeBoolean(isPrivate);
    out.writeUTF(phone);
    out.writeNullUTF(fax);
    out.writeUTF(address1);
    out.writeNullUTF(address2);
    out.writeUTF(city);
    out.writeNullUTF(state);
    out.writeUTF(country);
    out.writeNullUTF(zip);
    out.writeBoolean(sendInvoice);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(created.getTime());
    } else {
      SQLStreamables.writeTimestamp(created, out);
    }
    out.writeUTF(billingContact);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_22) < 0) {
      out.writeUTF(Strings.join(billingEmail, ", "));
    } else {
      int size = billingEmail.size();
      out.writeCompressedInt(size);
      for (Email email : billingEmail) {
        out.writeUTF(email.toString());
      }
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_20) >= 0) {
      out.writeEnum(billingEmailFormat);
    }
    out.writeUTF(technicalContact);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_22) < 0) {
      out.writeUTF(Strings.join(technicalEmail, ", "));
    } else {
      int size = technicalEmail.size();
      out.writeCompressedInt(size);
      for (Email email : technicalEmail) {
        out.writeUTF(email.toString());
      }
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_20) >= 0) {
      out.writeEnum(technicalEmailFormat);
    }
  }
}
