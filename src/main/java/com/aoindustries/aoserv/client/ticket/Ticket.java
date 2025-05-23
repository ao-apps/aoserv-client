/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.ticket;

import com.aoapps.collections.AoCollections;
import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.reseller.Reseller;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The <code>Ticket</code> system allows clients to submit support
 * requests and monitor the progress of the work.
 *
 * @see  Action
 * @see  Priority
 * @see  TicketType
 *
 * @author  AO Industries, Inc.
 */
public final class Ticket extends CachedObjectIntegerKey<Ticket> {

  // <editor-fold desc="Fields">
  private Account.Name brand;
  private Account.Name reseller;
  private Account.Name accounting;
  private String language;
  private User.Name createdBy;
  private int category;
  private String ticketType;
  private Email fromAddress;
  private String summary;
  private boolean detailsLoaded;
  private String details;
  private boolean rawEmailLoaded;
  private String rawEmail;
  private UnmodifiableTimestamp openDate;
  private String clientPriority;
  private String adminPriority;
  private String status;
  private UnmodifiableTimestamp statusTimeout;
  private Set<Email> contactEmails;
  private String contactPhoneNumbers;
  private boolean internalNotesLoaded;
  private String internalNotes;

  // </editor-fold>

  // <editor-fold desc="Object implementation">
  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Ticket() {
    // Do nothing
  }

  @Override
  public String toStringImpl() {
    if (reseller != null) {
      return pkey + "|" + brand + '/' + accounting + '|' + status + "→" + reseller;
    } else {
      return pkey + "|" + brand + '/' + accounting + '|' + status;
    }
  }
  // </editor-fold>

