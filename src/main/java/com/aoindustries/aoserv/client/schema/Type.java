/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.schema;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainLabel;
import com.aoapps.net.DomainLabels;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.MacAddress;
import com.aoapps.net.Port;
import com.aoapps.security.HashedKey;
import com.aoapps.security.HashedPassword;
import com.aoapps.security.Identifier;
import com.aoapps.security.SmallIdentifier;
import com.aoapps.sql.SQLUtility;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.linux.LinuxId;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.net.FirewallZone;
import com.aoindustries.aoserv.client.net.IpAddress;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A {@link Type} is a unique data type used for
 * {@link Column}.
 *
 * @see  Column#getType(com.aoindustries.aoserv.client.AoservConnector)
 *
 * @author  AO Industries, Inc.
 */
public final class Type extends GlobalObjectIntegerKey<Type> {

  /**
   * The time zone used for all <code>{@link #DATE}</code> to/from String conversions.
   * Is "GMT" and is not expected to change, but all date-based parsing, formatting, conversions,
   * comparisons, or filters should use this constant as the time zone.
   */
  public static final TimeZone DATE_TIME_ZONE = TimeZone.getTimeZone("GMT");

  static final int COLUMN_NAME = 1;
  public static final String DATE_name = "date";

  // TODO: No longer pass type ids, instead pass per-protocol schema+type names

  /**
   * @see com.aoindustries.aoserv.client.account.Account.Name
   */
  public static final int ACCOUNTING =  0;

  /**
   * @see java.lang.Boolean
   */
  public static final int BOOLEAN =  1;

  // public static final int BYTE =  2; // byte/java.lang.Byte
  // public static final int CITY =  3; // java.lang.String
  // public static final int COUNTRY =  4; // java.lang.String

  /**
   * TODO: UnmodifiableDate.
   *
   * @see java.sql.Date
   */
  public static final int DATE =  5;

  /**
   * @see java.math.BigDecimal
   */
  public static final int DECIMAL_2 =  6;

  /**
   * @see java.math.BigDecimal
   */
  public static final int DECIMAL_3 =  7;

  /**
   * @see java.lang.Double
   */
  public static final int DOUBLE =  8;

  /**
   * @see com.aoapps.net.Email
   */
  public static final int EMAIL =  9;

  /**
   * @see java.lang.Integer
   */
  public static final int FKEY = 10;

  /**
   * @see java.lang.Float
   */
  public static final int FLOAT = 11;

  /**
   * @see com.aoapps.net.HostAddress
   */
  public static final int HOSTNAME = 12;

  /**
   * @see java.lang.Integer
   */
  public static final int INT = 13;

  /**
   * @see java.lang.Long
   */
  public static final int INTERVAL = 14;

  /**
   * @see com.aoapps.net.InetAddress
   */
  public static final int IP_ADDRESS = 15;

  /**
   * @see java.lang.Long
   */
  public static final int LONG = 16;

  // public static final int OCTAL_INT = 17; // int/java.lang.Integer

  /**
   * @see java.lang.Long
   */
  public static final int OCTAL_LONG = 18;

  // public static final int PACKAGE = 19; // com.aoindustries.aoserv.client.validator.AccountingCode

  /**
   * @see java.lang.Integer
   */
  public static final int PKEY = 20;

  /**
   * @see com.aoindustries.aoserv.client.linux.PosixPath
   */
  public static final int PATH = 21;

  /**
   * @see java.lang.String
   */
  public static final int PHONE = 22;

  /**
   * @see java.lang.Short
   */
  public static final int SHORT = 23; // short/java.lang.Short

  // public static final int STATE = 24; // java.lang.String

  /**
   * @see java.lang.String
   */
  public static final int STRING = 25;

  /**
   * @see UnmodifiableTimestamp
   */
  public static final int TIME = 26;

  /**
   * @see java.net.URL
   */
  public static final int URL = 27;

  /**
   * @see com.aoindustries.aoserv.client.account.User.Name
   */
  public static final int USERNAME = 28;

  // public static final int ZIP = 29; // java.lang.String

  /**
   * @see java.lang.String
   */
  public static final int ZONE = 30;

  /**
   * @see java.math.BigDecimal
   */
  public static final int BIG_DECIMAL = 31;

  /**
   * @see com.aoapps.net.DomainLabel
   */
  public static final int DOMAIN_LABEL = 32;

  /**
   * @see com.aoapps.net.DomainLabels
   */
  public static final int DOMAIN_LABELS = 33;

  /**
   * @see com.aoapps.net.DomainName
   */
  public static final int DOMAIN_NAME = 34;

  /**
   * @see com.aoindustries.aoserv.client.linux.User.Gecos
   */
  public static final int GECOS = 35;

  /**
   * @see com.aoindustries.aoserv.client.linux.Group.Name
   */
  public static final int GROUP_ID = 36;

  /**
   * @see HashedPassword
   */
  public static final int HASHED_PASSWORD = 37;

  /**
   * @see com.aoindustries.aoserv.client.linux.LinuxId
   */
  public static final int LINUX_ID = 38;

  /**
   * @see com.aoapps.net.MacAddress
   */
  public static final int MAC_ADDRESS = 39;

  /**
   * @see com.aoapps.lang.i18n.Money
   */
  public static final int MONEY = 40;

  /**
   * @see com.aoindustries.aoserv.client.mysql.Database.Name
   */
  public static final int MYSQL_DATABASE_NAME = 41;

  /**
   * @see com.aoindustries.aoserv.client.mysql.Server.Name
   */
  public static final int MYSQL_SERVER_NAME = 42;

  /**
   * @see com.aoindustries.aoserv.client.mysql.TableName
   */
  public static final int MYSQL_TABLE_NAME = 43;

  /**
   * @see com.aoindustries.aoserv.client.mysql.User.Name
   */
  public static final int MYSQL_USERNAME = 44;

  /**
   * @see com.aoapps.net.Port
   */
  public static final int NET_PORT = 45;

  /**
   * @see com.aoindustries.aoserv.client.postgresql.Database.Name
   */
  public static final int POSTGRES_DATABASE_NAME = 46;

  /**
   * @see com.aoindustries.aoserv.client.postgresql.Server.Name
   */
  public static final int POSTGRES_SERVER_NAME = 47;

  /**
   * @see com.aoindustries.aoserv.client.postgresql.User.Name
   */
  public static final int POSTGRES_USERNAME = 48;

