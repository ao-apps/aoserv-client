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

package com.aoindustries.aoserv.client.dns;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainLabel;
import com.aoapps.net.DomainName;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableStringKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Zone
 *
 * @author  AO Industries, Inc.
 */
public final class ZoneTable extends CachedTableStringKey<Zone> {

  ZoneTable(AoservConnector connector) {
    super(connector, Zone.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Zone.COLUMN_ZONE_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Zone get(String zone) throws IOException, SQLException {
    return getUniqueRow(Zone.COLUMN_ZONE, zone);
  }

  private List<DomainName> getTopLevelDomains() throws IOException, SQLException {
    List<TopLevelDomain> tlds = connector.getDns().getTopLevelDomain().getRows();
    List<DomainName> names = new ArrayList<>(tlds.size());
    for (TopLevelDomain tld : tlds) {
      names.add(tld.getDomain());
    }
    return names;
  }

  public void addDnsZone(Package packageObj, String zone, InetAddress ip, int ttl) throws IOException, SQLException {
    connector.requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.DNS_ZONES,
        packageObj.getName(),
        zone,
        ip,
        ttl
    );
  }

  /**
   * Checks the formatting for a DNS zone.  The format of a DNS zone must be <code><i>name</i>.<i>tld</i>.</code>
   */
  public boolean checkDnsZone(String zone) throws IOException, SQLException {
    return checkDnsZone(zone, getTopLevelDomains());
  }

  /**
   * Checks the formatting for a DNS zone.  The format of a DNS zone must be <code><i>name</i>.<i>tld</i>.</code>
   */
  public static boolean checkDnsZone(String zone, List<DomainName> tlds) {
    int zoneLen = zone.length();

    String shortestName = null;
    int len = tlds.size();
    for (int c = 0; c < len; c++) {
      DomainName o = tlds.get(c);
      String tld = '.' + o.toString() + '.';

      int tldLen = tld.length();
      if (tldLen < zoneLen) {
        if (zone.substring(zoneLen - tldLen).equals(tld)) {
          String name = zone.substring(0, zoneLen - tldLen);
          if (shortestName == null || name.length() < shortestName.length()) {
            shortestName = name;
          }
        }
      }
    }
    if (shortestName != null) {
      return isValidHostnamePart(shortestName);
    }
    return false;
  }

  public String getZoneForHostname(String hostname) throws IllegalArgumentException, IOException, SQLException {
    return getZoneForHostname(hostname, getTopLevelDomains());
  }

  /**
   * Gets the zone represented by this <code>DNSZone</code>.
   *
   * @return  the zone in the format <code><i>name</i>.<i>tld</i>.</code>
   */
  public static String getZoneForHostname(String hostname, List<DomainName> tlds) throws IllegalArgumentException {
    int hlen = hostname.length();
    if (hlen > 0 && hostname.charAt(hlen - 1) == '.') {
      hostname = hostname.substring(0, --hlen);
    }
    String longestTld = null;
    int tldlen = tlds.size();
    for (int i = 0; i < tldlen; i++) {
      DomainName o = tlds.get(i);
      String tld = '.' + o.toString(); // No dot at end

      int len = tld.length();
      if (hlen >= len && hostname.substring(hlen - len).equals(tld)) {
        if (longestTld == null || tld.length() > longestTld.length()) {
          longestTld = tld;
        }
      }
    }
    if (longestTld == null) {
      throw new IllegalArgumentException("Unable to determine top level domain for hostname: " + hostname);
    }

    String zone = hostname.substring(0, hlen - longestTld.length());
    int startpos = zone.lastIndexOf('.');
    if (startpos >= 0) {
      zone = zone.substring(startpos + 1);
    }
    return zone + longestTld + ".";
  }

  public List<Zone> getDnsZones(Package packageObj) throws IOException, SQLException {
    return getIndexedRows(Zone.COLUMN_PACKAGE, packageObj.getName());
  }

  /**
   * Gets the hostname for a fully qualified hostname.  Gets a hostname in <code><i>name</i>.<i>tld</i></code> format.
   *
   * @exception  IllegalArgumentException  if hostname cannot be resolved to a top level domain
   */
  public static DomainName getHostTld(DomainName hostname, List<DomainName> tlds) throws IllegalArgumentException {
    String hostnameStr = hostname.toLowerCase();
    int hostnameLen = hostnameStr.length();
    for (DomainName o : tlds) {
      String tld = '.' + o.toLowerCase();

      int tldLen = tld.length();
      if (
          hostnameLen > tldLen
              && hostnameStr.endsWith(tld)
      ) {
        String name = hostnameStr.substring(0, hostnameLen - tldLen);
        // Take only the last hostname segment
        int pos = name.lastIndexOf('.');
        if (pos != -1) {
          name = name.substring(pos + 1);
        }
        try {
          return DomainName.valueOf(name + tld);
        } catch (ValidationException e) {
          throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
      }
    }
    throw new IllegalArgumentException("Unable to determine the host.tld format of " + hostname);
  }

  public DomainName getHostTld(DomainName hostname) throws IllegalArgumentException, IOException, SQLException {
    return getHostTld(hostname, getTopLevelDomains());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_ZONES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_DNS_ZONE)) {
      if (Aosh.checkParamCount(Command.ADD_DNS_ZONE, args, 4, err)) {
        connector.getSimpleClient().addDnsZone(
            Aosh.parseAccountingCode(args[1], "package"),
            args[2],
            Aosh.parseInetAddress(args[3], "ip_address"),
            Aosh.parseInt(args[4], "ttl")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_DNS_ZONE)) {
      if (Aosh.checkParamCount(Command.CHECK_DNS_ZONE, args, 1, err)) {
        try {
          connector.getSimpleClient().checkDnsZone(
              args[1]
          );
          out.println("true");
        } catch (IllegalArgumentException iae) {
          out.print("aosh: " + Command.CHECK_DNS_ZONE + ": ");
          out.println(iae.getMessage());
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_DNS_ZONE_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_DNS_ZONE_AVAILABLE, args, 1, err)) {
        try {
          out.println(connector.getSimpleClient().isDnsZoneAvailable(args[1]));
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_DNS_ZONE_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.PRINT_ZONE_FILE)) {
      if (Aosh.checkParamCount(Command.PRINT_ZONE_FILE, args, 1, err)) {
        connector.getSimpleClient().printZoneFile(
            args[1],
            out
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_DNS_ZONE)) {
      if (Aosh.checkParamCount(Command.REMOVE_DNS_ZONE, args, 1, err)) {
        connector.getSimpleClient().removeDnsZone(
            args[1]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_DNS_ZONE_TTL)) {
      if (Aosh.checkParamCount(Command.REMOVE_DNS_ZONE, args, 2, err)) {
        connector.getSimpleClient().setDnsZoneTtl(
            args[1],
            Aosh.parseInt(args[2], "ttl")
        );
      }
    }
    return false;
  }

  public boolean isDnsZoneAvailable(String zone) throws IOException, SQLException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandId.IS_DNS_ZONE_AVAILABLE, zone);
  }

  /**
   * TODO: Is this redundant with {@link DomainLabel}?.
   */
  public static boolean isValidHostnamePart(String name) {
    // Must not be an empty string
    int len = name.length();
    if (len == 0) {
      return false;
    }

    // The first character must not be -
    if (name.charAt(0) == '-') {
      return false;
    }

    // Must not be all numbers
    int numCount = 0;
    // All remaining characters must be [a-z,0-9,-]
    for (int c = 0; c < len; c++) {
      char ch = name.charAt(c);
      if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '-') {
        return false;
      }
      if (ch >= '0' && ch <= '9') {
        numCount++;
      }
    }
    return numCount != len;
  }
}
