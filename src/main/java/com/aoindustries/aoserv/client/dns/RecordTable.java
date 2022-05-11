/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.dns;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Record
 *
 * @author  AO Industries, Inc.
 */
public final class RecordTable extends CachedTableIntegerKey<Record> {

  RecordTable(AoservConnector connector) {
    super(connector, Record.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Record.COLUMN_ZONE_name, ASCENDING),
      new OrderBy(Record.COLUMN_DOMAIN_name, ASCENDING),
      new OrderBy(Record.COLUMN_TYPE_name, ASCENDING),
      new OrderBy(Record.COLUMN_PRIORITY_name, ASCENDING),
      new OrderBy(Record.COLUMN_WEIGHT_name, ASCENDING),
      new OrderBy(Record.COLUMN_TAG_name, ASCENDING),
      new OrderBy(Record.COLUMN_DESTINATION_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addDnsRecord(
      Zone zone,
      String domain,
      RecordType type,
      int priority,
      int weight,
      int port,
      short flag,
      String tag,
      String destination,
      int ttl
  ) throws IOException, SQLException {
    if (!Record.isValidFlag(flag)) {
      throw new IllegalArgumentException("Invalid flag: " + flag);
    }
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.DNS_RECORDS,
        zone.getZone(),
        domain,
        type.getType(),
        priority,
        weight,
        port,
        flag,
        (tag == null) ? "" : tag,
        destination,
        ttl
    );
  }

  @Override
  public Record get(int id) throws IOException, SQLException {
    return getUniqueRow(Record.COLUMN_ID, id);
  }

  List<Record> getRecords(Zone dnsZone) throws IOException, SQLException {
    return getIndexedRows(Record.COLUMN_ZONE, dnsZone.getZone());
  }

  List<Record> getRecords(Zone dnsZone, String domain, RecordType dnsType) throws IOException, SQLException {
    String type = dnsType.getType();

    // Use the index first
    List<Record> cached = getRecords(dnsZone);
    int size = cached.size();
    List<Record> matches = new ArrayList<>(size);
    for (int c = 0; c < size; c++) {
      Record rec = cached.get(c);
      if (
          rec.getType_type().equals(type)
              && rec.getDomain().equals(domain)
      ) {
        matches.add(rec);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_RECORDS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_DNS_RECORD)) {
      if (Aosh.checkMinParamCount(Command.ADD_DNS_RECORD, args, 3, err)) {
        String zone   = args[1];
        String domain = args[2];
        String type   = args[3];
        int priority;
        int weight;
        int port;
        short flag;
        String tag;
        String destination;
        int ttl;
        if (
            (
                RecordType.A.equals(type)
                    || RecordType.AAAA.equals(type)
                    || RecordType.CNAME.equals(type)
                    || RecordType.NS.equals(type)
                    || RecordType.PTR.equals(type)
                    || RecordType.TXT.equals(type)
            )
                && (args.length == 5 || args.length == 6)
        ) {
          priority    = Record.NO_PRIORITY;
          weight      = Record.NO_WEIGHT;
          port        = Record.NO_PORT;
          flag        = Record.NO_FLAG;
          tag         = null;
          destination = args[4];
          ttl         = args.length <= 5 || args[5].isEmpty() ? Record.NO_TTL : Aosh.parseInt(args[5], "ttl");
        } else if (
            RecordType.CAA.equals(type)
                && (args.length == 7 || args.length == 8)
        ) {
          priority = Record.NO_PRIORITY;
          weight = Record.NO_WEIGHT;
          port = Record.NO_PORT;
          flag        = Aosh.parseShort(args[4], "flag");
          tag         = args[5];
          destination = args[6];
          ttl         = args.length <= 7 || args[7].isEmpty() ? Record.NO_TTL : Aosh.parseInt(args[7], "ttl");
        } else if (
            RecordType.MX.equals(type)
                && (args.length == 6 || args.length == 7)
        ) {
          priority    = Aosh.parseInt(args[4], "priority");
          weight      = Record.NO_WEIGHT;
          port        = Record.NO_PORT;
          flag        = Record.NO_FLAG;
          tag         = null;
          destination = args[5];
          ttl         = args.length <= 6 || args[6].isEmpty() ? Record.NO_TTL : Aosh.parseInt(args[6], "ttl");
        } else if (
            RecordType.SRV.equals(type)
                && (args.length == 8 || args.length == 9)
        ) {
          priority    = Aosh.parseInt(args[4], "priority");
          weight      = Aosh.parseInt(args[5], "weight");
          port        = Aosh.parseInt(args[6], "port");
          flag        = Record.NO_FLAG;
          tag         = null;
          destination = args[7];
          ttl         = args.length <= 8 || args[8].isEmpty() ? Record.NO_TTL : Aosh.parseInt(args[8], "ttl");
        } else if (args.length == 10 || Aosh.checkParamCount(Command.ADD_DNS_RECORD, args, 10, err)) {
          // Arbitrary type, all fields
          priority    = args[4].isEmpty() ? Record.NO_PRIORITY : Aosh.parseInt(args[4], "priority");
          weight      = args[5].isEmpty() ? Record.NO_WEIGHT   : Aosh.parseInt(args[5], "weight");
          port        = args[6].isEmpty() ? Record.NO_PORT     : Aosh.parseInt(args[6], "port");
          flag        = args[7].isEmpty() ? Record.NO_FLAG     : Aosh.parseShort(args[7], "flag");
          tag         = args[8].isEmpty() ? null               : args[8];
          destination = args[9];
          ttl         = args.length <= 10 || args[10].isEmpty() ? Record.NO_TTL : Aosh.parseInt(args[10], "ttl");
        } else {
          return true;
        }
        out.println(
            connector.getSimpleClient().addDnsRecord(
                zone,
                domain,
                type,
                priority,
                weight,
                port,
                flag,
                tag,
                destination,
                ttl
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_DNS_RECORD)) {
      if (args.length == 2) {
        connector.getSimpleClient().removeDnsRecord(
            Aosh.parseInt(args[1], "pkey")
        );
        return true;
      } else if (args.length == 6) {
        connector.getSimpleClient().removeDnsRecord(
            args[1],
            args[2],
            args[3],
            args[4].isEmpty() ? null : args[4],
            args[5]
        );
        return true;
      } else {
        err.print("aosh: ");
        err.print(Command.REMOVE_DNS_RECORD);
        err.println(": must be either 1 or 5 parameters");
        err.flush();
        return false;
      }
    }
    return false;
  }
}
