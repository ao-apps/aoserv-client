/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.CachedObjectStringKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Dumpable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A <code>DNSZone</code> is one domain hosted in the name servers.  It can have
 * any number of <code>DNSRecord</code>s.  Please see <code>DNSTLD</code> for
 * domain restrictions.
 *
 * @see  TopLevelDomain
 * @see  Record
 *
 * @author  AO Industries, Inc.
 */
public final class Zone extends CachedObjectStringKey<Zone> implements Removable, Dumpable {

  static final int COLUMN_ZONE = 0;
  static final int COLUMN_PACKAGE = 2;
  static final String COLUMN_ZONE_name = "zone";

  public static final int DEFAULT_TTL = 3600;

  /**
   * The maximum number of characters allowed in a line.
   */
  public static final int MAX_LINE_LENGTH = 255;

  /**
   * The zone that is in charge of the API.
   */
  public static final String API_ZONE = "aoindustries.com.";

  /**
   * The hostmaster that is placed in a newly created <code>DNSZone</code>.
   */
  public static final String DEFAULT_HOSTMASTER = "hostmaster." + API_ZONE;

  /**
   * The default priority for new MX records.
   */
  public static final int DEFAULT_MX_PRIORITY = 10;

  /**
   * The default flag for new CAA records.
   */
  public static final short DEFAULT_CAA_FLAG = 0;

  /**
   * The default tag for new CAA records.
   */
  public static final String DEFAULT_CAA_TAG = Record.CAA_TAG_ISSUE;

  /**
   * The default value for new CAA records.
   */
  public static final String DEFAULT_CAA_VALUE = ";";

