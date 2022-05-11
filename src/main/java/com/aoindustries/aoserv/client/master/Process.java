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

package com.aoindustries.aoserv.client.master;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.InetAddress;
import com.aoapps.security.Identifier;
import com.aoapps.security.SecurityStreamables;
import com.aoapps.security.SmallIdentifier;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Each <code>Thread</code> on the master reports its activities so that
 * a query on this table shows a snapshot of the currently running system.
 *
 * @author  AO Industries, Inc.
 */
public class Process extends AoservObject<SmallIdentifier, Process> implements SingleTableObject<SmallIdentifier, Process> {

  static final int COLUMN_ID = 0;

  /**
   * The different states a process may be in.
   */
  public static final String
      LOGIN = "login",
      RUN = "run",
      SLEEP = "sleep";

  protected SmallIdentifier id;
  protected Identifier connectorId;
  protected User.Name authenticatedUser;
  protected User.Name effectiveUser;
  protected int daemonServer;
  protected InetAddress host;
  protected String protocol;
  protected String aoservProtocol;
  protected boolean isSecure;
  protected UnmodifiableTimestamp connectTime;
  protected long useCount;
  protected long totalTime;
  protected int priority;
  protected String state;
  private String[] command;
  // TODO: Add tracking of which daemon(s) is(are) currently connected to by the master process.
  //       This would greatly aid in seeing when a daemon is stalling the system.
  protected UnmodifiableTimestamp stateStartTime;

