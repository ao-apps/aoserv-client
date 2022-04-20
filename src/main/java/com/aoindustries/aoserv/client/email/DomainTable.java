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

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Domain
 *
 * @author  AO Industries, Inc.
 */
public final class DomainTable extends CachedTableIntegerKey<Domain> {

  DomainTable(AOServConnector connector) {
    super(connector, Domain.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(Domain.COLUMN_DOMAIN_name, ASCENDING),
    new OrderBy(Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addEmailDomain(DomainName domain, Server ao, Package packageObject) throws SQLException, IOException {
    return connector.requestIntQueryIL(
      true,
      AoservProtocol.CommandID.ADD,
      Table.TableID.EMAIL_DOMAINS,
      domain,
      ao.getPkey(),
      packageObject.getName()
    );
  }

  @Override
  public Domain get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Domain.COLUMN_PKEY, pkey);
  }

  public List<Domain> getEmailDomains(Account owner) throws SQLException, IOException {
    Account.Name accounting=owner.getName();

    List<Domain> cached = getRows();
    int len = cached.size();
    List<Domain> matches=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      Domain domain = cached.get(c);
      if (domain.getPackage().getAccount_name().equals(accounting)) {
        matches.add(domain);
      }
    }
    return matches;
  }

  public List<Domain> getEmailDomains(Package pack) throws IOException, SQLException {
    return getIndexedRows(Domain.COLUMN_PACKAGE, pack.getName());
  }

  public List<Domain> getEmailDomains(Server ao) throws IOException, SQLException {
    return getIndexedRows(Domain.COLUMN_AO_SERVER, ao.getServer_pkey());
  }

  public Domain getEmailDomain(Server ao, DomainName domain) throws IOException, SQLException {
    // Use the index first
    List<Domain> cached = getEmailDomains(ao);
    int len = cached.size();
    for (int c = 0; c < len; c++) {
      Domain sd = cached.get(c);
      if (domain.equals(sd.getDomain())) {
        return sd;
      }
    }
    return null;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.EMAIL_DOMAINS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command=args[0];
    if (command.equalsIgnoreCase(Command.ADD_EMAIL_DOMAIN)) {
      if (AOSH.checkParamCount(Command.ADD_EMAIL_DOMAIN, args, 3, err)) {
        out.println(
          connector.getSimpleAOClient().addEmailDomain(
            AOSH.parseDomainName(args[1], "domain"),
            args[2],
            AOSH.parseAccountingCode(args[3], "package")
          )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_EMAIL_DOMAIN)) {
      if (AOSH.checkParamCount(Command.CHECK_EMAIL_DOMAIN, args, 1, err)) {
        ValidationResult validationResult = DomainName.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: "+Command.CHECK_EMAIL_DOMAIN+": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_EMAIL_DOMAIN_AVAILABLE)) {
      if (AOSH.checkParamCount(Command.IS_EMAIL_DOMAIN_AVAILABLE, args, 2, err)) {
        try {
          out.println(
            connector.getSimpleAOClient().isEmailDomainAvailable(
              AOSH.parseDomainName(args[1], "domain"),
              args[2]
            )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: "+Command.IS_EMAIL_DOMAIN_AVAILABLE+": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_EMAIL_DOMAIN)) {
      if (AOSH.checkParamCount(Command.REMOVE_EMAIL_DOMAIN, args, 2, err)) {
        connector.getSimpleAOClient().removeEmailDomain(
          AOSH.parseDomainName(args[1], "domain"),
          args[2]
        );
      }
      return true;
    }
    return false;
  }

  public boolean isEmailDomainAvailable(Server aoServer, DomainName domain) throws SQLException, IOException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandID.IS_EMAIL_DOMAIN_AVAILABLE, aoServer.getPkey(), domain);
  }
}
