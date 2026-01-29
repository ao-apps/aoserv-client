/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

  static final int COLUMN_TYPE = 0;
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
      TXT   = "TXT";

  private String description;
  private boolean hasPriority;
  private boolean hasWeight;
  private boolean hasPort;
  private boolean hasFlag;
  private boolean hasTag;
  private boolean paramIp;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  RecordType#init(java.sql.ResultSet)
   * @see  RecordType#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public RecordType() {
    // Do nothing
  }

  public void checkDestination(String tag, String destination) throws IllegalArgumentException {
    checkDestination(pkey, tag, destination);
  }

  @SuppressWarnings("deprecation")
  public static void checkDestination(String type, String tag, String destination) throws IllegalArgumentException {
    if (destination.isEmpty()) {
      throw new IllegalArgumentException("Destination may not by empty");
    }

    if (type.equals(A)) {
      try {
        InetAddress parsed = InetAddress.valueOf(destination);
        if (parsed.getAddressFamily() != com.aoapps.net.AddressFamily.INET) {
          throw new IllegalArgumentException("A type requires IPv4 address: " + destination);
        }
      } catch (ValidationException e) {
        throw new IllegalArgumentException(e.getLocalizedMessage(), e);
      }
    } else if (type.equals(AAAA)) {
      try {
        InetAddress parsed = InetAddress.valueOf(destination);
        if (parsed.getAddressFamily() != com.aoapps.net.AddressFamily.INET6) {
          throw new IllegalArgumentException("AAAA type requires IPv6 address: " + destination);
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
      String validateMe;
      String validateType;
      if (type.equals(CNAME)) {
        // Per RFC 2181, section 11:
        // "any binary string whatever can be used as the label of any resource record"
        // "any binary string can serve as the value of any record that includes a domain name as some or all of its value"
        // https://www.rfc-editor.org/rfc/rfc2181#section-11
        //
        // We're going to only add additional characters as-needed for CNAME.

        // Underscores may be used in DNS despite not being valid hostname (as defined in RFC 1123, section 2.1):
        // https://www.rfc-editor.org/rfc/rfc1123#page-13
        validateMe = destination.replace('_', 'a');
        validateType = "CNAME";
      } else {
        validateMe = destination;
        validateType = "hostname";
      }
      // May end with a single .
      if (validateMe.charAt(validateMe.length() - 1) == '.') {
        validateMe = validateMe.substring(0, validateMe.length() - 1);
      }
      if (
          !ZoneTable.isValidHostnamePart(validateMe)
              && !DomainName.validate(validateMe).isValid()
      ) {
        throw new IllegalArgumentException("Invalid destination " + validateType + ": " + destination);
      }
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_TYPE:
        return pkey;
      case 1:
        return description;
      case 2:
        return hasPriority;
      case 3:
        return hasWeight;
      case 4:
        return hasPort;
      case 5:
        return hasFlag;
      case 6:
        return hasTag;
      case 7:
        return paramIp;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDescription() {
    return description;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_TYPES;
  }

  public String getType() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey         = result.getString("type");
    description  = result.getString("description");
    hasPriority = result.getBoolean("has_priority");
    hasWeight   = result.getBoolean("has_weight");
    hasPort     = result.getBoolean("has_port");
    hasFlag     = result.getBoolean("has_flag");
    hasTag      = result.getBoolean("has_tag");
    paramIp     = result.getBoolean("param_ip");
  }

  public boolean hasPriority() {
    return hasPriority;
  }

  public boolean hasWeight() {
    return hasWeight;
  }

  public boolean hasPort() {
    return hasPort;
  }

  public boolean hasFlag() {
    return hasFlag;
  }

  public boolean hasTag() {
    return hasTag;
  }

  public boolean isParamIp() {
    return paramIp;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey         = in.readUTF().intern();
    description  = in.readUTF();
    hasPriority = in.readBoolean();
    hasWeight   = in.readBoolean();
    hasPort     = in.readBoolean();
    hasFlag     = in.readBoolean();
    hasTag      = in.readBoolean();
    paramIp     = in.readBoolean();
  }

  @Override
  public String toStringImpl() {
    return description;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(description);
    out.writeBoolean(hasPriority);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_72) >= 0) {
      out.writeBoolean(hasWeight);
      out.writeBoolean(hasPort);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_86_0) >= 0) {
      out.writeBoolean(hasFlag);
      out.writeBoolean(hasTag);
    }
    out.writeBoolean(paramIp);
  }
}
