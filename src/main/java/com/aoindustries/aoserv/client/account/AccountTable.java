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

package com.aoindustries.aoserv.client.account;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.hodgepodge.tree.Node;
import com.aoapps.hodgepodge.tree.Tree;
import com.aoapps.hodgepodge.tree.TreeCopy;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see  Account
 *
 * @author  AO Industries, Inc.
 */
public final class AccountTable extends CachedTableAccountNameKey<Account> {

  private Account.Name rootAccounting;

  AccountTable(AoservConnector connector) {
    super(connector, Account.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Account.COLUMN_ACCOUNTING_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public void addAccount(
      final Account.Name accounting,
      final String contractNumber,
      final Host defaultServer,
      final Account.Name parent,
      final boolean canAddBackupServers,
      final boolean canAddAccounts,
      final boolean canSeePrices,
      final boolean billParent
  ) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.BUSINESSES.ordinal());
            out.writeUTF(accounting.toString());
            out.writeBoolean(contractNumber != null);
            if (contractNumber != null) {
              out.writeUTF(contractNumber);
            }
            out.writeCompressedInt(defaultServer.getPkey());
            out.writeUTF(parent.toString());
            out.writeBoolean(canAddBackupServers);
            out.writeBoolean(canAddAccounts);
            out.writeBoolean(canSeePrices);
            out.writeBoolean(billParent);
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

  @Override
  public void clearCache() {
    super.clearCache();
    synchronized (this) {
      rootAccounting = null;
    }
  }

  public Account.Name generateAccountingCode(Account.Name template) throws IOException, SQLException {
    try {
      return Account.Name.valueOf(
          connector.requestStringQuery(
              true,
              AoservProtocol.CommandId.GENERATE_ACCOUNTING_CODE,
              template.toString()
          )
      );
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  /**
   * Gets one {@link Account} from the database.
   */
  @Override
  public Account get(Account.Name accounting) throws IOException, SQLException {
    return getUniqueRow(Account.COLUMN_ACCOUNTING, accounting);
  }

  List<Account> getChildAccounts(Account business) throws IOException, SQLException {
    Account.Name accounting = business.getName();

    List<Account> cached = getRows();
    List<Account> matches = new ArrayList<>();
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      Account bu = cached.get(c);
      if (accounting.equals(bu.getParent_name())) {
        matches.add(bu);
      }
    }
    return matches;
  }

  public synchronized Account.Name getRootAccount_name() throws IOException, SQLException {
    if (rootAccounting == null) {
      try {
        rootAccounting = Account.Name.valueOf(connector.requestStringQuery(true, AoservProtocol.CommandId.GET_ROOT_BUSINESS));
      } catch (ValidationException e) {
        throw new IOException(e);
      }
    }
    return rootAccounting;
  }

  public Account getRootAccount() throws IOException, SQLException {
    Account.Name accounting = getRootAccount_name();
    Account bu = get(accounting);
    if (bu == null) {
      throw new SQLException("Unable to find Account: " + accounting);
    }
    return bu;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BUSINESSES;
  }

  /**
   * Gets the list of all accounts that either have a null parent (the
   * actual root of the account tree) or where the parent is inaccessible.
   */
  public List<Account> getTopLevelAccounts() throws IOException, SQLException {
    List<Account> cached = getRows();
    List<Account> matches = new ArrayList<>();
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      Account bu = cached.get(c);
      if (bu.getParent_name() == null || getUniqueRow(Account.COLUMN_ACCOUNTING, bu.getParent_name()) == null) {
        matches.add(bu);
      }
    }
    return matches;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_BUSINESS)) {
      if (Aosh.checkParamCount(Command.ADD_BUSINESS, args, 8, err)) {
        try {
          connector.getSimpleClient().addAccount(
              Aosh.parseAccountingCode(args[1], "accounting_code"),
              args[2],
              args[3],
              Aosh.parseAccountingCode(args[4], "parent_business"),
              Aosh.parseBoolean(args[5], "can_add_backup_servers"),
              Aosh.parseBoolean(args[6], "can_add_businesses"),
              Aosh.parseBoolean(args[7], "can_see_prices"),
              Aosh.parseBoolean(args[8], "bill_parent")
          );
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.ADD_BUSINESS + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CANCEL_BUSINESS)) {
      if (Aosh.checkParamCount(Command.CANCEL_BUSINESS, args, 2, err)) {
        connector.getSimpleClient().cancelAccount(
            Aosh.parseAccountingCode(args[1], "accounting_code"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_ACCOUNTING)) {
      if (Aosh.checkParamCount(Command.CHECK_ACCOUNTING, args, 1, err)) {
        ValidationResult validationResult = Account.Name.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_ACCOUNTING + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_BUSINESS)) {
      if (Aosh.checkParamCount(Command.DISABLE_BUSINESS, args, 2, err)) {
        out.println(
            connector.getSimpleClient().disableAccount(
                Aosh.parseAccountingCode(args[1], "accounting"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_BUSINESS)) {
      if (Aosh.checkParamCount(Command.ENABLE_BUSINESS, args, 1, err)) {
        connector.getSimpleClient().enableAccount(
            Aosh.parseAccountingCode(args[1], "accounting")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_ACCOUNTING)) {
      if (Aosh.checkParamCount(Command.GENERATE_ACCOUNTING, args, 1, err)) {
        out.println(
            connector.getSimpleClient().generateAccountingCode(
                Aosh.parseAccountingCode(args[1], "template")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GET_ROOT_BUSINESS)) {
      if (Aosh.checkParamCount(Command.GET_ROOT_BUSINESS, args, 0, err)) {
        out.println(connector.getSimpleClient().getRootAccount());
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_ACCOUNTING_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_ACCOUNTING_AVAILABLE, args, 1, err)) {
        try {
          out.println(
              connector.getSimpleClient().isAccountingAvailable(
                  Aosh.parseAccountingCode(args[1], "accounting_code")
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_ACCOUNTING_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.MOVE_BUSINESS)) {
      if (Aosh.checkParamCount(Command.MOVE_BUSINESS, args, 3, err)) {
        connector.getSimpleClient().moveAccount(
            Aosh.parseAccountingCode(args[1], "business"),
            args[2],
            args[3],
            isInteractive ? out : null
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_BUSINESS_ACCOUNTING)) {
      if (Aosh.checkParamCount(Command.SET_BUSINESS_ACCOUNTING, args, 2, err)) {
        connector.getSimpleClient().setAccountAccounting(
            Aosh.parseAccountingCode(args[1], "old_accounting"),
            Aosh.parseAccountingCode(args[2], "new_accounting")
        );
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean isAccountingAvailable(Account.Name accounting) throws SQLException, IOException {
    return connector.requestBooleanQuery(
        true,
        AoservProtocol.CommandId.IS_ACCOUNTING_AVAILABLE,
        accounting.toString()
    );
  }

  // <editor-fold defaultstate="collapsed" desc="Tree compatibility">
  private final Tree<Account> tree = () -> {
    List<Account> topLevelAccounts = getTopLevelAccounts();
    int size = topLevelAccounts.size();
    switch (size) {
      case 0:
        return Collections.emptyList();
      case 1:
        Node<Account> singleNode = new AccountTreeNode(topLevelAccounts.get(0));
        return Collections.singletonList(singleNode);
      default:
        List<Node<Account>> rootNodes = new ArrayList<>(size);
        for (Account topLevelAccount : topLevelAccounts) {
          rootNodes.add(new AccountTreeNode(topLevelAccount));
        }
        return Collections.unmodifiableList(rootNodes);
    }
  };

  static class AccountTreeNode implements Node<Account> {

    private final Account business;

    AccountTreeNode(Account business) {
      this.business = business;
    }

    @Override
    public List<Node<Account>> getChildren() throws IOException, SQLException {
      // Look for any existing children
      List<Account> children = business.getChildAccounts();
      int size = children.size();
      switch (size) {
        case 0:
          if (business.canAddAccounts()) {
            // Can have children but empty
            return Collections.emptyList();
          } else {
            // Not allowed to have children
            return null;
          }
        case 1:
          Node<Account> singleNode = new AccountTreeNode(children.get(0));
          return Collections.singletonList(singleNode);
        default:
          List<Node<Account>> childNodes = new ArrayList<>(size);
          for (Account child : children) {
            childNodes.add(new AccountTreeNode(child));
          }
          return Collections.unmodifiableList(childNodes);
      }
    }

    @Override
    public Account getValue() {
      return business;
    }
  }

  /**
   * Gets a Tree view of all the accessible businesses.
   * All access to the tree read-through to the underlying storage
   * and are thus subject to change at any time.  If you need a consistent
   * snapshot of the tree, consider using TreeCopy.
   *
   * @see  TreeCopy
   */
  public Tree<Account> getTree() {
    return tree;
  }
  // </editor-fold>
}