  private AoservTable<SmallIdentifier, Process> table;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  // Also used by aoserv-master subclass
  public Process() {
    // Do nothing
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return id;
      case 1:
        return connectorId;
      case 2:
        return authenticatedUser;
      case 3:
        return effectiveUser;
      case 4:
        return daemonServer == -1 ? null : daemonServer;
      case 5:
        return host;
      case 6:
        return protocol;
      case 7:
        return aoservProtocol;
      case 8:
        return isSecure;
      case 9:
        return connectTime;
      case 10:
        return useCount;
      case 11:
        return totalTime;
      case 12:
        return priority;
      case 13:
        return state;
      case 14:
        return combineCommand(getCommand()); // TODO: Support arrays
      case 15:
        return stateStartTime;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getDaemonServer() {
    return daemonServer;
  }

  public int getPriority() {
    return priority;
  }

  public SmallIdentifier getId() {
    return id;
  }

  public Identifier getConnectorId() {
    return connectorId;
  }

  public User.Name getAuthenticatedAdministrator_username() {
    return authenticatedUser;
  }

  public Administrator getAuthenticatedAdministrator() throws IOException, SQLException {
    // Null OK when filtered
    return table.getConnector().getAccount().getAdministrator().get(authenticatedUser);
  }

  public User.Name getEffectiveAdministrator_username() {
    return effectiveUser;
  }

  public Administrator getEffectiveAdministrator() throws SQLException, IOException {
    Administrator obj = table.getConnector().getAccount().getAdministrator().get(effectiveUser);
    if (obj == null) {
      throw new SQLException("Unable to find Administrator: " + effectiveUser);
    }
    return obj;
  }

  public InetAddress getHost() {
    return host;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getAoservProtocol() {
    return aoservProtocol;
  }

  public boolean isSecure() {
    return isSecure;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getConnectTime() {
    return connectTime;
  }

  public long getUseCount() {
    return useCount;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public String getState() {
    return state;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getStateStartTime() {
    return stateStartTime;
  }

  @Override
  public SmallIdentifier getKey() {
    return id;
  }

  @Override
  public AoservTable<SmallIdentifier, Process> getTable() {
    return table;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_PROCESSES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    throw new SQLException("Should not be read from the database, should be generated.");
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      id = SecurityStreamables.readSmallIdentifier(in);
      connectorId = SecurityStreamables.readNullIdentifier(in);
      authenticatedUser = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      effectiveUser = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      daemonServer = in.readCompressedInt();
      host = InetAddress.valueOf(in.readUTF()).intern();
      protocol = in.readUTF().intern();
      aoservProtocol = InternUtils.intern(in.readNullUTF());
      isSecure = in.readBoolean();
      connectTime = SQLStreamables.readUnmodifiableTimestamp(in);
      useCount = in.readLong();
      totalTime = in.readLong();
      priority = in.readCompressedInt();
      state = in.readUTF().intern();
      int len = in.readCompressedInt();
      if (len == -1) {
        command = null;
      } else {
        command = new String[len];
        for (int i = 0; i < len; i++) {
          command[i] = in.readNullUTF();
        }
      }
      stateStartTime = SQLStreamables.readUnmodifiableTimestamp(in);
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public static String combineCommand(String[] command) {
    if (command == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int c = 0, len = command.length; c < len; c++) {
      if (c > 0) {
        sb.append(' ');
      }
      String com = command[c];
      if (com == null) {
        sb.append("''");
      } else {
        sb.append(com);
      }
    }
    return sb.toString();
  }

  public String[] getCommand() {
    return command == null ? null : Arrays.copyOf(command, command.length);
  }

  @Override
  public void setTable(AoservTable<SmallIdentifier, Process> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table = table;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    // Get values under lock before writing.  This is done here because
    // these objects are used on the master to track process states, and the objects can change at any time
    SmallIdentifier myId;
    Identifier myConnectorId;
    User.Name myAuthenticatedUser;
    User.Name myEffectiveUser;
    int myDaemonServer;
    InetAddress myHost;
    String myProtocol;
    String myAoservProtocol;
    boolean myIsSecure;
    UnmodifiableTimestamp myConnectTime;
    long myUseCount;
    long myTotalTime;
    int myPriority;
    String myState;
    String[] myCommand;
    UnmodifiableTimestamp myStateStartTime;
    synchronized (this) {
      myId = id;
      myConnectorId = connectorId;
      myAuthenticatedUser = authenticatedUser;
      myEffectiveUser = effectiveUser;
      myDaemonServer = daemonServer;
      myHost = host;
      myProtocol = protocol;
      myAoservProtocol = aoservProtocol;
      myIsSecure = isSecure;
      myConnectTime = connectTime;
      myUseCount = useCount;
      myTotalTime = totalTime;
      myPriority = priority;
      myState = state;
      myCommand = getCommand(); // Using method since subclass overrides this to generate command from master server-side objects
      myStateStartTime = stateStartTime;
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(myId.getValue());
      // Old clients had a 64-bit ID, just send them the low-order bits
      out.writeLong(myConnectorId == null ? -1 : myConnectorId.getLo());
    } else {
      SecurityStreamables.writeSmallIdentifier(myId, out);
      SecurityStreamables.writeNullIdentifier(myConnectorId, out);
    }
    out.writeNullUTF(Objects.toString(myAuthenticatedUser, null));
    out.writeNullUTF(Objects.toString(myEffectiveUser, null));
    out.writeCompressedInt(myDaemonServer);
    out.writeUTF(myHost.toString());
    out.writeUTF(myProtocol);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) {
      out.writeNullUTF(myAoservProtocol);
    }
    out.writeBoolean(myIsSecure);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(myConnectTime.getTime());
    } else {
      SQLStreamables.writeTimestamp(myConnectTime, out);
    }
    out.writeLong(myUseCount);
    out.writeLong(myTotalTime);
    out.writeCompressedInt(myPriority);
    out.writeUTF(myState);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeNullUTF(combineCommand(myCommand));
      out.writeLong(myStateStartTime.getTime());
    } else {
      if (myCommand == null) {
        out.writeCompressedInt(-1);
      } else {
        int len = myCommand.length;
        out.writeCompressedInt(len);
        for (int i = 0; i < len; i++) {
          out.writeNullUTF(myCommand[i]);
        }
      }
      SQLStreamables.writeTimestamp(myStateStartTime, out);
    }
  }
}