  // <editor-fold desc="AoservObject implementation">
  static final int COLUMN_PKEY = 0;
  static final int COLUMN_ACCOUNTING = 3;
  static final int COLUMN_CREATED_BY = 5;
  static final int COLUMN_OPEN_DATE = 12;
  static final String COLUMN_PKEY_name = "pkey";
  static final String COLUMN_OPEN_DATE_name = "open_date";

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) throws IOException, SQLException {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return brand;
      case 2:
        return reseller;
      case COLUMN_ACCOUNTING:
        return accounting;
      case 4:
        return language;
      case COLUMN_CREATED_BY:
        return createdBy;
      case 6:
        return category == -1 ? null : category;
      case 7:
        return ticketType;
      case 8:
        return fromAddress;
      case 9:
        return summary;
      case 10:
        return getDetails();
      case 11:
        return getRawEmail();
      case COLUMN_OPEN_DATE:
        return openDate;
      case 13:
        return clientPriority;
      case 14:
        return adminPriority;
      case 15:
        return status;
      case 16:
        return statusTimeout;
      // TODO: Support array types
      case 17:
        return Strings.join(contactEmails, ", ");
      case 18:
        return contactPhoneNumbers;
      case 19:
        return getInternalNotes();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKETS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      brand = Account.Name.valueOf(result.getString(pos++));
      reseller = Account.Name.valueOf(result.getString(pos++));
      accounting = Account.Name.valueOf(result.getString(pos++));
      language = result.getString(pos++);
      createdBy = User.Name.valueOf(result.getString(pos++));
      category = result.getInt(pos++);
      if (result.wasNull()) {
        category = -1;
      }
      ticketType = result.getString(pos++);
      fromAddress = Email.valueOf(result.getString(pos++));
      summary = result.getString(pos++);
      openDate = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      clientPriority = result.getString(pos++);
      adminPriority = result.getString(pos++);
      status = result.getString(pos++);
      statusTimeout = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      // TODO: Array in PostgreSQL
      String str = result.getString(pos++);
      try {
        contactEmails = Profile.splitEmails(str);
      } catch (ValidationException e) {
        throw new SQLException("contact_emails = " + str, e);
      }
      contactPhoneNumbers = result.getString(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      brand = Account.Name.valueOf(in.readUTF()).intern();
      String resellerStr = in.readUTF();
      if (AoservProtocol.FILTERED.equals(resellerStr)) {
        reseller = null;
      } else {
        reseller = Account.Name.valueOf(resellerStr).intern();
      }
      accounting = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
      language = in.readUTF().intern();
      createdBy = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      category = in.readCompressedInt();
      ticketType = in.readUTF().intern();
      fromAddress = Email.valueOf(in.readNullUTF());
      summary = in.readUTF();
      openDate = SQLStreamables.readUnmodifiableTimestamp(in);
      clientPriority = in.readUTF().intern();
      adminPriority = InternUtils.intern(in.readNullUTF());
      status = in.readUTF().intern();
      statusTimeout = SQLStreamables.readNullUnmodifiableTimestamp(in);
      {
        int size = in.readCompressedInt();
        Set<Email> emails = AoCollections.newLinkedHashSet(size);
        for (int i = 0; i < size; i++) {
          emails.add(Email.valueOf(in.readUTF()));
        }
        contactEmails = AoCollections.optimalUnmodifiableSet(emails);
      }
      contactPhoneNumbers = in.readUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_46) >= 0) {
      out.writeUTF(brand.toString());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeUTF(reseller == null ? AoservProtocol.FILTERED : reseller.toString());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_125) <= 0) {
      out.writeUTF(accounting == null ? "" : accounting.toString());
    } else {
      out.writeNullUTF(Objects.toString(accounting, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeUTF(language);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_125) <= 0) {
      out.writeUTF(createdBy == null ? "" : createdBy.toString());
    } else {
      out.writeNullUTF(Objects.toString(createdBy, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeCompressedInt(category);
    }
    out.writeUTF(ticketType);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeNullUTF(Objects.toString(fromAddress, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeUTF(summary);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_14) <= 0) {
      out.writeUTF("");
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_15) >= 0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      // details
      out.writeCompressedInt(0);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(openDate.getTime());
    } else {
      SQLStreamables.writeTimestamp(openDate, out);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      out.writeLong(-1);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      out.writeLong(-1);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      out.writeNullUTF(null);
    }
    out.writeUTF(clientPriority);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_10) < 0) {
      out.writeUTF(adminPriority == null ? clientPriority : adminPriority);
    } else {
      out.writeNullUTF(adminPriority);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      // technology
      out.writeNullUTF(null);
    }
    out.writeUTF(status);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
        out.writeLong(statusTimeout == null ? -1 : statusTimeout.getTime());
      } else {
        SQLStreamables.writeNullTimestamp(statusTimeout, out);
      }
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_125) >= 0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      // assigned_to
      out.writeNullUTF(null);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_125) >= 0) {
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_22) < 0) {
        out.writeUTF(Strings.join(contactEmails, ", "));
      } else {
        int size = contactEmails.size();
        out.writeCompressedInt(size);
        for (Email email : contactEmails) {
          out.writeUTF(email.toString());
        }
      }
      out.writeUTF(contactPhoneNumbers);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Accessors">
  public int getTicketId() {
    return pkey;
  }

  /**
   * May be filtered.
   */
  public Brand getBrand() throws IOException, SQLException {
    return table.getConnector().getReseller().getBrand().get(brand);
  }

  /**
   * May be null when filtered.
   */
  public Reseller getReseller() throws SQLException, IOException {
    if (reseller == null) {
      return null;
    }
    return table.getConnector().getReseller().getReseller().get(reseller);
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  /**
   * May be null if not set or filtered.
   */
  public Account getAccount() throws SQLException, IOException {
    if (accounting == null) {
      return null;
    }
    return table.getConnector().getAccount().getAccount().get(accounting);
  }

  public Language getLanguage() throws SQLException, IOException {
    Language la = table.getConnector().getTicket().getLanguage().get(language);
    if (la == null) {
      throw new SQLException("Unable to find Language: " + language);
    }
    return la;
  }

  public Administrator getCreatedBy() throws IOException, SQLException {
    if (createdBy == null) {
      return null;
    }
    // Data may be filtered by APIs
    return table.getConnector().getAccount().getAdministrator().get(createdBy);
  }

  public Category getCategory() throws IOException, SQLException {
    if (category == -1) {
      return null;
    }
    Category tc = table.getConnector().getReseller().getCategory().get(category);
    if (tc == null) {
      throw new SQLException("Unable to find TicketCategory: " + category);
    }
    return tc;
  }

  public TicketType getTicketType() throws IOException, SQLException {
    TicketType ticketTypeObject = table.getConnector().getTicket().getTicketType().get(ticketType);
    if (ticketTypeObject  == null) {
      throw new SQLException("Unable to find TicketType: " + ticketType);
    }
    return ticketTypeObject;
  }

  public Email getFromAddress() {
    return fromAddress;
  }

  public String getSummary() {
    return summary;
  }

  public synchronized String getDetails() throws IOException, SQLException {
    if (!detailsLoaded) {
      details = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_DETAILS, pkey);
      detailsLoaded = true;
    }
    return details;
  }

  public synchronized String getRawEmail() throws IOException, SQLException {
    if (!rawEmailLoaded) {
      rawEmail = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_RAW_EMAIL, pkey);
      rawEmailLoaded = true;
    }
    return rawEmail;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getOpenDate() {
    return openDate;
  }

  public Priority getClientPriority() throws IOException, SQLException {
    Priority clientPriorityObject = table.getConnector().getTicket().getPriority().get(clientPriority);
    if (clientPriorityObject == null) {
      throw new SQLException("Unable to find Priority: " + clientPriority);
    }
    return clientPriorityObject;
  }

  public Priority getAdminPriority() throws IOException, SQLException {
    if (adminPriority == null) {
      return null;
    }
    Priority adminPriorityObject = table.getConnector().getTicket().getPriority().get(adminPriority);
    if (adminPriorityObject == null) {
      throw new SQLException("Unable to find Priority: " + adminPriority);
    }
    return adminPriorityObject;
  }

  public Status getStatus() throws IOException, SQLException {
    Status statusObject = table.getConnector().getTicket().getStatus().get(status);
    if (statusObject == null) {
      throw new SQLException("Unable to find status: " + status);
    }
    return statusObject;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getStatusTimeout() {
    return statusTimeout;
  }

  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public Set<Email> getContactEmails() {
    return contactEmails;
  }

  public String getContactPhoneNumbers() {
    return contactPhoneNumbers;
  }

  public synchronized String getInternalNotes() throws IOException, SQLException {
    if (!internalNotesLoaded) {
      internalNotes = table.getConnector().requestLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_INTERNAL_NOTES, pkey);
      internalNotesLoaded = true;
    }
    return internalNotes;
  }

  // </editor-fold>

  // <editor-fold desc="Data Access">
  public List<Action> getTicketActions() throws IOException, SQLException {
    return table.getConnector().getTicket().getAction().getActions(this);
  }

  public List<Assignment> getTicketAssignments() throws IOException, SQLException {
    return table.getConnector().getTicket().getAssignment().getTicketAssignments(this);
  }

  // </editor-fold>

  // <editor-fold desc="Ticket Actions">
  /*
  public void actBounceTicket(Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.BOUNCE_TICKET, pkey, administrator.getUsername_id(), comments);
  }*/

  public void actChangeAdminPriority(Priority priority, Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.CHANGE_TICKET_ADMIN_PRIORITY,
        pkey,
        priority == null ? "" : priority.getPriority(), administrator.getUsername_userId(),
        comments
    );
  }

  public void setClientPriority(Priority clientPriority) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.CHANGE_TICKET_CLIENT_PRIORITY, pkey, clientPriority.getPriority());
  }

  public void setSummary(String summary) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_TICKET_SUMMARY, pkey, summary);
  }

  public void addAnnotation(final String summary, final String details) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.ADD_TICKET_ANNOTATION,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeUTF(summary);
            out.writeNullLongUTF(details);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  public void actAssignTo(Administrator assignedTo, Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_TICKET_ASSIGNED_TO,
        pkey,
        assignedTo == null ? "" : assignedTo.getUsername().getUsername(),
        administrator.getUsername_userId(),
        comments
    );
  }

  public void setContactEmails(final Set<Email> contactEmails) throws IOException, SQLException {
    final AoservConnector connector = table.getConnector();
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.SET_TICKET_CONTACT_EMAILS,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(contactEmails.size());
            for (Email email : contactEmails) {
              out.writeUTF(email.toString());
            }
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            connector.tablesUpdated(invalidateList);
          }
        }
    );
  }

  public void setContactPhoneNumbers(String contactPhoneNumbers) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_TICKET_CONTACT_PHONE_NUMBERS, pkey, contactPhoneNumbers);
  }

  /*
  public void actCompleteTicket(Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.COMPLETE_TICKET, pkey, administrator.getUsername_userId(), comments);
  }*/

  /*
  public void actHoldTicket(String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.HOLD_TICKET, pkey, comments);
  }*/

  /*
  public void actKillTicket(Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.KILL_TICKET, pkey, administrator.getUsername_userId(), comments);
  }*/

  /*
  public void actReactivateTicket(Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REACTIVATE_TICKET, pkey, administrator.getUsername_userId(), comments);
  }*/

  public void actWorkEntry(Administrator administrator, String comments) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.TICKET_WORK, pkey, administrator.getUsername_userId(), comments);
  }

  /**
   * Updates the ticket business if the old business matches the current value.
   *
   * @return <code>true</code> if successfully updated or <code>false</code> if oldAccount doesn't match the current business.
   */
  public boolean setAccount(Account oldAccount, Account newAccount) throws IOException, SQLException {
    return table.getConnector().requestBooleanQueryInvalidating(
        true,
        AoservProtocol.CommandId.SET_TICKET_BUSINESS,
        pkey,
        oldAccount == null ? "" : oldAccount.getName().toString(),
        newAccount == null ? "" : newAccount.getName().toString()
    );
  }

  /**
   * Updates the ticket type if the old value matches the current value.
   *
   * @return <code>true</code> if successfully updated or <code>false</code> if oldType doesn't match the current type.
   */
  public boolean setTicketType(TicketType oldType, TicketType newType) throws IOException, SQLException {
    return table.getConnector().requestBooleanQueryInvalidating(true, AoservProtocol.CommandId.CHANGE_TICKET_TYPE, pkey, oldType.getType(), newType.getType());
    // table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.CHANGE_TICKET_TYPE, pkey, ticket_type.pkey, business_administrator.getUsername_userId(), comments);
  }

  /**
   * Updates the ticket status if the old status matches the current value.
   *
   * @return <code>true</code> if successfully updated or <code>false</code> if oldStatus doesn't match the current status.
   */
  public boolean setStatus(Status oldStatus, Status newStatus, long statusTimeout) throws IOException, SQLException {
    return table.getConnector().requestBooleanQueryInvalidating(true, AoservProtocol.CommandId.SET_TICKET_STATUS, pkey, oldStatus.getStatus(), newStatus.getStatus(), statusTimeout);
  }

  /**
   * Updates the internal notes if the old value matches the current value.
   *
   * @return <code>true</code> if successfully updated or <code>false</code> if oldInternalNotes doesn't match the current internal notes.
   */
  public boolean setInternalNotes(final String oldInternalNotes, final String newInternalNotes) throws IOException, SQLException {
    return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandId.SET_TICKET_INTERNAL_NOTES,
        new AoservConnector.ResultRequest<>() {
          private boolean result;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeLongUTF(oldInternalNotes);
            out.writeLongUTF(newInternalNotes);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              result = in.readBoolean();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public Boolean afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
            return result;
          }
        }
    );
  }
  // </editor-fold>
}