  /**
   * @see com.aoindustries.aoserv.client.net.FirewallZone.Name
   */
  public static final int FIREWALLD_ZONE_NAME = 49;

  /**
   * @see com.aoindustries.aoserv.client.linux.User.Name
   */
  public static final int LINUX_USERNAME = 50;

  /**
   * @see Identifier
   */
  public static final int IDENTIFIER = 51;

  /**
   * @see SmallIdentifier
   */
  public static final int SMALL_IDENTIFIER = 52;

  /**
   * @see HashedKey
   */
  public static final int HASHED_KEY = 53;

  private static final BigDecimal bigDecimalNegativeOne = BigDecimal.valueOf(-1);

  private String name;
  private String sinceVersion;
  private String lastVersion;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Type() {
    // Do nothing
  }

  public boolean alignRight() {
    return alignRight(pkey);
  }

  public static boolean alignRight(int id) {
    switch (id) {
      case DATE:
      case DECIMAL_2:
      case DECIMAL_3:
      case DOUBLE:
      case FKEY:
      case FLOAT:
      case INT:
      case INTERVAL:
      case LONG:
      case OCTAL_LONG:
      case PKEY:
      case SHORT:
      case TIME:
      case BIG_DECIMAL:
      case LINUX_ID:
      case MONEY:
      case NET_PORT:
        return true;
      default:
        return false;
    }
  }

