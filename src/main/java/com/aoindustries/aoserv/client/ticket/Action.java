/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * <code>TicketAction</code>s represent a complete history of the changes that have been made to a ticket.
 * When a ticket is initially created it has no actions.  Any change from its initial state will cause
 * an action to be logged.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class Action extends CachedObjectIntegerKey<Action> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Action.class);

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_TICKET = 1;
  static final int COLUMN_ADMINISTRATOR = 2;
  static final int COLUMN_TIME = 3;
  static final String COLUMN_TICKET_name = "ticket";
  static final String COLUMN_TIME_name = "time";
  static final String COLUMN_PKEY_name = "pkey";

  private int ticket;
  private User.Name administrator;
  private UnmodifiableTimestamp time;
  private String actionType;
  private Account.Name oldAccounting;
  private Account.Name newAccounting;
  private String oldPriority;
  private String newPriority;
  private String oldType;
  private String newType;
  private String oldStatus;
  private String newStatus;
  private User.Name oldAssignedTo;
  private User.Name newAssignedTo;
  private int oldCategory;
  private int newCategory;
  private boolean oldValueLoaded;
  private String oldValue;
  private boolean newValueLoaded;
  private String newValue;
  private Email fromAddress;
  private String summary;
  private boolean detailsLoaded;
  private String details;
  private boolean rawEmailLoaded;
  private String rawEmail;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Action() {
    // Do nothing
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) throws IOException, SQLException {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_TICKET:
        return ticket;
      case COLUMN_ADMINISTRATOR:
        return administrator;
      case COLUMN_TIME:
        return time;
      case 4:
        return actionType;
      case 5:
        return oldAccounting;
      case 6:
        return newAccounting;
      case 7:
        return oldPriority;
      case 8:
        return newPriority;
      case 9:
        return oldType;
      case 10:
        return newType;
      case 11:
        return oldStatus;
      case 12:
        return newStatus;
      case 13:
        return oldAssignedTo;
      case 14:
        return newAssignedTo;
      case 15:
        return oldCategory == -1 ? null : oldCategory;
      case 16:
        return newCategory == -1 ? null : newCategory;
      case 17:
        return getOldValue();
      case 18:
        return getNewValue();
      case 19:
        return fromAddress;
      case 20:
        return summary;
      case 21:
        return getDetails();
      case 22:
        return getRawEmail();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Ticket getTicket() throws IOException, SQLException {
    Ticket t = table.getConnector().getTicket().getTicket().get(ticket);
    if (t == null) {
      throw new SQLException("Unable to find Ticket: " + ticket);
    }
    return t;
  }

  public Administrator getAdministrator() throws IOException, SQLException {
    if (administrator == null) {
      return null;
    }
    return table.getConnector().getAccount().getAdministrator().get(administrator);
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  public ActionType getTicketActionType() throws SQLException, IOException {
    ActionType type = table.getConnector().getTicket().getActionType().get(actionType);
    if (type == null) {
      throw new SQLException("Unable to find TicketActionType: " + actionType);
    }
    return type;
  }

  /**
   * May be null due to filtering.
   */
  public Account getOldAccount() throws IOException, SQLException {
    if (oldAccounting == null) {
      return null;
    }
    return table.getConnector().getAccount().getAccount().get(oldAccounting);
  }

  /**
   * May be null due to filtering.
   */
  public Account getNewAccount() throws IOException, SQLException {
    if (newAccounting == null) {
      return null;
    }
    return table.getConnector().getAccount().getAccount().get(newAccounting);
  }

  public Priority getOldPriority() throws IOException, SQLException {
    if (oldPriority == null) {
      return null;
    }
    Priority tp = table.getConnector().getTicket().getPriority().get(oldPriority);
    if (tp == null) {
      throw new SQLException("Unable to find TicketPriority: " + oldPriority);
    }
    return tp;
  }

  public Priority getNewPriority() throws IOException, SQLException {
    if (newPriority == null) {
      return null;
    }
    Priority tp = table.getConnector().getTicket().getPriority().get(newPriority);
    if (tp == null) {
      throw new SQLException("Unable to find TicketPriority: " + newPriority);
    }
    return tp;
  }

  public TicketType getOldType() throws IOException, SQLException {
    if (oldType == null) {
      return null;
    }
    TicketType tt = table.getConnector().getTicket().getTicketType().get(oldType);
    if (tt == null) {
      throw new SQLException("Unable to find TicketType: " + oldType);
    }
    return tt;
  }

  public TicketType getNewType() throws IOException, SQLException {
    if (newType == null) {
      return null;
    }
    TicketType tt = table.getConnector().getTicket().getTicketType().get(newType);
    if (tt == null) {
      throw new SQLException("Unable to find TicketType: " + newType);
    }
    return tt;
  }

  public Status getOldStatus() throws IOException, SQLException {
    if (oldStatus == null) {
      return null;
    }
    Status ts = table.getConnector().getTicket().getStatus().get(oldStatus);
    if (ts == null) {
      throw new SQLException("Unable to find TicketStatus: " + oldStatus);
    }
    return ts;
  }

  public Status getNewStatus() throws IOException, SQLException {
    if (newStatus == null) {
      return null;
    }
    Status ts = table.getConnector().getTicket().getStatus().get(newStatus);
    if (ts == null) {
      throw new SQLException("Unable to find TicketStatus: " + newStatus);
    }
    return ts;
  }

  /**
   * May be null due to filtering.
   */
  public Administrator getOldAssignedTo() throws IOException, SQLException {
    if (oldAssignedTo == null) {
      return null;
    }
    return table.getConnector().getAccount().getAdministrator().get(oldAssignedTo);
  }

  /**
   * May be null due to filtering.
   */
  public Administrator getNewAssignedTo() throws IOException, SQLException {
    if (newAssignedTo == null) {
      return null;
    }
    return table.getConnector().getAccount().getAdministrator().get(newAssignedTo);
  }

  public Category getOldCategory() throws IOException, SQLException {
    if (oldCategory == -1) {
      return null;
    }
    Category tc = table.getConnector().getReseller().getCategory().get(oldCategory);
    if (tc == null) {
      throw new SQLException("Unable to find TicketCategory: " + oldCategory);
    }
    return tc;
  }

  public Category getNewCategory() throws IOException, SQLException {
    if (newCategory == -1) {
      return null;
    }
    Category tc = table.getConnector().getReseller().getCategory().get(newCategory);
    if (tc == null) {
      throw new SQLException("Unable to find TicketCategory: " + newCategory);
    }
    return tc;
  }

  public synchronized String getOldValue() throws IOException, SQLException {
    if (!oldValueLoaded) {
      // Only perform the query for action types that have old values
      if (
          actionType.equals(ActionType.SET_CONTACT_EMAILS)
              || actionType.equals(ActionType.SET_CONTACT_PHONE_NUMBERS)
              || actionType.equals(ActionType.SET_SUMMARY)
              || actionType.equals(ActionType.SET_INTERNAL_NOTES)
      ) {
        oldValue = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_ACTION_OLD_VALUE, pkey);
      } else {
        oldValue = null;
      }
      oldValueLoaded = true;
    }
    return oldValue;
  }

  public synchronized String getNewValue() throws IOException, SQLException {
    if (!newValueLoaded) {
      // Only perform the query for action types that have new values
      if (
          actionType.equals(ActionType.SET_CONTACT_EMAILS)
              || actionType.equals(ActionType.SET_CONTACT_PHONE_NUMBERS)
              || actionType.equals(ActionType.SET_SUMMARY)
              || actionType.equals(ActionType.SET_INTERNAL_NOTES)
      ) {
        newValue = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_ACTION_NEW_VALUE, pkey);
      } else {
        newValue = null;
      }
      newValueLoaded = true;
    }
    return newValue;
  }

  public Email getFromAddress() {
    return fromAddress;
  }

  /**
   * Gets the summary for the provided Locale, may be generated for certain action types.
   */
  public String getSummary() throws IOException, SQLException {
    if (summary != null) {
      return summary;
    }
    final String oldValue;
    final String newValue;
    switch (actionType) {
      case ActionType.SET_BUSINESS:
        oldValue = Objects.toString(oldAccounting, null);
        newValue = Objects.toString(newAccounting, null);
        break;
      case ActionType.SET_CLIENT_PRIORITY:
      case ActionType.SET_ADMIN_PRIORITY:
        oldValue = oldPriority;
        newValue = newPriority;
        break;
      case ActionType.SET_TYPE:
        oldValue = getOldType().toStringImpl();
        newValue = getNewType().toStringImpl();
        break;
      case ActionType.SET_STATUS:
        oldValue = getOldStatus().toStringImpl();
        newValue = getNewStatus().toStringImpl();
        break;
      case ActionType.ASSIGN:
        Administrator oldAssignedTo = getOldAssignedTo();
        Administrator newAssignedTo = getNewAssignedTo();
        oldValue = oldAssignedTo != null ? oldAssignedTo.getName() : oldAssignedTo != null ? RESOURCES.getMessage("old_assigned_to.filtered") : null;
        newValue = newAssignedTo != null ? newAssignedTo.getName() : newAssignedTo != null ? RESOURCES.getMessage("new_assigned_to.filtered") : null;
        break;
      case ActionType.SET_CATEGORY:
        Category oldCategory = getOldCategory();
        Category newCategory = getNewCategory();
        oldValue = oldCategory != null ? oldCategory.toStringImpl() : null;
        newValue = newCategory != null ? newCategory.toStringImpl() : null;
        break;
      case ActionType.SET_CONTACT_EMAILS:
      case ActionType.SET_CONTACT_PHONE_NUMBERS:
      case ActionType.SET_SUMMARY:
      case ActionType.SET_INTERNAL_NOTES:
      case ActionType.ADD_ANNOTATION:
        // These either have no old/new value or their value is not altered in any way
        oldValue = getOldValue();
        newValue = getNewValue();
        break;
      default:
        throw new SQLException("Unexpected value for action_type: " + actionType);
    }
    return getTicketActionType().generateSummary(table.getConnector(), oldValue, newValue);
  }

  public synchronized String getDetails() throws IOException, SQLException {
    if (!detailsLoaded) {
      // Only perform the query for action types that have details
      if (actionType.equals(ActionType.ADD_ANNOTATION)) {
        details = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_ACTION_DETAILS, pkey);
      } else {
        details = null;
      }
      detailsLoaded = true;
    }
    return details;
  }

  public synchronized String getRawEmail() throws IOException, SQLException {
    if (!rawEmailLoaded) {
      // Only perform the query for action types that may have raw email
      if (actionType.equals(ActionType.ADD_ANNOTATION)) {
        rawEmail = table.getConnector().requestNullLongStringQuery(true, AoservProtocol.CommandId.GET_TICKET_ACTION_RAW_EMAIL, pkey);
      } else {
        rawEmail = null;
      }
      rawEmailLoaded = true;
    }
    return rawEmail;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKET_ACTIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      ticket = result.getInt(pos++);
      administrator = User.Name.valueOf(result.getString(pos++));
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      actionType = result.getString(pos++);
      oldAccounting = Account.Name.valueOf(result.getString(pos++));
      newAccounting = Account.Name.valueOf(result.getString(pos++));
      oldPriority = result.getString(pos++);
      newPriority = result.getString(pos++);
      oldType = result.getString(pos++);
      newType = result.getString(pos++);
      oldStatus = result.getString(pos++);
      newStatus = result.getString(pos++);
      oldAssignedTo = User.Name.valueOf(result.getString(pos++));
      newAssignedTo = User.Name.valueOf(result.getString(pos++));
      oldCategory = result.getInt(pos++);
      if (result.wasNull()) {
        oldCategory = -1;
      }
      newCategory = result.getInt(pos++);
      if (result.wasNull()) {
        newCategory = -1;
      }
      // Loaded only when needed: old_value = result.getString(pos++);
      // Loaded only when needed: new_value = result.getString(pos++);
      fromAddress = Email.valueOf(result.getString(pos++));
      summary = result.getString(pos++);
      // Loaded only when needed: details = result.getString(pos++);
      // Loaded only when needed: raw_email = result.getString(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      ticket = in.readCompressedInt();
      administrator = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      actionType = in.readUTF().intern();
      oldAccounting = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
      newAccounting = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
      oldPriority = InternUtils.intern(in.readNullUTF());
      newPriority = InternUtils.intern(in.readNullUTF());
      oldType = InternUtils.intern(in.readNullUTF());
      newType = InternUtils.intern(in.readNullUTF());
      oldStatus = InternUtils.intern(in.readNullUTF());
      newStatus = InternUtils.intern(in.readNullUTF());
      oldAssignedTo = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      newAssignedTo = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      oldCategory = in.readCompressedInt();
      newCategory = in.readCompressedInt();
      // Loaded only when needed: old_value
      // Loaded only when needed: new_value
      fromAddress = Email.valueOf(in.readNullUTF());
      summary = in.readNullUTF();
      // Loaded only when needed: details
      // Loaded only when needed: raw_email
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return ticket + "|" + pkey + '|' + actionType + '|' + administrator;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(ticket);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_50) >= 0) {
      out.writeNullUTF(Objects.toString(administrator, null));
    } else {
      out.writeUTF(administrator == null ? "aoadmin" : administrator.toString());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(time.getTime());
    } else {
      SQLStreamables.writeTimestamp(time, out);
    }
    out.writeUTF(actionType);
    out.writeNullUTF(Objects.toString(oldAccounting, null));
    out.writeNullUTF(Objects.toString(newAccounting, null));
    out.writeNullUTF(oldPriority);
    out.writeNullUTF(newPriority);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_49) >= 0) {
      out.writeNullUTF(oldType);
      out.writeNullUTF(newType);
    }
    out.writeNullUTF(oldStatus);
    out.writeNullUTF(newStatus);
    out.writeNullUTF(Objects.toString(oldAssignedTo, null));
    out.writeNullUTF(Objects.toString(newAssignedTo, null));
    out.writeCompressedInt(oldCategory);
    out.writeCompressedInt(newCategory);
    // Loaded only when needed: old_value
    // Loaded only when needed: new_value
    out.writeNullUTF(Objects.toString(fromAddress, null));
    out.writeNullUTF(summary);
    // Loaded only when needed: details
    // Loaded only when needed: raw_email
  }
}
