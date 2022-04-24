/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.util.InternUtils;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DNSRecord</code> is one line of a <code>DNSZone</code>
 * (name server zone file).
 *
 * @see  Zone
 *
 * @author  AO Industries, Inc.
 */
public final class Record extends CachedObjectIntegerKey<Record> implements Removable {

  static final int
      COLUMN_ID = 0,
      COLUMN_ZONE = 1
  ;
  static final String COLUMN_ZONE_name        = "zone";
  static final String COLUMN_DOMAIN_name      = "domain";
  static final String COLUMN_TYPE_name        = "type";
  static final String COLUMN_PRIORITY_name    = "priority";
  static final String COLUMN_WEIGHT_name      = "weight";
  static final String COLUMN_TAG_name         = "tag";
  static final String COLUMN_DESTINATION_name = "destination";

  public static final int   NO_PRIORITY = -1;
  public static final int   NO_WEIGHT   = -1;
  public static final int   NO_PORT     = -1;
  public static final short NO_FLAG     = -1;
  public static final int   NO_TTL      = -1;

  public static final String CAA_TAG_ISSUE        = "issue";
  public static final String CAA_TAG_ISSUEWILD    = "issuewild";
  public static final String CAA_TAG_IODEF        = "iodef";
  public static final String CAA_TAG_CONTACTEMAIL = "contactemail";
  public static final String CAA_TAG_CONTACTPHONE = "contactphone";

  private String zone;
  private String domain;
  private String type;
  private int priority;
  private int weight;
  private int port;
  private short flag;
  private String tag;
  private String destination;
  private int dhcpAddress;
  private int ttl;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Record() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID: return pkey;
      case COLUMN_ZONE: return zone;
      case 2: return domain;
      case 3: return type;
      case 4: return priority == NO_PRIORITY ? null : priority;
      case 5: return weight == NO_WEIGHT     ? null : weight;
      case 6: return port == NO_PORT         ? null : port;
      case 7: return flag == NO_FLAG         ? null : flag;
      case 8: return tag;
      case 9: return destination;
      case 10: return dhcpAddress == -1      ? null : dhcpAddress;
      case 11: return ttl == NO_TTL          ? null : ttl;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getId() {
    return pkey;
  }

  public String getZone_zone() {
    return zone;
  }

  public Zone getZone() throws SQLException, IOException {
    Zone obj = table.getConnector().getDns().getZone().get(zone);
    if (obj == null) {
      throw new SQLException("Unable to find DNSZone: " + zone);
    }
    return obj;
  }

  public String getDomain() {
    return domain;
  }

  public String getType_type() {
    return type;
  }

  public RecordType getType() throws SQLException, IOException {
    RecordType obj = table.getConnector().getDns().getRecordType().get(type);
    if (obj == null) {
      throw new SQLException("Unable to find DNSType: " + type);
    }
    return obj;
  }

  public int getPriority() {
    return priority;
  }

  public int getWeight() {
    return weight;
  }

  public int getPort() {
    return port;
  }

  /**
   * Verifies a flag is either {@link #NO_FLAG} or between 0 and 0xFF.
   */
  public static boolean isValidFlag(short flag) {
    return flag == NO_FLAG || (flag >= 0 && flag <= 0xFF);
  }

  /**
   * @return  {@link #NO_FLAG} when none, or a value between 0 and 0xFF.
   */
  public short getFlag() {
    assert isValidFlag(flag);
    return flag;
  }

  public String getTag() {
    return tag;
  }

  public String getDestination() {
    return destination;
  }

  public Integer getDhcpAddress_id() {
    return dhcpAddress == -1 ? null : dhcpAddress;
  }

  public IpAddress getDhcpAddress() throws SQLException, IOException {
    if (dhcpAddress == -1) {
      return null;
    }
    IpAddress ia = table.getConnector().getNet().getIpAddress().get(dhcpAddress);
    if (ia == null) {
      throw new SQLException("Unable to find IPAddress: " + dhcpAddress);
    }
    return ia;
  }