  private String file;
  private Account.Name packageName;
  private String hostmaster;
  private long serial;
  private int ttl;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Zone#init(java.sql.ResultSet)
   * @see  Zone#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Zone() {
    // Do nothing
  }

  public int addDnsRecord(
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
    return table.getConnector().getDns().getRecord().addDnsRecord(this, domain, type, priority, weight, port, flag, tag, destination, ttl);
  }

  @Override
  public void dump(PrintWriter out) throws SQLException, IOException {
    printZoneFile(out);
  }

  public RecordType[] getAllowedTypes() throws IOException, SQLException {
    RecordTypeTable tt = table.getConnector().getDns().getRecordType();
    if (isArpa()) {
      RecordType[] types = {
          tt.get(RecordType.NS),
          tt.get(RecordType.PTR)
      };
      return types;
    } else {
      RecordType[] types = {
          tt.get(RecordType.A),
          tt.get(RecordType.AAAA),
          tt.get(RecordType.CAA),
          tt.get(RecordType.CNAME),
          tt.get(RecordType.MX),
          tt.get(RecordType.NS),
          tt.get(RecordType.SRV),
          tt.get(RecordType.TXT)
      };
      return types;
    }
  }

  public static String getArpaZoneForIpAddress(InetAddress ip, String netmask) throws IllegalArgumentException {
    @SuppressWarnings("deprecation")
    com.aoapps.net.AddressFamily addressFamily = ip.getAddressFamily();
    switch (addressFamily) {
      case INET:
        {
          String ipStr = ip.toString();
          if ("255.255.255.0".equals(netmask)) {
            int pos = ipStr.indexOf('.');
            int oct1 = Integer.parseInt(ipStr.substring(0, pos));
            int pos2 = ipStr.indexOf('.', pos + 1);
            int oct2 = Integer.parseInt(ipStr.substring(pos + 1, pos2));
            pos = ipStr.indexOf('.', pos2 + 1);
            int oct3 = Integer.parseInt(ipStr.substring(pos2 + 1, pos));
            return oct3 + "." + oct2 + "." + oct1 + ".in-addr.arpa";
          } else if ("255.255.255.128".equals(netmask)) {
            // Hurricane Electric compatible
            int pos = ipStr.indexOf('.');
            int oct1 = Integer.parseInt(ipStr.substring(0, pos));
            int pos2 = ipStr.indexOf('.', pos + 1);
            int oct2 = Integer.parseInt(ipStr.substring(pos + 1, pos2));
            pos = ipStr.indexOf('.', pos2 + 1);
            int oct3 = Integer.parseInt(ipStr.substring(pos2 + 1, pos));
            int oct4 = Integer.parseInt(ipStr.substring(pos + 1));
            return "subnet" + (oct4 & 128) + "." + oct3 + "." + oct2 + "." + oct1 + ".in-addr.arpa";
          } else {
            throw new IllegalArgumentException("Unsupported netmask: " + netmask);
          }
        }
      case INET6:
        throw new IllegalArgumentException("IPv6 not yet implemented: " + ip);
      default:
        throw new AssertionError("Unexpected address family: " + addressFamily);
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ZONE:
        return pkey;
      case 1:
        return file;
      case COLUMN_PACKAGE:
        return packageName;
      case 3:
        return hostmaster;
      case 4:
        return serial;
      case 5:
        return ttl;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public static long getCurrentSerial() {
    GregorianCalendar gcal = new GregorianCalendar(Type.DATE_TIME_ZONE);
    return
        gcal.get(Calendar.YEAR) * 1000000L
            + (gcal.get(Calendar.MONTH) + 1) * 10000
            + gcal.get(Calendar.DATE) * 100
            + 01;
  }

  public List<Record> getRecords() throws IOException, SQLException {
    return table.getConnector().getDns().getRecord().getRecords(this);
  }

  public List<Record> getRecords(String domain, RecordType type) throws IOException, SQLException {
    return table.getConnector().getDns().getRecord().getRecords(this, domain, type);
  }

  public String getFile() {
    return file;
  }

  public String getHostmaster() {
    return hostmaster;
  }

  public Package getPackage() throws SQLException, IOException {
    Package obj = table.getConnector().getBilling().getPackage().get(packageName);
    if (obj == null) {
      throw new SQLException("Unable to find Package: " + packageName);
    }
    return obj;
  }

  public long getSerial() {
    return serial;
  }

  public int getTtl() {
    return ttl;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_ZONES;
  }

  public String getZone() {
    return pkey;
  }

  public String getZoneFile() throws SQLException, IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(bout);
    printZoneFile(out);
    out.flush();
    return new String(bout.toByteArray());
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getString(1);
      file = result.getString(2);
      packageName = Account.Name.valueOf(result.getString(3));
      hostmaster = result.getString(4);
      serial = result.getLong(5);
      ttl = result.getInt(6);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isArpa() {
    return pkey.length() > 13 && ".in-addr.arpa".equals(pkey.substring(pkey.length() - 13));
  }

  /**
   * Checks that the line is not too long, prints the line, and clears the buffer.
   */
  private static void printLine(StringBuilder line, PrintWriter out) {
    if (line.length() > MAX_LINE_LENGTH) {
      throw new IllegalStateException("Line too long: " + line);
    }
    out.print(line);
    out.print('\n');
    line.setLength(0);
  }

  private static void printRecord(
      String linePrefix,
      StringBuilder line,
      PrintWriter out,
      String domain,
      int ttl,
      int recordTtl,
      String type,
      int priority,
      int weight,
      int port,
      short flag,
      String tag,
      String destination
  ) {
    line.append(linePrefix);
    line.append(domain);
    int count = Math.max(1, 24 - domain.length());
    for (int d = 0; d < count; d++) {
      line.append(' ');
    }
    if (recordTtl != Record.NO_TTL) {
      String s = String.valueOf(recordTtl);
      line.append(s);
      count = Math.max(1, 24 - s.length());
    } else {
      String s = String.valueOf(ttl);
      line.append(s);
      count = Math.max(1, 24 - s.length());
    }
    for (int d = 0; d < count; d++) {
      line.append(' ');
    }
    line.append("IN   ");
    line.append(type);
    count = Math.max(1, 8 - type.length());
    for (int d = 0; d < count; d++) {
      line.append(' ');
    }
    if (priority != Record.NO_PRIORITY) {
      line.append(priority);
      line.append(' ');
    }
    if (weight != Record.NO_WEIGHT) {
      line.append(weight);
      line.append(' ');
    }
    if (port != Record.NO_PORT) {
      line.append(port);
      line.append(' ');
    }
    if (flag != Record.NO_FLAG) {
      line.append(flag);
      line.append(' ');
    }
    if (tag != null) {
      line.append(tag);
      line.append(' ');
    }
    if (type.equals(RecordType.CAA) || type.equals(RecordType.TXT)) {
      // Clean the CAA or TXT type
      String txt = Record.cleanTxt(destination);
      int oneLineLength = line.length() + 1 + txt.length() + 1;
      if (oneLineLength <= MAX_LINE_LENGTH) {
        // Double-quote in one line
        line.append('"').append(txt).append('"');
        printLine(line, out);
      } else {
        // Begin parenthesis
        line.append('(');
        printLine(line, out);
        // Double-quote TXT
        final int charsPerLine = MAX_LINE_LENGTH - (linePrefix.length() + 2);
        for (int lineStart = 0; lineStart < txt.length(); lineStart += charsPerLine) {
          line
              .append(linePrefix)
              .append('"')
              .append(txt, lineStart, Math.min(txt.length(), lineStart + charsPerLine))
              .append('"');
          printLine(line, out);
        }
        // End parenthesis
        line.append(linePrefix).append(')');
        printLine(line, out);
      }
    } else {
      line.append(destination);
      printLine(line, out);
    }
  }

  public void printZoneFile(PrintWriter out) throws SQLException, IOException {
    StringBuilder line = new StringBuilder(); // Buffers each line to ensure not too long
    final List<Record> records = getRecords();
    line.append("$TTL    ").append(ttl);
    printLine(line, out);
    if (!isArpa()) {
      line.append("$ORIGIN ").append(pkey);
      printLine(line, out);
    }
    line.append("@                       ").append(ttl).append(" IN   SOA     ");
    // Find the first nameserver
    Record firstNameserver = null;
    for (Record rec : records) {
      if (rec.getType().getType().equals(RecordType.NS)) {
        firstNameserver = rec;
        break;
      }
    }
    // TODO: First Nameserver should be from brands
    // TODO: Default hostmaster should be from brands, and made nullable where value from brands is taken
    line.append(firstNameserver == null ? "ns1.aoindustries.com." : firstNameserver.getDestination());
    line.append("   ").append(hostmaster).append(" (");
    printLine(line, out);
    line.append("                                ").append(serial).append(" ; serial");
    printLine(line, out);
    line.append("                                3600    ; refresh");
    printLine(line, out);
    line.append("                                600     ; retry");
    printLine(line, out);
    line.append("                                1814400 ; expiry");
    printLine(line, out);
    line.append("                                300     ; minimum");
    printLine(line, out);
    line.append("                                )");
    printLine(line, out);
    if (firstNameserver == null) {
      // Add the default nameservers because named will refuse to start without them
      line.append("; No name servers configured, using the defaults");
      printLine(line, out);
      printRecord("", line, out, "@", ttl, Record.NO_TTL, RecordType.NS, Record.NO_PRIORITY, Record.NO_WEIGHT, Record.NO_PORT, Record.NO_FLAG, null, "ns1.aoindustries.com.");
      printRecord("", line, out, "@", ttl, Record.NO_TTL, RecordType.NS, Record.NO_PRIORITY, Record.NO_WEIGHT, Record.NO_PORT, Record.NO_FLAG, null, "ns2.aoindustries.com.");
      printRecord("", line, out, "@", ttl, Record.NO_TTL, RecordType.NS, Record.NO_PRIORITY, Record.NO_WEIGHT, Record.NO_PORT, Record.NO_FLAG, null, "ns3.aoindustries.com.");
      printRecord("", line, out, "@", ttl, Record.NO_TTL, RecordType.NS, Record.NO_PRIORITY, Record.NO_WEIGHT, Record.NO_PORT, Record.NO_FLAG, null, "ns4.aoindustries.com.");
    }
    int len = records.size();
    for (int c = 0; c < len; c++) {
      Record rec = records.get(c);
      boolean hasConflictAbove = false;
      for (int d = 0; d < c; d++) {
        if (rec.hasConflict(records.get(d))) {
          hasConflictAbove = true;
          break;
        }
      }
      printRecord(
          hasConflictAbove ? "; Disabled due to conflict: " : "",
          line,
          out,
          rec.getDomain(),
          ttl,
          rec.getTtl(),
          rec.getType_type(),
          rec.getPriority(),
          rec.getWeight(),
          rec.getPort(),
          rec.getFlag(),
          rec.getTag(),
          rec.getDestination()
      );
      // Allow the first one when there is a conflict
      if (!hasConflictAbove) {
        boolean hasConflictBelow = false;
        for (int d = c + 1; d < len; d++) {
          if (rec.hasConflict(records.get(d))) {
            hasConflictBelow = true;
            break;
          }
        }
        if (hasConflictBelow) {
          line.append("; Some records below have been disabled due to conflict with previous record");
          printLine(line, out);
        }
      }
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readUTF().intern();
      file = in.readUTF();
      packageName = Account.Name.valueOf(in.readUTF()).intern();
      hostmaster = in.readUTF().intern();
      serial = in.readLong();
      ttl = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<Zone>> getCannotRemoveReasons() {
    List<CannotRemoveReason<Zone>> reasons = new ArrayList<>();
    if (pkey.equals(API_ZONE)) {
      reasons.add(new CannotRemoveReason<>("Not allowed to remove the API Zone: " + API_ZONE, this));
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.DNS_ZONES, pkey);
  }

  public void setTtl(int ttl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_DNS_ZONE_TTL, pkey, ttl);
    this.ttl = ttl;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(file);
    out.writeUTF(packageName.toString());
    out.writeUTF(hostmaster);
    out.writeLong(serial);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_127) >= 0) {
      out.writeCompressedInt(ttl);
    }
  }
}
