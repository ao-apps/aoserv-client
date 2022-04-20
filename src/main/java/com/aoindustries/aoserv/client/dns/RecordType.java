/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.DomainName;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>DNSType</code> associated with a <code>DNSRecord</code> provides
 * details about which values should be used in the destination field, and whether
 * a priority, weight, and port should exist.
 *
 * @see  Record
 *
 * @author  AO Industries, Inc.
 */
public final class RecordType extends GlobalObjectStringKey<RecordType> {

  static final int COLUMN_TYPE=0;
  static final String COLUMN_DESCRIPTION_name = "description";

  /**
   * The possible <code>DNSType</code>s.
   */
  public static final String
    A     = "A",
    AAAA  = "AAAA",
    CNAME = "CNAME",
    CAA   = "CAA",
    MX    = "MX",
    NS    = "NS",
    PTR   = "PTR",
    SRV   = "SRV",
    TXT   = "TXT"
  ;

  private String description;
  private boolean
    has_priority,
    has_weight,
    has_port,
    has_flag,
    has_tag,
    param_ip
  ;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public RecordType() {
    // Do nothing
  }

  public void checkDestination(String tag, String destination) throws IllegalArgumentException {
    checkDestination(pkey, tag, destination);
  }

  @SuppressWarnings("deprecation")
  public static void checkDestination(String type, String tag, String destination) throws IllegalArgumentException {
    String origDest = destination;
    if (destination.isEmpty()) {
      throw new IllegalArgumentException("Destination may not by empty");
    }

    if (type.equals(A)) {
      try {
        InetAddress parsed = InetAddress.valueOf(destination);
        if (parsed.getAddressFamily() != com.aoapps.net.AddressFamily.INET) {
          throw new IllegalArgumentException("A type requires IPv4 address: "+destination);
        }
      } catch (ValidationException e) {
        throw new IllegalArgumentException(e.getLocalizedMessage(), e);
      }
    } else if (type.equals(AAAA)) {
      try {
        InetAddress parsed = InetAddress.valueOf(destination);
        if (parsed.getAddressFamily() != com.aoapps.net.AddressFamily.INET6) {
          throw new IllegalArgumentException("AAAA type requires IPv6 address: "+destination);
        }
      } catch (ValidationException e) {
        throw new IllegalArgumentException(e.getLocalizedMessage(), e);
      }
    } else if (type.equals(CAA)) {
      if (tag == null) {
        throw new IllegalArgumentException("tag required for type = " + CAA);
      }
      if (Record.CAA_TAG_ISSUE.equals(tag) || Record.CAA_TAG_ISSUEWILD.equals(tag)) {
        int semicolon = destination.indexOf(';');
        String issuerDomainName = (semicolon == -1) ? destination : destination.substring(0, semicolon);
        // May be empty to match none
        if (!issuerDomainName.isEmpty()) {
          // Or may be a valid domain name
          ValidationResult result = DomainName.validate(issuerDomainName);
          if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid issuer domain: " + result.toString());
          }
        }
        // TODO: Could also verify name=value formatting for that following ";"
      }
      // TODO: Checks by tag
    } else if (type.equals(TXT)) {
      // Pretty much anything goes?
      // TODO: What are the rules for what is allowed in TXT?  Where do we enforce this currently?
    } else {
      // May end with a single .
      if (destination.charAt(destination.length()-1) == '.') {
        destination=destination.substring(0, destination.length()-1);
      }
      if (
        !ZoneTable.isValidHostnamePart(destination)
        && !DomainName.validate(destination).isValid()
      ) {
        throw new IllegalArgumentException("Invalid destination hostname: "+origDest);
      }
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_TYPE : return pkey;
      case 1 : return description;
      case 2 : return has_priority;
      case 3 : return has_weight;
      case 4 : return has_port;
      case 5 : return has_flag;
      case 6 : return has_tag;
      case 7 : return param_ip;
      default : throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDescription() {
    return description;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.DNS_TYPES;
  }

  public String getType() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey         = result.getString("type");
    description  = result.getString("description");
    has_priority = result.getBoolean("has_priority");
    has_weight   = result.getBoolean("has_weight");
    has_port     = result.getBoolean("has_port");
    has_flag     = result.getBoolean("has_flag");
    has_tag      = result.getBoolean("has_tag");
    param_ip     = result.getBoolean("param_ip");
  }

  public boolean hasPriority() {
    return has_priority;
  }

  public boolean hasWeight() {
    return has_weight;
  }

  public boolean hasPort() {
    return has_port;
  }

  public boolean hasFlag() {
    return has_flag;
  }

  public boolean hasTag() {
    return has_tag;
  }

  public boolean isParamIP() {
    return param_ip;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey         = in.readUTF().intern();
    description  = in.readUTF();
    has_priority = in.readBoolean();
    has_weight   = in.readBoolean();
    has_port     = in.readBoolean();
    has_flag     = in.readBoolean();
    has_tag      = in.readBoolean();
    param_ip     = in.readBoolean();
  }

  @Override
  public String toStringImpl() {
    return description;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(description);
    out.writeBoolean(has_priority);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_72) >= 0) {
      out.writeBoolean(has_weight);
      out.writeBoolean(has_port);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_86_0) >= 0) {
      out.writeBoolean(has_flag);
      out.writeBoolean(has_tag);
    }
    out.writeBoolean(param_ip);
  }
}