  public int getTtl() {
    return ttl;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey        = result.getInt("id");
    zone        = result.getString("zone");
    domain      = result.getString("domain");
    type        = result.getString("type");
    priority    = result.getInt("priority");
    if (result.wasNull()) {
      priority = NO_PRIORITY;
    }
    weight      = result.getInt("weight");
    if (result.wasNull()) {
      weight = NO_WEIGHT;
    }
    port        = result.getInt("port");
    if (result.wasNull()) {
      port = NO_PORT;
    }
    flag        = result.getShort("flag");
    if (result.wasNull()) {
      flag = NO_FLAG;
    }
    if (!isValidFlag(flag)) {
      throw new SQLException("Invalid flag: " + flag);
    }
    tag         = result.getString("tag");
    destination = result.getString("destination");
    dhcpAddress = result.getInt("dhcpAddress");
    if (result.wasNull()) {
      dhcpAddress = -1;
    }
    ttl         = result.getInt("ttl");
    if (result.wasNull()) {
      ttl = NO_TTL;
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey        = in.readCompressedInt();
    zone        = in.readUTF().intern();
    domain      = in.readUTF().intern();
    type        = in.readUTF().intern();
    priority    = in.readCompressedInt();
    weight      = in.readCompressedInt();
    port        = in.readCompressedInt();
    flag        = in.readShort();
    if (!isValidFlag(flag)) {
      throw new IOException("Invalid flag: " + flag);
    }
    tag         = InternUtils.intern(in.readNullUTF());
    destination = in.readUTF().intern();
    dhcpAddress = in.readCompressedInt();
    ttl         = in.readCompressedInt();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(zone);
    out.writeUTF(domain);
    out.writeUTF(type);
    out.writeCompressedInt(priority);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_72) >= 0) {
      out.writeCompressedInt(weight);
      out.writeCompressedInt(port);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_86_0) >= 0) {
      assert isValidFlag(flag);
      out.writeShort(flag);
      out.writeNullUTF(tag);
    }
    out.writeUTF(destination);
    out.writeCompressedInt(dhcpAddress);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_127) >= 0) {
      out.writeCompressedInt(ttl);
    }
  }

  /**
   * Gets the domain, but in fully-qualified, absolute path (with trailing period).
   */
  public String getAbsoluteDomain() {
    if (domain.equals("@")) {
      return zone;
    }
    if (domain.endsWith(".")) {
      return domain;
    }
    return domain + '.' + zone;
  }

  /**
   * Strips a destination of characters not allowed in CAA or TXT records.
   * Removes any double quotes, anything below space <code>' '</code>,
   * or anything <code>&gt;= (char)0x7f</code>.  Also trims the entry after
   * characters are escaped.
   */
  static String cleanTxt(String destination) {
    int len = destination.length();
    StringBuilder txt = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char ch = destination.charAt(i);
      if (
          ch != '"'
              && ch >= ' '
              && ch < (char) 0x7f
      ) {
        txt.append(ch);
      }
    }
    String cleaned = txt.length() == len ? destination : txt.toString();
    return cleaned.trim();
  }

  private static boolean isSpf1(String destination) {
    String txt = cleanTxt(destination);
    return txt.equals("v=spf1") || txt.startsWith("v=spf1 ");
  }

  /**
   * Checks if this record conflicts with the provided record, meaning they may not both exist
   * in a zone file at the same time.  The current conflicts checked are:
   * <ol>
   *   <li>CNAME must exist by itself, and only one CNAME maximum, per domain</li>
   *   <li>
   *     Multiple TXT entries of "v=spf1", with or without surrounded by quotes, see
   *     <a href="http://www.openspf.org/RFC_4408#version">4.5. Selecting Records</a>.
   *   </li>
   * </ol>
   *
   * @return <code>true</code> if there is a conflict, <code>false</code> if the records may coexist.
   */
  public boolean hasConflict(Record other) {
    String domain1 = getAbsoluteDomain();
    String domain2 = other.getAbsoluteDomain();

    // Look for CNAME conflict
    if (domain1.equals(domain2)) {
      // If either (or both) are CNAME, there is a conflict
      if (
          type.equals(RecordType.CNAME)
              || other.type.equals(RecordType.CNAME)
      ) {
        return true;
      }
      // If both are TXT types, and v=spf1, there is a conflict
      if (
          type.equals(RecordType.TXT)
              && other.type.equals(RecordType.TXT)
              && isSpf1(destination)
              && isSpf1(other.destination)
      ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.DNS_RECORDS;
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.DNS_RECORDS, pkey);
  }

  @Override
  public String toStringImpl() {
    StringBuilder sb = new StringBuilder();
    sb.append(zone).append(": ").append(domain);
    if (ttl != NO_TTL) {
      sb.append(' ').append(ttl);
    }
    sb.append(" IN ").append(type);
    if (priority != NO_PRIORITY) {
      sb.append(' ').append(priority);
    }
    if (weight   != NO_WEIGHT) {
      sb.append(' ').append(weight);
    }
    if (port     != NO_PORT) {
      sb.append(' ').append(port);
    }
    if (flag     != NO_FLAG) {
      sb.append(' ').append(flag);
    }
    if (tag      != null) {
      sb.append(' ').append(tag);
    }
    sb.append(' ').append(destination);
    return sb.toString();
  }
}