  /**
   * Casts one type of object to another.  These casts are allowed:
   *
   * <pre>                                                                                                     P
   *                                                                                                      O
   *                                                                                                      S P
   *                                                                                            M         T O   F
   *                                                                                            Y         G S   I
   *                                                                                            S M       R T P R
   *                                                                                            Q Y M     E G O E     S
   *                                                                                    H       L S Y     S R S W     M
   *                                                                                    A       _ Q S M   _ E T A L   A
   *                                                                            D       S       D L Q Y   D S G L I   L
   *                                                                          D O       H       A _ L S   A _ R L N   L
   *                                                                        B O M D     E   M   T S _ Q   T S E D U   _
   *                        A                       I   O                   I M A O     D   A   A E T L   A E S _ X I I H
   *                        C     D D               P   C                   G A I M     _   C   B R A _   B R _ Z _ D D A
   *                        C     E E         H   I _   T               U   _ I N A   G P L _   A V B U N A V U O U E E S
   *                      T O B   C C         O   N A   A               S   D N _ I   R A I A   S E L S E S E S N S N N H
   *                      O U O   I I D       S   T D   L         S     E   E _ L N   O S N D   E R E E T E R E E E T T E
   *                        N O   M M O E   F T   E D   _     P S T     R   C L A _ G U S U D M _ _ _ R _ _ _ R _ R I I D
   *                        T L D A A U M F L N   R R L L P P H H R T   N Z I A B N E P W X R O N N N N P N N N N N F F _
   *                        I E A L L B A K O A I V E O O K A O O I I U A O M B E A C _ O _ E N A A A A O A A A A A I I K
   *                        N A T _ _ L I E A M N A S N N E T N R N M R M N A E L M O I R I S E M M M M R M M M M M E E E
   *               FROM     G N E 2 3 E L Y T E T L S G G Y H E T G E L E E L L S E S D D D S Y E E E E T E E E E E R R Y
   *
   *             ACCOUNTING X                                     X
   *                BOOLEAN   X   X X X     X   X     X X       X X         X
   *                   DATE     X X X X     X   X     X X       X X X       X
   *              DECIMAL_2   X X X X X     X   X X   X X       X X         X
   *              DECIMAL_3   X X X X X     X   X X   X X       X X         X
   *                 DOUBLE   X X X X X     X   X X   X X       X X         X
   *                  EMAIL             X     X                   X   X X X       X                               X
   *                   FKEY               X     X         X       X
   *                  FLOAT   X X X X X     X   X X   X X       X X         X
   *               HOSTNAME                   X     X             X       X       X
   *                    INT   X X X X X   X X   X X X X X X     X X         X             X
   *               INTERVAL       X X X     X   X X   X X       X X         X
   *             IP_ADDRESS                         X             X
   *                   LONG   X X X X X     X   X X   X X       X X X       X                                         X
   *             OCTAL_LONG   X X X X X     X   X X   X X       X X X       X                                         X
   *                   PKEY               X     X         X       X
   *                   PATH                                 X     X
   *                  PHONE                                   X   X X
   *                  SHORT   X X X X X     X   X X   X X       X X         X
   *                 STRING X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X   X X X X X X X X X X X X X
   *                   TIME     X                     X X         X X
   *                    URL                   X             X     X   X   X       X
   *               USERNAME                                       X     X                             X       X   X
   *                   ZONE                   X                   X       X       X
   *            BIG_DECIMAL   X X X X X     X   X X   X X       X X         X                                         X
   *           DOMAIN_LABEL                                       X           X X X
   *          DOMAIN_LABELS                                       X             X X
   *            DOMAIN_NAME                   X                   X       X     X X
   *                  GECOS                                       X                 X
   *               GROUP_ID                                       X                   X
   *        HASHED_PASSWORD                                       X                     X
   *               LINUX_ID   X                 X                 X                       X
   *            MAC_ADDRESS                                       X                         X
   *                  MONEY                                       X         X                 X
   *    MYSQL_DATABASE_NAME                                       X                             X
   *      MYSQL_SERVER_NAME                                       X                               X
   *       MYSQL_TABLE_NAME                                       X                                 X
   *         MYSQL_USERNAME                                       X     X                             X           X
   *               NET_PORT                     X                 X                                     X
   * POSTGRES_DATABASE_NAME                                       X                                       X
   *   POSTGRES_SERVER_NAME                                       X                                         X
   *      POSTGRES_USERNAME                                       X     X                                     X   X
   *    FIREWALLD_ZONE_NAME                                       X                                             X
   *         LINUX_USERNAME                                       X     X                             X       X   X
   *             IDENTIFIER                                       X         X                                       X
   *       SMALL_IDENTIFIER                           X X         X         X                                         X
   *             HASHED_KEY                                       X                                                     X</pre>
   */
  public Object cast(AoservConnector conn, Object value, Type castToType) throws IOException, SQLException {
    try {
      // When castToType is this type, just return value directly
      if (pkey == castToType.pkey) {
        return value;
      }
      // When casting to STRING, use regular string conversion method
      if (castToType.pkey == STRING) {
        return getString(value, pkey);
      }
      switch (pkey) {
        case ACCOUNTING:
          {
            // No special casts
            break;
          }
        case BOOLEAN:
          {
            switch (castToType.getId()) {
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((Boolean) value ? -100 : 0);
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((Boolean) value ? -1000 : 0);
              case DOUBLE:
                return value == null ? null : Double.valueOf((Boolean) value ? -1 : 0);
              case FLOAT:
                return value == null ? null : Float.valueOf((Boolean) value ? -1 : 0);
              case INT:
                return value == null ? null : Integer.valueOf((Boolean) value ? -1 : 0);
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf((Boolean) value ? -1 : 0);
              case SHORT:
                return value == null ? null : Short.valueOf((Boolean) value ? (short) -1 : 0);
              case BIG_DECIMAL:
                return value == null ? null : (Boolean) value ? bigDecimalNegativeOne : BigDecimal.ZERO;
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DATE:
          {
            long tvalue = value == null ? 0 : ((java.sql.Date) value).getTime();
            switch (castToType.getId()) {
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((int) (getDaysFromMillis(tvalue) * 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((int) (getDaysFromMillis(tvalue) * 1000));
              case DOUBLE:
                return value == null ? null : Double.valueOf(getDaysFromMillis(tvalue));
              case FLOAT:
                return value == null ? null : Float.valueOf(getDaysFromMillis(tvalue));
              case INT:
                return value == null ? null : Integer.valueOf((int) (getDaysFromMillis(tvalue)));
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(getDaysFromMillis(tvalue));
              case SHORT:
                return value == null ? null : Short.valueOf((short) (getDaysFromMillis(tvalue)));
              case TIME:
                return value == null ? null : new UnmodifiableTimestamp(roundToDate(tvalue));
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf(getDaysFromMillis(tvalue));
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DECIMAL_2:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Integer) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays((Integer) value / 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((Integer) value * 10);
              case DOUBLE:
                return value == null ? null : Double.valueOf((double) (Integer) value / 100);
              case FLOAT:
                return value == null ? null : Float.valueOf((float) (Integer) value / 100);
              case INT:
                return value == null ? null : Integer.valueOf((Integer) value / 100);
              case INTERVAL:
                return value == null ? null : Long.valueOf((Integer) value / 100);
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf((Integer) value / 100);
              case SHORT:
                return value == null ? null : Short.valueOf((short) ((Integer) value / 100));
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf((Integer) value, 2);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DECIMAL_3:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Integer) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays((Integer) value / 1000));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((Integer) value / 10);
              case DOUBLE:
                return value == null ? null : Double.valueOf((double) (Integer) value / 1000);
              case FLOAT:
                return value == null ? null : Float.valueOf((float) (Integer) value / 1000);
              case INT:
                return value == null ? null : Integer.valueOf((Integer) value / 1000);
              case INTERVAL:
                return value == null ? null : Long.valueOf((Integer) value / 1000);
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf((Integer) value / 1000);
              case SHORT:
                return value == null ? null : Short.valueOf((short) ((Integer) value / 1000));
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf((Integer) value, 3);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DOUBLE:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Double) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays(((Double) value).longValue()));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((int) ((Double) value * 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((int) ((Double) value * 1000));
              case FLOAT:
                return value == null ? null : Float.valueOf(((Double) value).floatValue());
              case INT:
                return value == null ? null : Integer.valueOf(((Double) value).intValue());
              case INTERVAL:
                return value == null ? null : Long.valueOf(((Double) value).longValue());
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(((Double) value).longValue());
              case SHORT:
                return value == null ? null : Short.valueOf(((Double) value).shortValue());
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf((Double) value);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case EMAIL:
          {
            switch (castToType.getId()) {
              case HOSTNAME:
                return value == null ? null : HostAddress.valueOf(((Email) value).getDomain());
              case URL:
                return value == null ? null : "mailto:" + value;
              case USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.account.User.Name.valueOf(getUsernameForEmail((Email) value));
              case ZONE:
                return value == null ? null : getZoneForDomainName(conn, ((Email) value).getDomain());
              case DOMAIN_NAME:
                return value == null ? null : ((Email) value).getDomain();
              case LINUX_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.linux.User.Name.valueOf(getUsernameForEmail((Email) value));
              default:
                // fall-through to throw exception
            }
            break;
          }
        case FKEY:
          {
            switch (castToType.getId()) {
              case INT:
              case PKEY:
                return value;
              default:
                // fall-through to throw exception
            }
            break;
          }
        case FLOAT:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Float) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays(((Float) value).longValue()));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((int) ((Float) value * 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((int) ((Float) value * 1000));
              case DOUBLE:
                return value == null ? null : Double.valueOf(((Float) value).doubleValue());
              case INT:
                return value == null ? null : Integer.valueOf(((Float) value).intValue());
              case INTERVAL:
                return value == null ? null : Long.valueOf(((Float) value).longValue());
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(((Float) value).longValue());
              case SHORT:
                return value == null ? null : Short.valueOf(((Float) value).shortValue());
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf(((Float) value).doubleValue());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case HOSTNAME:
          {
            switch (castToType.getId()) {
              case IP_ADDRESS:
                return value == null ? null : ((HostAddress) value).getInetAddress();
              case ZONE:
                return value == null ? null : getZoneForDomainName(conn, ((HostAddress) value).getDomainName());
              case DOMAIN_NAME:
                return value == null ? null : ((HostAddress) value).getDomainName();
              default:
                // fall-through to throw exception
            }
            break;
          }
        case INT:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Integer) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays((Integer) value));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((Integer) value * 100);
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((Integer) value * 1000);
              case DOUBLE:
                return value == null ? null : Double.valueOf(((Integer) value).doubleValue());
              case FKEY:
                return value;
              case FLOAT:
                return value == null ? null : Float.valueOf(((Integer) value).floatValue());
              case INTERVAL:
                return value == null ? null : Long.valueOf(((Integer) value).longValue());
              case IP_ADDRESS:
                return value == null ? null : InetAddress.valueOf(IpAddress.getIpAddressForInt((Integer) value));
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(((Integer) value).longValue());
              case PKEY:
                return value;
              case SHORT:
                return value == null ? null : Short.valueOf(((Integer) value).shortValue());
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf(((Integer) value).doubleValue());
              case LINUX_ID:
                return value == null ? null : LinuxId.valueOf((Integer) value);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case INTERVAL:
          {
            switch (castToType.getId()) {
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((int) ((Long) value * 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((int) ((Long) value * 1000));
              case DOUBLE:
                return value == null ? null : Double.valueOf(((Long) value).doubleValue());
              case FLOAT:
                return value == null ? null : Float.valueOf(((Long) value).floatValue());
              case INT:
                return value == null ? null : Integer.valueOf(((Long) value).intValue());
              case LONG:
              case OCTAL_LONG:
                return value;
              case SHORT:
                return value == null ? null : Short.valueOf(((Long) value).shortValue());
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf((Long) value);
              // TODO: Should casts to/from Interval add decimal places, since interval represents milliseconds
              //       Interval of (long)234 would become (decimal_2)23, (decimal_3)234, BigDecimal(0.234), ...
              default:
                // fall-through to throw exception
            }
            break;
          }
        case IP_ADDRESS:
          {
            // No special casts
            break;
          }
        case LONG:
        case OCTAL_LONG:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Long) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays((Long) value));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((int) ((Long) value * 100));
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((int) ((Long) value * 1000));
              case DOUBLE:
                return value == null ? null : Double.valueOf(((Long) value).doubleValue());
              case FLOAT:
                return value == null ? null : Float.valueOf(((Long) value).floatValue());
              case INT:
                return value == null ? null : Integer.valueOf(((Long) value).intValue());
              case INTERVAL:
                return value;
              case LONG:
              case OCTAL_LONG:
                return value;
              case SHORT:
                return value == null ? null : Short.valueOf(((Long) value).shortValue());
              case TIME:
                return value == null ? null : new UnmodifiableTimestamp((Long) value);
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf((Long) value);
              case SMALL_IDENTIFIER:
                return value == null ? null : new SmallIdentifier((Long) value);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case PKEY:
          {
            switch (castToType.getId()) {
              case FKEY:
              case INT:
                return value;
              default:
                // fall-through to throw exception
            }
            break;
          }
        case PATH:
          {
            // No special casts
            break;
          }
        case PHONE:
          {
            switch (castToType.getId()) {
              case URL:
                return value == null ? null : "tel:" + ((String) value).replace(' ', '-');
              default:
                // fall-through to throw exception
            }
            break;
          }
        case SHORT:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : (Short) value != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays((Short) value));
              case DECIMAL_2:
                return value == null ? null : Integer.valueOf((Short) value * 100);
              case DECIMAL_3:
                return value == null ? null : Integer.valueOf((Short) value * 1000);
              case DOUBLE:
                return value == null ? null : Double.valueOf(((Short) value).doubleValue());
              case FLOAT:
                return value == null ? null : Float.valueOf(((Short) value).floatValue());
              case INT:
                return value == null ? null : Integer.valueOf(((Short) value).intValue());
              case INTERVAL:
                return value == null ? null : Long.valueOf(((Short) value).longValue());
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(((Short) value).longValue());
              case BIG_DECIMAL:
                return value == null ? null : BigDecimal.valueOf(((Short) value).longValue());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case STRING:
          return castToType.parseString((String) value);
        case TIME:
          {
            long lvalue = value == null ? 0 : ((Timestamp) value).getTime();
            switch (castToType.getId()) {
              case DATE:
                return value == null ? null : new java.sql.Date(roundToDate(lvalue));
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : Long.valueOf(lvalue);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case URL:
          {
            // TODO: URL as URI (no resolve stuff)?
            switch (castToType.getId()) {
              case HOSTNAME:
                return value == null ? null : HostAddress.valueOf(new URL((String) value).getHost());
              case PATH:
                return value == null ? null : PosixPath.valueOf(new URL((String) value).getPath());
              case ZONE:
                return value == null ? null : getZoneForDomainName(conn, DomainName.valueOf(new URL((String) value).getHost()));
              case DOMAIN_NAME:
                return value == null ? null : DomainName.valueOf(new URL((String) value).getHost());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case USERNAME:
          {
            switch (castToType.getId()) {
              case MYSQL_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.mysql.User.Name.valueOf(((com.aoindustries.aoserv.client.account.User.Name) value).toString());
              case POSTGRES_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(((com.aoindustries.aoserv.client.account.User.Name) value).toString());
              case LINUX_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.linux.User.Name.valueOf(((com.aoindustries.aoserv.client.account.User.Name) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case ZONE:
          {
            // TODO: com.aoapps.net.DomainName (once no longer ends with ".")
            switch (castToType.getId()) {
              case HOSTNAME:
                {
                  String hname = (String) value;
                  while (hname.endsWith(".")) {
                    hname = hname.substring(0, hname.length() - 1);
                  }
                  return HostAddress.valueOf(hname);
                }
              case DOMAIN_NAME:
                return value == null ? null : getDomainNameForZone((String) value);
              default:
                // fall-through to throw exception
            }
            break;
          }
        case BIG_DECIMAL:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : ((BigDecimal) value).compareTo(BigDecimal.ZERO) != 0;
              case DATE:
                return value == null ? null : new java.sql.Date(getMillisFromDays(((BigDecimal) value).longValue()));
              case DECIMAL_2:
                return value == null ? null : ((BigDecimal) value).scaleByPowerOfTen(2).intValue();
              case DECIMAL_3:
                return value == null ? null : ((BigDecimal) value).scaleByPowerOfTen(3).intValue();
              case DOUBLE:
                return value == null ? null : ((BigDecimal) value).doubleValue();
              case FLOAT:
                return value == null ? null : ((BigDecimal) value).floatValue();
              case INT:
                return value == null ? null : ((BigDecimal) value).intValue();
              case INTERVAL:
                return value == null ? null : ((BigDecimal) value).longValue();
              case LONG:
              case OCTAL_LONG:
                return value == null ? null : ((BigDecimal) value).longValue();
              case SHORT:
                return value == null ? null : ((BigDecimal) value).shortValue();
              case SMALL_IDENTIFIER:
                return value == null ? null : new SmallIdentifier(((BigDecimal) value).longValue());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DOMAIN_LABEL:
          {
            switch (castToType.getId()) {
              case DOMAIN_LABELS:
                return value == null ? null : DomainLabels.valueOf(((DomainLabel) value).toString());
              case DOMAIN_NAME:
                return value == null ? null : DomainName.valueOf(((DomainLabel) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DOMAIN_LABELS:
          {
            switch (castToType.getId()) {
              case DOMAIN_NAME:
                return DomainName.valueOf(((DomainLabels) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case DOMAIN_NAME:
          {
            switch (castToType.getId()) {
              case HOSTNAME:
                return HostAddress.valueOf((DomainName) value);
              case ZONE:
                return ((DomainName) value).toString() + '.';
              case DOMAIN_LABELS:
                return DomainLabels.valueOf(((DomainName) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case GECOS:
          {
            // No special casts
            break;
          }
        case GROUP_ID:
          {
            // No special casts
            break;
          }
        case HASHED_PASSWORD:
          {
            // No special casts
            break;
          }
        case LINUX_ID:
          {
            switch (castToType.getId()) {
              case BOOLEAN:
                return value == null ? null : ((LinuxId) value).getId() != 0;
              case INT:
                return value == null ? null : Integer.valueOf(((LinuxId) value).getId());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case MAC_ADDRESS:
          {
            // No special casts
            break;
          }
        case MONEY:
          {
            switch (castToType.getId()) {
              case BIG_DECIMAL:
                return value == null ? null : ((Money) value).getValue();
              default:
                // fall-through to throw exception
            }
            break;
          }
        case MYSQL_DATABASE_NAME:
          {
            // No special casts
            break;
          }
        case MYSQL_SERVER_NAME:
          {
            // No special casts
            break;
          }
        case MYSQL_TABLE_NAME:
          {
            // No special casts
            break;
          }
        case MYSQL_USERNAME:
          {
            switch (castToType.getId()) {
              case USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.account.User.Name.valueOf(((com.aoindustries.aoserv.client.mysql.User.Name) value).toString());
              case LINUX_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.linux.User.Name.valueOf(((com.aoindustries.aoserv.client.mysql.User.Name) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case NET_PORT:
          {
            switch (castToType.getId()) {
              case INT:
                return value == null ? null : Integer.valueOf(((Port) value).getPort());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case POSTGRES_DATABASE_NAME:
          {
            // No special casts
            break;
          }
        case POSTGRES_SERVER_NAME:
          {
            // No special casts
            break;
          }
        case POSTGRES_USERNAME:
          {
            switch (castToType.getId()) {
              case USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.account.User.Name.valueOf(((com.aoindustries.aoserv.client.postgresql.User.Name) value).toString());
              case LINUX_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.linux.User.Name.valueOf(((com.aoindustries.aoserv.client.postgresql.User.Name) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case FIREWALLD_ZONE_NAME:
          {
            // No special casts
            break;
          }
        case LINUX_USERNAME:
          {
            switch (castToType.getId()) {
              case USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.account.User.Name.valueOf(((com.aoindustries.aoserv.client.linux.User.Name) value).toString());
              case MYSQL_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.mysql.User.Name.valueOf(((com.aoindustries.aoserv.client.linux.User.Name) value).toString());
              case POSTGRES_USERNAME:
                return value == null ? null : com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(((com.aoindustries.aoserv.client.linux.User.Name) value).toString());
              default:
                // fall-through to throw exception
            }
            break;
          }
        case IDENTIFIER:
          {
            switch (castToType.getId()) {
              case BIG_DECIMAL:
                if (value == null) {
                  return null;
                }
                Identifier identifier = (Identifier) value;
                String hexHi = Long.toHexString(identifier.getHi());
                String hexLo = Long.toHexString(identifier.getLo());
                int combinedLen = hexHi.length() + 32;
                StringBuilder combined = new StringBuilder(combinedLen);
                combined.append(hexHi);
                while (combined.length() < (combinedLen - hexLo.length())) {
                  combined.append('0');
                }
                combined.append(hexLo);
                assert combined.length() == combinedLen;
                return new BigDecimal(new BigInteger(combined.toString(), 16));
              default:
                // fall-through to throw exception
            }
            break;
          }
        case SMALL_IDENTIFIER:
          {
            switch (castToType.getId()) {
              case LONG:
              case OCTAL_LONG:
                return (value == null) ? null : ((SmallIdentifier) value).getValue();
              case BIG_DECIMAL:
                return (value == null) ? null : new BigDecimal(new BigInteger(Long.toHexString(((SmallIdentifier) value).getValue()), 16));
              default:
                // fall-through to throw exception
            }
            break;
          }
        case HASHED_KEY:
          {
            // No special casts
            break;
          }
        default:
          // fall-through to throw exception
      }
      throw new IllegalArgumentException("Unable to cast from " + name + " to " + castToType.getName());
    } catch (ValidationException e) {
      throw new IllegalArgumentException(e.getLocalizedMessage(), e);
    }
  }

  /**
   * The number of milliseconds in a day.
   */
  public static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

  /**
   * Gets the number of days from epoch in GMT.
   */
  public static long getDaysFromMillis(long time) {
    long days = time / MILLIS_PER_DAY;
    long remainder = time % MILLIS_PER_DAY;
    return (remainder < 0) ? (days - 1) : days;
  }

  /**
   * Gets the number of milliseconds from epoch in GMT.
   */
  public static long getMillisFromDays(long days) {
    return days * MILLIS_PER_DAY;
  }

  /**
   * Rounds the time to an exact day in GMT.
   */
  public static long roundToDate(long time) {
    return getMillisFromDays(getDaysFromMillis(time));
  }

  private static DomainName getDomainNameForZone(String zone) throws ValidationException {
    while (zone.endsWith(".")) {
      zone = zone.substring(0, zone.length() - 1);
    }
    return DomainName.valueOf(zone);
  }

  /**
   * Gets the username for an email address, which is the local part, up to the first '+' address (at position >= 1) (skipping any plus addressing).
   */
  private static String getUsernameForEmail(Email email) {
    String localPart = email.getLocalPart();
    int plusPos = localPart.indexOf('+', 1);
    if (plusPos == -1) {
      return localPart;
    }
    return localPart.substring(0, plusPos);
  }

  private static String getZoneForDomainName(AoservConnector conn, DomainName domainName) throws IOException, IllegalArgumentException, SQLException {
    if (domainName == null) {
      return null;
    }
    return conn.getDns().getZone().getHostTld(domainName) + ".";
  }

  public int compareTo(Object value1, Object value2) throws IllegalArgumentException, SQLException, UnknownHostException {
    return compareTo(value1, value2, pkey);
  }

  /**
   * Compares two values lexicographically.  The values must be of the same type.
   *
   * @param  value1  the first value being compared
   * @param  value2  the second value being compared
   * @param  id  the data type
   *
   * @return  the value <code>0</code> if the two values are equal;
   *          a value less than <code>0</code> if the first value
   *          is lexicographically less than the second value; and a
   *          value greater than <code>0</code> if the first value is
   *          lexicographically greater than the second value.
   *
   * @exception  IllegalArgumentException  if the type is invalid
   */
  public static int compareTo(Object value1, Object value2, int id) throws IllegalArgumentException {
    if (value1 == null) {
      return value2 == null ? 0 : -1;
    } else {
      if (value2 == null) {
        return 1;
      }
      switch (id) {
        case ACCOUNTING:
          return ((Account.Name) value1).compareTo((Account.Name) value2);
        case PHONE:
        case STRING:
        case URL:
          return Strings.compareToIgnoreCaseCarefulEquals((String) value1, (String) value2);
        case USERNAME:
          return ((com.aoindustries.aoserv.client.account.User.Name) value1).compareTo((com.aoindustries.aoserv.client.account.User.Name) value2);
        case PATH:
          return ((PosixPath) value1).compareTo((PosixPath) value2);
        case BOOLEAN:
          return ((Boolean) value1).compareTo((Boolean) value2);
        case DATE:
          return Long.compare(
              getDaysFromMillis(((java.sql.Date) value1).getTime()),
              getDaysFromMillis(((java.sql.Date) value2).getTime())
          );
        case DECIMAL_2:
        case DECIMAL_3:
        case FKEY:
        case INT:
        case PKEY:
          return ((Integer) value1).compareTo((Integer) value2);
        case DOUBLE:
          return ((Double) value1).compareTo((Double) value2);
        case EMAIL:
          return ((Email) value1).compareTo((Email) value2);
        case FLOAT:
          return ((Float) value1).compareTo((Float) value2);
        case HOSTNAME:
          return ((HostAddress) value1).compareTo((HostAddress) value2);
        case ZONE:
          // TODO: com.aoapps.net.DomainName (once no longer ends with ".")
          return DomainName.compareLabels((String) value1, (String) value2);
        case INTERVAL:
        case LONG:
        case OCTAL_LONG:
          return ((Long) value1).compareTo((Long) value2);
        case IP_ADDRESS:
          return ((InetAddress) value1).compareTo((InetAddress) value2);
        case SHORT:
          return ((Short) value1).compareTo((Short) value2);
        case TIME:
          return ((Timestamp) value1).compareTo((Timestamp) value2);
        case BIG_DECIMAL:
          return ((BigDecimal) value1).compareTo((BigDecimal) value2);
        case DOMAIN_LABEL:
          return ((DomainLabel) value1).compareTo((DomainLabel) value2);
        case DOMAIN_LABELS:
          return ((DomainLabels) value1).compareTo((DomainLabels) value2);
        case DOMAIN_NAME:
          return ((DomainName) value1).compareTo((DomainName) value2);
        case GECOS:
          return ((Gecos) value1).compareTo((Gecos) value2);
        case GROUP_ID:
          return ((com.aoindustries.aoserv.client.linux.Group.Name) value1).compareTo((com.aoindustries.aoserv.client.linux.Group.Name) value2);
        case HASHED_PASSWORD:
          return ((HashedPassword) value1).toString().compareTo(((HashedPassword) value2).toString());
        case LINUX_ID:
          return ((LinuxId) value1).compareTo((LinuxId) value2);
        case MAC_ADDRESS:
          return ((MacAddress) value1).compareTo((MacAddress) value2);
        case MONEY:
          return ((Money) value1).compareTo((Money) value2);
        case MYSQL_DATABASE_NAME:
          return ((com.aoindustries.aoserv.client.mysql.Database.Name) value1).compareTo((com.aoindustries.aoserv.client.mysql.Database.Name) value2);
        case MYSQL_SERVER_NAME:
          return ((com.aoindustries.aoserv.client.mysql.Server.Name) value1).compareTo((com.aoindustries.aoserv.client.mysql.Server.Name) value2);
        case MYSQL_TABLE_NAME:
          return ((com.aoindustries.aoserv.client.mysql.TableName) value1).compareTo((com.aoindustries.aoserv.client.mysql.TableName) value2);
        case MYSQL_USERNAME:
          return ((com.aoindustries.aoserv.client.mysql.User.Name) value1).compareTo((com.aoindustries.aoserv.client.mysql.User.Name) value2);
        case NET_PORT:
          return ((Port) value1).compareTo((Port) value2);
        case POSTGRES_DATABASE_NAME:
          return ((com.aoindustries.aoserv.client.postgresql.Database.Name) value1).compareTo((com.aoindustries.aoserv.client.postgresql.Database.Name) value2);
        case POSTGRES_SERVER_NAME:
          return ((com.aoindustries.aoserv.client.postgresql.Server.Name) value1).compareTo((com.aoindustries.aoserv.client.postgresql.Server.Name) value2);
        case POSTGRES_USERNAME:
          return ((com.aoindustries.aoserv.client.postgresql.User.Name) value1).compareTo((com.aoindustries.aoserv.client.postgresql.User.Name) value2);
        case FIREWALLD_ZONE_NAME:
          return ((FirewallZone.Name) value1).compareTo((FirewallZone.Name) value2);
        case LINUX_USERNAME:
          return ((com.aoindustries.aoserv.client.linux.User.Name) value1).compareTo((com.aoindustries.aoserv.client.linux.User.Name) value2);
        case IDENTIFIER:
          return ((Identifier) value1).compareTo((Identifier) value2);
        case SMALL_IDENTIFIER:
          return ((SmallIdentifier) value1).compareTo((SmallIdentifier) value2);
        case HASHED_KEY:
          return ((HashedKey) value1).compareTo((HashedKey) value2);
        default:
          throw new IllegalArgumentException("Unknown type: " + id);
      }
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case 0:
        return pkey;
      case COLUMN_NAME:
        return name;
      case 2:
        return sinceVersion;
      case 3:
        return lastVersion;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getId() {
    return pkey;
  }

  public String getName() {
    return name;
  }

  public String getSinceVersion_version() {
    return sinceVersion;
  }

  public AoservProtocol getSinceVersion(AoservConnector connector) throws SQLException, IOException {
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(sinceVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + sinceVersion);
    }
    return obj;
  }

  public String getLastVersion_version() {
    return lastVersion;
  }

  public AoservProtocol getLastVersion(AoservConnector connector) throws SQLException, IOException {
    if (lastVersion == null) {
      return null;
    }
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(lastVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + lastVersion);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SCHEMA_TYPES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey         = result.getInt(pos++);
    name         = result.getString(pos++);
    sinceVersion = result.getString(pos++);
    lastVersion  = result.getString(pos++);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey         = in.readCompressedInt();
    name         = in.readUTF().intern();
    sinceVersion = in.readUTF().intern();
    lastVersion  = InternUtils.intern(in.readNullUTF());
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeUTF(name);
      out.writeCompressedInt(pkey);
    } else {
      out.writeCompressedInt(pkey);
      out.writeUTF(name);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_69) >= 0) {
      out.writeUTF(sinceVersion);
      out.writeNullUTF(lastVersion);
    }
  }

  @Override
  public String toStringImpl() {
    return name;
  }

  /**
   * Checks if this type supports any precisions other than default {@code -1}.
   * This can be used to short-cut somethings when it is known no precisions will be returned.
   */
  public boolean supportsPrecision() {
    return supportsPrecision(pkey);
  }

  /**
   * Checks if the given type supports any precisions other than default {@code -1}.
   * This can be used to short-cut somethings when it is known no precisions will be returned.
   */
  public static boolean supportsPrecision(int type) {
    return type == TIME;
  }

  /**
   * Gets the maximum precision for this type or {@code -1} when unbounded.
   * This can be used to stop searching for largest precision once the maximum possible value has been found.
   */
  public int getMaxPrecision() {
    return getMaxPrecision(pkey);
  }

  /**
   * Gets the maximum precision for the given type or {@code -1} when unbounded.
   * This can be used to stop searching for largest precision once the maximum possible value has been found.
   */
  // Return Integer with null when unbounded
  public static int getMaxPrecision(int type) {
    switch (type) {
      case TIME:
        return 29;
      default:
        return -1;
    }
  }

  public int getPrecision(Object value) {
    return getPrecision(value, pkey);
  }

  public static int getPrecision(Object value, int type) {
    if (value == null) {
      return -1;
    }
    switch (type) {
      case TIME:
        {
          // Precision matches definition for Timestamp: https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html
          int nanos = ((Timestamp) value).getNanos();
          if (nanos == 0) {
            return 19;
          } else if ((nanos % 1000000) == 0) {
            return 23;
          } else if ((nanos % 1000) == 0) {
            return 26;
          } else {
            return 29;
          }
        }
      default:
        return -1;
    }
  }

  public String getString(Object value, int precision) {
    return getString(value, precision, pkey);
  }

  public static String getString(Object value, int precision, int type) throws IllegalArgumentException {
    if (value == null) {
      return null;
    }
    switch (type) {
      case ACCOUNTING:
        return ((Account.Name) value).toString();
      case BOOLEAN:
        return ((Boolean) value).toString();
      case DATE:
        return SQLUtility.formatDate((java.sql.Date) value, DATE_TIME_ZONE);
      case DECIMAL_2:
        return SQLUtility.formatDecimal2(((Integer) value));
      case DECIMAL_3:
        return SQLUtility.formatDecimal3(((Integer) value));
      case DOUBLE:
        return ((Double) value).toString();
      case EMAIL:
        return ((Email) value).toString();
      case FKEY:
        return ((Integer) value).toString();
      case FLOAT:
        return ((Float) value).toString();
      case HOSTNAME:
        return ((HostAddress) value).toString();
      case INT:
        return ((Integer) value).toString();
      case INTERVAL:
        return Strings.getDecimalTimeLengthString(((Long) value));
      case IP_ADDRESS:
        return ((InetAddress) value).toString();
      case LONG:
        return ((Long) value).toString();
      case OCTAL_LONG:
        return Long.toOctalString(((Long) value));
      case PATH:
        return ((PosixPath) value).toString();
      case PHONE:
        return (String) value;
      case PKEY:
        return ((Integer) value).toString();
      case SHORT:
        return ((Short) value).toString();
      case STRING:
        return (String) value;
      case TIME:
        {
          Timestamp ts = (Timestamp) value;
          String seconds = SQLUtility.formatDateTime(ts); // TODO: Make a formatDateTimeNanos?
          int nanos = ts.getNanos();
          if (
              precision == 19
                  || (precision == -1 && nanos == 0)
          ) {
            return seconds;
          }
          String end;
          int endDigits;
          if (
              precision == 23
                  || (precision == -1 && (nanos % 1000000) == 0)
          ) {
            end = Integer.toString(nanos / 1000000);
            endDigits = 3;
          } else if (
              precision == 26
                  || (precision == -1 && (nanos % 1000) == 0)
          ) {
            end = Integer.toString(nanos / 1000);
            endDigits = 6;
          } else if (precision == -1 || precision == 29) {
            end = Integer.toString(nanos);
            endDigits = 9;
          } else {
            throw new IllegalArgumentException("Expected precision in (-1, 19, 23, 26, 29), got: " + precision);
          }
          int slen = seconds.length();
          StringBuilder formatted = new StringBuilder(slen + 1 + endDigits);
          formatted.append(seconds).append('.');
          while (formatted.length() + end.length() < slen + 1 + endDigits) {
            formatted.append('0');
          }
          formatted.append(end);
          assert formatted.length() == slen + 1 + endDigits;
          return formatted.toString();
        }
      case URL:
        return (String) value;
      case USERNAME:
        return ((com.aoindustries.aoserv.client.account.User.Name) value).toString();
      case ZONE:
        return (String) value; // TODO: com.aoapps.net.DomainName (once no longer ends with ".")
      case BIG_DECIMAL:
        return ((BigDecimal) value).toString();
      case DOMAIN_LABEL:
        return ((DomainLabel) value).toString();
      case DOMAIN_LABELS:
        return ((DomainLabels) value).toString();
      case DOMAIN_NAME:
        return ((DomainName) value).toString();
      case GECOS:
        return ((Gecos) value).toString();
      case GROUP_ID:
        return ((com.aoindustries.aoserv.client.linux.Group.Name) value).toString();
      case HASHED_PASSWORD:
        return ((HashedPassword) value).toString();
      case LINUX_ID:
        return ((LinuxId) value).toString();
      case MAC_ADDRESS:
        return ((MacAddress) value).toString();
      case MONEY:
        return ((Money) value).toString();
      case MYSQL_DATABASE_NAME:
        return ((com.aoindustries.aoserv.client.mysql.Database.Name) value).toString();
      case MYSQL_SERVER_NAME:
        return ((com.aoindustries.aoserv.client.mysql.Server.Name) value).toString();
      case MYSQL_TABLE_NAME:
        return ((com.aoindustries.aoserv.client.mysql.TableName) value).toString();
      case MYSQL_USERNAME:
        return ((com.aoindustries.aoserv.client.mysql.User.Name) value).toString();
      case NET_PORT:
        return ((Port) value).toString();
      case POSTGRES_DATABASE_NAME:
        return ((com.aoindustries.aoserv.client.postgresql.Database.Name) value).toString();
      case POSTGRES_SERVER_NAME:
        return ((com.aoindustries.aoserv.client.postgresql.Server.Name) value).toString();
      case POSTGRES_USERNAME:
        return ((com.aoindustries.aoserv.client.postgresql.User.Name) value).toString();
      case FIREWALLD_ZONE_NAME:
        return ((FirewallZone.Name) value).toString();
      case LINUX_USERNAME:
        return ((com.aoindustries.aoserv.client.linux.User.Name) value).toString();
      case IDENTIFIER:
        return ((Identifier) value).toString();
      case SMALL_IDENTIFIER:
        return ((SmallIdentifier) value).toString();
      case HASHED_KEY:
        return ((HashedKey) value).toString();
      default:
        throw new IllegalArgumentException("Unknown SchemaType: " + type);
    }
  }

  public Object parseString(String s) throws IllegalArgumentException {
    return parseString(s, pkey);
  }

  public static Object parseString(String s, int id) throws IllegalArgumentException {
    try {
      if (s == null) {
        return null;
      }
      switch (id) {
        case ACCOUNTING:
          return Account.Name.valueOf(s);
        case EMAIL:
          return Email.valueOf(s);
        case PHONE:
        case STRING:
        case URL:
        case ZONE: // TODO: com.aoapps.net.DomainName (once no longer ends with ".")
          return s;
        case USERNAME:
          return com.aoindustries.aoserv.client.account.User.Name.valueOf(s);
        case PATH:
          return PosixPath.valueOf(s);
        case HOSTNAME:
          return HostAddress.valueOf(s);
        case IP_ADDRESS:
          return InetAddress.valueOf(s);
        case BOOLEAN:
          if (
              "y".equalsIgnoreCase(s)
                  || "yes".equalsIgnoreCase(s)
                  || "t".equalsIgnoreCase(s)
                  || "true".equalsIgnoreCase(s)
          ) {
            return Boolean.TRUE;
          }
          if (
              "n".equalsIgnoreCase(s)
                  || "no".equalsIgnoreCase(s)
                  || "f".equalsIgnoreCase(s)
                  || "false".equalsIgnoreCase(s)
          ) {
            return Boolean.FALSE;
          }
          throw new IllegalArgumentException("Unable to parse boolean: " + s);
        case DATE:
          return SQLUtility.parseDate(s, DATE_TIME_ZONE);
        case DECIMAL_2:
          return SQLUtility.parseDecimal2(s);
        case DECIMAL_3:
          return SQLUtility.parseDecimal3(s);
        case DOUBLE:
          return Double.parseDouble(s);
        case FKEY:
        case INT:
        case PKEY:
          return Integer.parseInt(s);
        case FLOAT:
          return Float.valueOf(s);
        case INTERVAL:
          throw new UnsupportedOperationException("Interval parsing not yet supported");
        case LONG:
          return Long.valueOf(s);
        case OCTAL_LONG:
          return Long.parseLong(s, 8);
        case SHORT:
          return Short.valueOf(s);
        case TIME:
          return SQLUtility.parseDateTime(s);
        case BIG_DECIMAL:
          return new BigDecimal(s);
        case DOMAIN_LABEL:
          return DomainLabel.valueOf(s);
        case DOMAIN_LABELS:
          return DomainLabels.valueOf(s);
        case DOMAIN_NAME:
          return DomainName.valueOf(s);
        case GECOS:
          return Gecos.valueOf(s);
        case GROUP_ID:
          return com.aoindustries.aoserv.client.linux.Group.Name.valueOf(s);
        case HASHED_PASSWORD:
          return HashedPassword.valueOf(s);
        case LINUX_ID:
          return LinuxId.valueOf(Integer.parseInt(s));
        case MAC_ADDRESS:
          return MacAddress.valueOf(s);
        case MONEY:
          throw new IllegalArgumentException("Parsing from String to Money is not supported.");
        case MYSQL_DATABASE_NAME:
          return com.aoindustries.aoserv.client.mysql.Database.Name.valueOf(s);
        case MYSQL_SERVER_NAME:
          return com.aoindustries.aoserv.client.mysql.Server.Name.valueOf(s);
        case MYSQL_TABLE_NAME:
          return com.aoindustries.aoserv.client.mysql.TableName.valueOf(s);
        case MYSQL_USERNAME:
          return com.aoindustries.aoserv.client.mysql.User.Name.valueOf(s);
        case NET_PORT:
          {
            int slashPos = s.indexOf('/');
            if (slashPos == -1) {
              throw new IllegalArgumentException("Slash (/) not found for Port: " + s);
            }
            return Port.valueOf(
                Integer.parseInt(s.substring(0, slashPos)),
                com.aoapps.net.Protocol.valueOf(s.substring(slashPos + 1).toUpperCase(Locale.ROOT))
            );
          }
        case POSTGRES_DATABASE_NAME:
          return com.aoindustries.aoserv.client.postgresql.Database.Name.valueOf(s);
        case POSTGRES_SERVER_NAME:
          return com.aoindustries.aoserv.client.postgresql.Server.Name.valueOf(s);
        case POSTGRES_USERNAME:
          return com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(s);
        case FIREWALLD_ZONE_NAME:
          return FirewallZone.Name.valueOf(s);
        case LINUX_USERNAME:
          return com.aoindustries.aoserv.client.linux.User.Name.valueOf(s);
        case IDENTIFIER:
          return new Identifier(s);
        case SMALL_IDENTIFIER:
          return new Identifier(s);
        case HASHED_KEY:
          return HashedKey.valueOf(s);
        default:
          throw new IllegalArgumentException("Unknown SchemaType: " + id);
      }
    } catch (ValidationException e) {
      throw new IllegalArgumentException(e.getLocalizedMessage(), e);
    }
  }
}
