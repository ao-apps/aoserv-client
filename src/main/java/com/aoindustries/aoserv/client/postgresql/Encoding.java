/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.postgresql;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Beginning with PostgreSQL 7.1, multiple character encoding formats are
 * supported, the <code>PostgresEncoding</code>s represent the possible
 * formats.
 *
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
public final class Encoding extends GlobalObjectIntegerKey<Encoding> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_POSTGRES_VERSION = 2;
  static final String COLUMN_ENCODING_name = "encoding";
  static final String COLUMN_POSTGRES_VERSION_name = "postgres_version";
  static final String COLUMN_PKEY_name = "pkey";

  /**
   * The supported encodings.
   */
  public static final String
      ALT = "ALT",                      // 7.1    7.2   7.3   8.0
      BIG5 = "BIG5",                    // 7.1    7.2   7.3
      EUC_CN = "EUC_CN",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      EUC_JIS_2004 = "EUC_JIS_2004",    //                          9.4   9.5   9.6   10   11   12   13   14   15
      EUC_JP = "EUC_JP",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      EUC_KR = "EUC_KR",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      EUC_TW = "EUC_TW",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      GB18030 = "GB18030",              //              7.3
      GBK = "GBK",                      //              7.3
      ISO_8859_5 = "ISO_8859_5",        //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      ISO_8859_6 = "ISO_8859_6",        //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      ISO_8859_7 = "ISO_8859_7",        //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      ISO_8859_8 = "ISO_8859_8",        //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      JOHAB = "JOHAB",                  //              7.3   8.0
      KOI8 = "KOI8",                    // 7.1    7.2   7.3   8.0
      KOI8R = "KOI8R",                  //                          9.4   9.5   9.6   10   11   12   13   14   15
      KOI8U = "KOI8U",                  //                          9.4   9.5   9.6   10   11   12   13   14   15
      LATIN1 = "LATIN1",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN2 = "LATIN2",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN3 = "LATIN3",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN4 = "LATIN4",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN5 = "LATIN5",                // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN6 = "LATIN6",                //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN7 = "LATIN7",                //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN8 = "LATIN8",                //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN9 = "LATIN9",                //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      LATIN10 = "LATIN10",              //        7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      MULE_INTERNAL = "MULE_INTERNAL",  // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      SJIS = "SJIS",                    // 7.1    7.2   7.3
      SQL_ASCII = "SQL_ASCII",          // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      UTF8 = "UTF8",                    //                          9.4   9.5   9.6   10   11   12   13   14   15
      TCVN = "TCVN",                    //              7.3   8.0
      UHC = "UHC",                      //              7.3
      UNICODE = "UNICODE",              // 7.1    7.2   7.3   8.0
      WIN = "WIN",                      // 7.1    7.2   7.3   8.0
      WIN866 = "WIN866",                //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN874 = "WIN874",                //              7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      WIN1250 = "WIN1250",              // 7.1    7.2   7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      WIN1251 = "WIN1251",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1252 = "WIN1252",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1253 = "WIN1253",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1254 = "WIN1254",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1255 = "WIN1255",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1256 = "WIN1256",              //              7.3   8.0   9.4   9.5   9.6   10   11   12   13   14   15
      WIN1257 = "WIN1257",              //                          9.4   9.5   9.6   10   11   12   13   14   15
      WIN1258 = "WIN1258";              //                          9.4   9.5   9.6   10   11   12   13   14   15

  /**
   * Gets the default PostgreSQL encoding for the given database version.
   * Version &lt;= 8.3: SQL_ASCII.
   * Version &gt;= 9.4: UTF8
   */
  public static String getDefaultEncoding(String version) {
    if (
        version.startsWith("7.")
            || version.startsWith("8.")
    ) {
      return SQL_ASCII;
    } else {
      return UTF8;
    }
  }

  private String encoding;
  private int postgresVersion;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Encoding() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return encoding;
      case COLUMN_POSTGRES_VERSION:
        return postgresVersion;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getEncoding() {
    return encoding;
  }

  public Version getPostgresVersion(AoservConnector connector) throws SQLException, IOException {
    Version pv = connector.getPostgresql().getVersion().get(postgresVersion);
    if (pv == null) {
      throw new SQLException("Unable to find PostgresVersion: " + postgresVersion);
    }
    return pv;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.POSTGRES_ENCODINGS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    encoding = result.getString(2);
    postgresVersion = result.getInt(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    encoding = in.readUTF().intern();
    postgresVersion = in.readCompressedInt();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(encoding);
    out.writeCompressedInt(postgresVersion);
  }
}
