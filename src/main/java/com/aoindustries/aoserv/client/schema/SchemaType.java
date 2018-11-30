/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.net.IPAddress;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.FirewalldZoneName;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.LinuxId;
import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLTableName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainLabel;
import com.aoindustries.net.DomainLabels;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.MacAddress;
import com.aoindustries.net.Port;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * A <code>SchemaType</code> is a unique data type used in
 * <code>SchemaColumn</code>s.
 *
 * @see  SchemaColumn#getSchemaType(AOServConnector)
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaType extends GlobalObjectIntegerKey<SchemaType> {

	static final int COLUMN_NAME = 1;
	public static final String DATE_name = "date";

	/**
	 * The different types of values.
	 */
	// TODO: No longer pass type ids, instead pass per-protocol type names, and use an enum here
	public static final int
		ACCOUNTING             =  0, // com.aoindustries.aoserv.client.validator.AccountingCode
		BOOLEAN                =  1, // boolean/java.lang.Boolean
		//BYTE                 =  2, // byte/java.lang.Byte
		//CITY                 =  3, // java.lang.String
		//COUNTRY              =  4, // java.lang.String
		DATE                   =  5, // java.sql.Date
		DECIMAL_2              =  6, // java.math.BigDecimal
		DECIMAL_3              =  7, // java.math.BigDecimal
		DOUBLE                 =  8, // double/java.lang.Double
		EMAIL                  =  9, // com.aoindustries.net.Email
		FKEY                   = 10, // int/java.lang.Integer
		FLOAT                  = 11, // float/java.lang.Float
		HOSTNAME               = 12, // com.aoindustries.net.HostAddress
		INT                    = 13, // int/java.lang.Integer
		INTERVAL               = 14, // long/java.lang.Long
		IP_ADDRESS             = 15, // com.aoindustries.net.InetAddress
		LONG                   = 16, // long/java.lang.Long
		//OCTAL_INT            = 17, // int/java.lang.Integer
		OCTAL_LONG             = 18, // long/java.lang.Long
		//PACKAGE              = 19, // com.aoindustries.aoserv.client.validator.AccountingCode
		PKEY                   = 20, // int/java.lang.Integer
		PATH                   = 21, // com.aoindustries.aoserv.client.validator.UnixPath
		PHONE                  = 22, // java.lang.String
		SHORT                  = 23, // short/java.lang.Short
		//STATE                = 24, // java.lang.String
		STRING                 = 25, // java.lang.String
		TIME                   = 26, // java.sql.Timestamp
		URL                    = 27, // java.net.URL
		USERNAME               = 28, // com.aoindustries.aoserv.client.validator.UserId
		//ZIP                  = 29, // java.lang.String
		ZONE                   = 30, // com.aoindustries.aoserv.client.validator.Zone
		BIG_DECIMAL            = 31, // java.math.BigDecimal
		DOMAIN_LABEL           = 32, // com.aoindustries.net.DomainLabel
		DOMAIN_LABELS          = 33, // com.aoindustries.net.DomainLabels
		DOMAIN_NAME            = 34, // com.aoindustries.net.DomainName
		GECOS                  = 35, // com.aoindustries.aoserv.client.validator.Gecos
		GROUP_ID               = 36, // com.aoindustries.aoserv.client.validator.GroupId
		HASHED_PASSWORD        = 37, // com.aoindustries.aoserv.client.validator.HashedPassword
		LINUX_ID               = 38, // com.aoindustries.aoserv.client.validator.LinuxId
		MAC_ADDRESS            = 39, // com.aoindustries.net.MacAddress
		MONEY                  = 40, // com.aoindustries.util.i18n.Money
		MYSQL_DATABASE_NAME    = 41, // com.aoindustries.aoserv.client.validator.MySQLDatabaseName
		MYSQL_SERVER_NAME      = 42, // com.aoindustries.aoserv.client.validator.MySQLServerName
		MYSQL_TABLE_NAME       = 43, // com.aoindustries.aoserv.client.validator.MySQLTableName
		MYSQL_USERNAME         = 44, // com.aoindustries.aoserv.client.validator.MySQLUserId
		NET_PORT               = 45, // com.aoindustries.net.NetPort
		POSTGRES_DATABASE_NAME = 46, // com.aoindustries.aoserv.client.validator.PostgresDatabaseName
		POSTGRES_SERVER_NAME   = 47, // com.aoindustries.aoserv.client.validator.PostgresServerName
		POSTGRES_USERNAME      = 48, // com.aoindustries.aoserv.client.validator.PostgresUserId
		FIREWALLD_ZONE_NAME    = 49  // com.aoindustries.aoserv.client.validator.FirewalldZoneName
	;

	private static final BigDecimal
		bigDecimalNegativeOne = BigDecimal.valueOf(-1),
		bigDecimal100 = BigDecimal.valueOf(100),
		bigDecimal1000 = BigDecimal.valueOf(1000)
	;

	private String name;
	private String sinceVersion;
	private String lastVersion;

	public boolean alignRight() {
		return alignRight(pkey);
	}

	public static boolean alignRight(int id) {
		switch(id) {
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
			case BIG_DECIMAL:
			case GROUP_ID:
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
	 * <pre>
	 *                                                                                                      P
	 *                                                                                                      O
	 *                                                                                                      S P
	 *                                                                                            M         T O   F
	 *                                                                                            Y         G S   I
	 *                                                                                            S M       R T P R
	 *                                                                                            Q Y M     E G O E
	 *                                                                                    H       L S Y     S R S W
	 *                                                                                    A       _ Q S M   _ E T A
	 *                                                                            D       S       D L Q Y   D S G L
	 *                                                                          D O       H       A _ L S   A _ R L
	 *                                                                        B O M D     E   M   T S _ Q   T S E D
	 *                        A                       I   O                   I M A O     D   A   A E T L   A E S _
	 *                        C     D D               P   C                   G A I M     _   C   B R A _   B R _ Z
	 *                        C     E E         H   I _   T               U   _ I N A   G P L _   A V B U N A V U O
	 *                      T O B   C C         O   N A   A               S   D N _ I   R A I A   S E L S E S E S N
	 *                      O U O   I I D       S   T D   L         S     E   E _ L N   O S N D   E R E E T E R E E
	 *                        N O   M M O E   F T   E D   _     P S T     R   C L A _ G U S U D M _ _ _ R _ _ _ R _
	 *                        T L D A A U M F L N   R R L L P P H H R T   N Z I A B N E P W X R O N N N N P N N N N
	 *                        I E A L L B A K O A I V E O O K A O O I I U A O M B E A C _ O _ E N A A A A O A A A A
	 *                        N A T _ _ L I E A M N A S N N E T N R N M R M N A E L M O I R I S E M M M M R M M M M
	 *               FROM     G N E 2 3 E L Y T E T L S G G Y H E T G E L E E L L S E S D D D S Y E E E E T E E E E
	 *
	 *             ACCOUNTING X                                     X
	 *                BOOLEAN   X   X X X     X   X     X X       X X         X
	 *                   DATE     X X X X     X   X     X X       X X X       X
	 *              DECIMAL_2   X   X X X     X   X X   X X       X X         X
	 *              DECIMAL_3   X   X X X     X   X X   X X       X X         X
	 *                 DOUBLE   X   X X X     X   X X   X X       X X         X
	 *                  EMAIL             X     X                   X   X X X       X
	 *                   FKEY               X     X         X       X
	 *                  FLOAT   X   X X X     X   X X   X X       X X         X
	 *               HOSTNAME                   X     X             X       X       X
	 *                    INT   X X X X X   X X   X X X X X X     X X         X             X
	 *               INTERVAL       X X X     X   X X   X X       X X         X
	 *             IP_ADDRESS                         X             X
	 *                   LONG   X X X X X     X   X X   X X       X X X       X
	 *             OCTAL_LONG   X X X X X     X   X X   X X       X X X       X
	 *                   PKEY               X     X         X       X
	 *                   PATH                                 X     X
	 *                  PHONE                                   X   X
	 *                  SHORT   X X X X X     X   X X   X X       X X         X
	 *                 STRING X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X   X X X X X X X X X
	 *                   TIME     X                     X X         X X
	 *                    URL                   X             X     X   X   X       X
	 *               USERNAME                                       X     X                             X       X
	 *                   ZONE                   X                   X       X       X
	 *            BIG_DECIMAL   X   X X X     X   X X   X X       X X         X
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
	 *         MYSQL_USERNAME                                       X     X                             X
	 *               NET_PORT                     X                 X                                     X
	 * POSTGRES_DATABASE_NAME                                       X                                       X
	 *   POSTGRES_SERVER_NAME                                       X                                         X
	 *      POSTGRES_USERNAME                                       X     X                                     X
	 *    FIREWALLD_ZONE_NAME                                       X                                             X
	 * </pre>
	 */
	public Object cast(AOServConnector conn, Object value, SchemaType castToType) throws IOException, SQLException {
		try {
			// When castToType is this type, just return value directly
			if(pkey == castToType.pkey) return value;
			// When casting to STRING, use regular string conversion method
			if(castToType.pkey == STRING) {
				return getString(value, pkey);
			}
			switch(pkey) {
				case ACCOUNTING:
					// No special casts
					break;
				case BOOLEAN:
					{
						boolean bvalue=value==null?false:((Boolean)value);
						switch(castToType.getId()) {
							case DECIMAL_2: return value==null?null:Integer.valueOf(bvalue?-100:0);
							case DECIMAL_3: return value==null?null:Integer.valueOf(bvalue?-1000:0);
							case DOUBLE: return value==null?null:new Double(bvalue?-1:0);
							case FLOAT: return value==null?null:Float.valueOf(bvalue?-1:0);
							case INT: return value==null?null:Integer.valueOf(bvalue?-1:0);
							case LONG: return value==null?null:Long.valueOf(bvalue?(long)-1:0);
							case OCTAL_LONG: return value==null?null:Long.valueOf(bvalue?(long)-1:0);
							case SHORT: return value==null?null:Short.valueOf(bvalue?(short)-1:0);
							case BIG_DECIMAL: return value==null?null:bvalue?bigDecimalNegativeOne:BigDecimal.ZERO;
						}
					}
					break;
				case DATE:
					{
						long tvalue=value==null?0:((java.sql.Date)value).getTime();
						switch(castToType.getId()) {
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)*1000));
							case DOUBLE: return value==null?null:new Double(SQLUtility.getDaysFromMillis(tvalue));
							case FLOAT: return value==null?null:Float.valueOf(SQLUtility.getDaysFromMillis(tvalue));
							case INT: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)));
							case LONG: return value==null?null:Long.valueOf(SQLUtility.getDaysFromMillis(tvalue));
							case OCTAL_LONG: return value==null?null:Long.valueOf(SQLUtility.getDaysFromMillis(tvalue));
							case SHORT: return value==null?null:Short.valueOf((short)(SQLUtility.getDaysFromMillis(tvalue)));
							case TIME: return value==null?null:new java.sql.Timestamp(SQLUtility.roundToDay(tvalue));
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(SQLUtility.getDaysFromMillis(tvalue));
						}
					}
					break;
				case DECIMAL_2:
					{
						int ivalue=value==null?0:((Integer)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:ivalue!=0;
							case DECIMAL_3: return value==null?null:Integer.valueOf(ivalue*10);
							case DOUBLE: return value==null?null:new Double((double)ivalue/100);
							case FLOAT: return value==null?null:Float.valueOf((float)ivalue/100);
							case INT: return value==null?null:Integer.valueOf(ivalue/100);
							case INTERVAL: return value==null?null:Long.valueOf(ivalue/100);
							case LONG: return value==null?null:Long.valueOf(ivalue/100);
							case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue/100);
							case SHORT: return value==null?null:Short.valueOf((short)(ivalue/100));
							case BIG_DECIMAL: return value==null?null:new BigDecimal(SQLUtility.getDecimal(ivalue));
						}
					}
					break;
				case DECIMAL_3:
					{
						int ivalue=value==null?0:((Integer)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:ivalue!=0;
							case DECIMAL_2: return value==null?null:Integer.valueOf(ivalue/10);
							case DOUBLE: return value==null?null:new Double((double)ivalue/1000);
							case FLOAT: return value==null?null:Float.valueOf((float)ivalue/1000);
							case INT: return value==null?null:Integer.valueOf(ivalue/1000);
							case INTERVAL: return value==null?null:Long.valueOf(ivalue/1000);
							case LONG: return value==null?null:Long.valueOf(ivalue/1000);
							case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue/1000);
							case SHORT: return value==null?null:Short.valueOf((short)(ivalue/1000));
							case BIG_DECIMAL: return value==null?null:new BigDecimal(SQLUtility.getDecimal(ivalue));
						}
					}
					break;
				case DOUBLE:
					{
						double dvalue=value==null?0:((Double)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:dvalue!=0;
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(dvalue*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(dvalue*1000));
							case FLOAT: return value==null?null:Float.valueOf((float)dvalue);
							case INT: return value==null?null:Integer.valueOf((int)dvalue);
							case INTERVAL: return value==null?null:Long.valueOf((long)dvalue);
							case LONG: return value==null?null:Long.valueOf((long)dvalue);
							case OCTAL_LONG: return value==null?null:Long.valueOf((long)dvalue);
							case SHORT: return value==null?null:Short.valueOf((short)dvalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(dvalue);
						}
					}
					break;
				case EMAIL:
					// TODO: com.aoindustries.net.Email
					switch(castToType.getId()) {
						case HOSTNAME:    return value==null ? null : HostAddress.valueOf(getDomainNameForEmail((String)value));
						case URL:         return value==null ? null : "mailto:"+((String)value);
						case USERNAME:    return value==null ? null : UserId.valueOf(getUsernameForEmail((String)value));
						case ZONE:        return value==null ? null : getZoneForDomainName(conn, getDomainNameForEmail((String)value));
						case DOMAIN_NAME: return value==null ? null : getDomainNameForEmail((String)value);
					}
					break;
				case FKEY:
					switch(castToType.getId()) {
						case INT: return value;
						case PKEY: return value;
					}
					break;
				case FLOAT:
					{
						float fvalue=value==null?0:((Float)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:fvalue!=0;
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(fvalue*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(fvalue*1000));
							case DOUBLE: return value==null?null:new Double(fvalue);
							case INT: return value==null?null:Integer.valueOf((int)fvalue);
							case INTERVAL: return value==null?null:Long.valueOf((long)fvalue);
							case LONG: return value==null?null:Long.valueOf((long)fvalue);
							case OCTAL_LONG: return value==null?null:Long.valueOf((long)fvalue);
							case SHORT: return value==null?null:Short.valueOf((short)fvalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(fvalue);
						}
					}
					break;
				case HOSTNAME:
					switch(castToType.getId()) {
						case IP_ADDRESS:  return value==null ? null : ((HostAddress)value).getInetAddress();
						case ZONE:        return value==null ? null : getZoneForDomainName(conn, ((HostAddress)value).getDomainName());
						case DOMAIN_NAME: return value==null ? null : ((HostAddress)value).getDomainName();
					}
					break;
				case INT:
					{
						int ivalue=value==null?0:((Integer)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:ivalue!=0;
							case DATE: return value==null?null:new java.sql.Date(SQLUtility.getMillisFromDays(ivalue));
							case DECIMAL_2: return value==null?null:Integer.valueOf(ivalue*100);
							case DECIMAL_3: return value==null?null:Integer.valueOf(ivalue*1000);
							case DOUBLE: return value==null?null:new Double(ivalue);
							case FKEY: return value;
							case FLOAT: return value==null?null:Float.valueOf((float)ivalue);
							case INTERVAL: return value==null?null:Long.valueOf(ivalue);
							case IP_ADDRESS: return value==null?null:InetAddress.valueOf(IPAddress.getIPAddressForInt(ivalue));
							case LONG: return value==null?null:Long.valueOf(ivalue);
							case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue);
							case PKEY: return value;
							case SHORT: return value==null?null:Short.valueOf((short)ivalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(ivalue);
							case LINUX_ID: return value==null?null:LinuxId.valueOf(ivalue);
						}
					}
					break;
				case INTERVAL:
					{
						long lvalue=value==null?0:((Long)value);
						switch(castToType.getId()) {
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
							case DOUBLE: return value==null?null:new Double((double)lvalue);
							case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
							case INT: return value==null?null:Integer.valueOf((int)lvalue);
							case LONG: return value;
							case OCTAL_LONG: return value;
							case SHORT: return value==null?null:Short.valueOf((short)lvalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
						}
					}
					break;
				case IP_ADDRESS:
					// No special casts
					break;
				case LONG:
					{
						long lvalue=value==null?0:((Long)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:lvalue!=0;
							case DATE: return value==null?null:new java.sql.Date(lvalue);
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
							case DOUBLE: return value==null?null:new Double((double)lvalue);
							case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
							case INT: return value==null?null:Integer.valueOf((int)lvalue);
							case INTERVAL: return value;
							case OCTAL_LONG: return value;
							case SHORT: return value==null?null:Short.valueOf((short)lvalue);
							case TIME: return value==null?null:new java.sql.Timestamp(lvalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
						}
					}
					break;
				case OCTAL_LONG:
					{
						long lvalue=value==null?0:((Long)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:lvalue!=0;
							case DATE: return value==null?null:new java.sql.Date(lvalue);
							case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
							case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
							case DOUBLE: return value==null?null:new Double((double)lvalue);
							case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
							case INT: return value==null?null:Integer.valueOf((int)lvalue);
							case INTERVAL: return value;
							case LONG: return value;
							case SHORT: return value==null?null:Short.valueOf((short)lvalue);
							case TIME: return value==null?null:new java.sql.Timestamp(lvalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
						}
					}
					break;
				case PKEY:
					switch(castToType.getId()) {
						case FKEY: return value;
						case INT: return value;
					}
					break;
				case PATH:
					// No special casts
					break;
				case PHONE:
					// No special casts
					break;
				case SHORT:
					{
						short svalue=value==null?0:((Short)value);
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:svalue!=0;
							case DATE: return value==null?null:new java.sql.Date(SQLUtility.getMillisFromDays(svalue));
							case DECIMAL_2: return value==null?null:Integer.valueOf(svalue*100);
							case DECIMAL_3: return value==null?null:Integer.valueOf(svalue*1000);
							case DOUBLE: return value==null?null:new Double((double)svalue);
							case FLOAT: return value==null?null:Float.valueOf((float)svalue);
							case INT: return value==null?null:Integer.valueOf(svalue);
							case INTERVAL: return value==null?null:Long.valueOf(svalue);
							case LONG: return value==null?null:Long.valueOf(svalue);
							case OCTAL_LONG: return value==null?null:Long.valueOf(svalue);
							case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(svalue);
						}
					}
					break;
				case STRING:
					return castToType.parseString((String)value);
				case TIME:
					{
						long lvalue=value==null?0:((java.sql.Timestamp)value).getTime();
						switch(castToType.getId()) {
							case DATE: return value==null?null:new java.sql.Date(lvalue);
							case LONG: return value==null?null:Long.valueOf(lvalue);
							case OCTAL_LONG: return value==null?null:Long.valueOf(lvalue);
						}
					}
					break;
				case URL:
					// TODO: URL as URI (no resolve stuff)?
					switch(castToType.getId()) {
						case HOSTNAME: return value==null?null:HostAddress.valueOf(new URL((String)value).getHost());
						case PATH: return value==null?null:UnixPath.valueOf(new URL((String)value).getPath());
						case ZONE: return value==null?null:getZoneForDomainName(conn, DomainName.valueOf(new URL((String)value).getHost()));
						case DOMAIN_NAME: return value==null?null:DomainName.valueOf(new URL((String)value).getHost());
					}
					break;
				case USERNAME:
					// TODO: com.aoindustries.aoserv.client.validator.UserId
					switch(castToType.getId()) {
						case MYSQL_USERNAME: return value==null ? null : MySQLUserId.valueOf(value.toString());
						case POSTGRES_USERNAME: return value==null ? null : PostgresUserId.valueOf(value.toString());
					}
					break;
				case ZONE:
					// TODO: com.aoindustries.aoserv.client.validator.Zone
					switch(castToType.getId()) {
						case HOSTNAME: {
							String hname = (String)value;
							while(hname.endsWith(".")) hname = hname.substring(0, hname.length()-1);
							return HostAddress.valueOf(hname);
						}
						case DOMAIN_NAME: return value==null?null:getDomainNameForZone((String)value);
					}
					break;
				case BIG_DECIMAL:
					{
						BigDecimal bvalue = (BigDecimal)value;
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:bvalue.compareTo(BigDecimal.ZERO)!=0;
							case DECIMAL_2: return value==null?null:bvalue.multiply(bigDecimal100).intValue();
							case DECIMAL_3: return value==null?null:bvalue.multiply(bigDecimal1000).intValue();
							case DOUBLE: return value==null?null:bvalue.doubleValue();
							case FLOAT: return value==null?null:bvalue.floatValue();
							case INT: return value==null?null:bvalue.intValue();
							case INTERVAL: return value==null?null:bvalue.longValue();
							case LONG: return value==null?null:bvalue.longValue();
							case OCTAL_LONG: return value==null?null:bvalue.longValue();
							case SHORT: return value==null?null:bvalue.shortValue();
						}
					}
					break;
				case DOMAIN_LABEL:
					{
						DomainLabel dlvalue = (DomainLabel)value;
						switch(castToType.getId()) {
							case DOMAIN_LABELS: return value==null?null:DomainLabels.valueOf(dlvalue.toString());
							case DOMAIN_NAME: return value==null?null:DomainName.valueOf(dlvalue.toString());
						}
					}
					break;
				case DOMAIN_LABELS:
					{
						DomainLabels dlvalue = (DomainLabels)value;
						switch(castToType.getId()) {
							case DOMAIN_NAME: return DomainName.valueOf(((DomainLabels)value).toString());
						}
					}
					break;
				case DOMAIN_NAME:
					switch(castToType.getId()) {
						case HOSTNAME: return HostAddress.valueOf((DomainName)value);
						case ZONE: return ((DomainName)value).toString()+'.';
						case DOMAIN_LABELS: return DomainLabels.valueOf(((DomainName)value).toString());
					}
					break;
				case GECOS:
					// No special casts
					break;
				case GROUP_ID:
					// No special casts
					break;
				case HASHED_PASSWORD:
					// No special casts
					break;
				case LINUX_ID:
					{
						switch(castToType.getId()) {
							case BOOLEAN: return value==null?null:((LinuxId)value).getId()!=0;
							case INT: return value==null?null:Integer.valueOf(((LinuxId)value).getId());
						}
					}
					break;
				case MAC_ADDRESS:
					// No special casts
					break;
				case MONEY:
					{
						switch(castToType.getId()) {
							case BIG_DECIMAL: return value==null?null:((Money)value).getValue();
						}
					}
					break;
				case MYSQL_DATABASE_NAME:
					// No special casts
					break;
				case MYSQL_SERVER_NAME:
					// No special casts
					break;
				case MYSQL_TABLE_NAME:
					// No special casts
					break;
				case MYSQL_USERNAME:
					{
						switch(castToType.getId()) {
							case USERNAME: return value;
						}
					}
					break;
				case NET_PORT:
					{
						switch(castToType.getId()) {
							case INT: return value==null?null:Integer.valueOf(((Port)value).getPort());
						}
					}
					break;
				case POSTGRES_DATABASE_NAME:
					// No special casts
					break;
				case POSTGRES_SERVER_NAME:
					// No special casts
					break;
				case POSTGRES_USERNAME:
					{
						switch(castToType.getId()) {
							case USERNAME: return value;
						}
					}
					break;
				case FIREWALLD_ZONE_NAME:
					// No special casts
					break;
			}
			throw new IllegalArgumentException("Unable to cast from "+name+" to "+castToType.getName());
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e.getLocalizedMessage(), e);
		}
	}
	private static DomainName getDomainNameForEmail(String email) throws ValidationException {
		int pos=email.indexOf('@');
		if(pos==-1) throw new IllegalArgumentException("Unable to find @ in email address: "+email);
		return DomainName.valueOf(email.substring(pos+1));
	}
	private static DomainName getDomainNameForZone(String zone) throws ValidationException {
		while(zone.endsWith(".")) zone=zone.substring(0, zone.length()-1);
		return DomainName.valueOf(zone);
	}
	private static String getUsernameForEmail(String email) {
		int pos=email.indexOf('@');
		if(pos==-1) throw new IllegalArgumentException("Unable to find @ in email address: "+email);
		return email.substring(0, pos);
	}

	private static String getZoneForDomainName(AOServConnector conn, DomainName domainName) throws IOException, IllegalArgumentException, SQLException {
		if(domainName==null) return null;
		return conn.getDnsZones().getHostTLD(domainName) + ".";
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
	public static int compareTo(Object value1, Object value2, int id) throws IllegalArgumentException, SQLException, UnknownHostException {
		try {
			if(value1==null) {
				return value2==null?0:-1;
			} else {
				if(value2==null) return 1;
				switch(id) {
					case ACCOUNTING:
						return ((AccountingCode)value1).compareTo((AccountingCode)value2);
					case PHONE:
					case STRING:
					case URL:
						return StringUtility.compareToIgnoreCaseCarefulEquals((String)value1, (String)value2);
					case USERNAME:
						return ((UserId)value1).compareTo((UserId)value2);
					case PATH:
						return ((UnixPath)value1).compareTo((UnixPath)value2);
					case BOOLEAN:
						// Sorts false before true
						return
							((Boolean)value1)
							?(((Boolean)value2)?0:1)
							:(((Boolean)value2)?-1:0)
						;
					case DATE:
						{
							long t1=SQLUtility.roundToDay(((java.sql.Date)value1).getTime());
							long t2=SQLUtility.roundToDay(((java.sql.Date)value2).getTime());
							return t1>t2?1:t1<t2?-1:0;
						}
					case DECIMAL_2:
						{
							int i1=((Integer)value1);
							int i2=((Integer)value2);
							return i1>i2?1:i1<i2?-1:0;
						}
					case DECIMAL_3:
						{
							int i1=((Integer)value1);
							int i2=((Integer)value2);
							return i1>i2?1:i1<i2?-1:0;
						}
					case FKEY:
					case INT:
					case PKEY:
						return ((Integer)value1).compareTo((Integer)value2);
					case DOUBLE:
						return ((Double)value1).compareTo((Double)value2);
					case EMAIL:
						// TODO: com.aoindustries.net.Email
						return compareEmailAddresses((String)value1, (String)value2);
					case FLOAT:
						return ((Float)value1).compareTo((Float)value2);
					case HOSTNAME:
						return ((HostAddress)value1).compareTo((HostAddress)value2);
					case ZONE:
						// TODO: com.aoindustries.aoserv.client.validator.Zone
						return DomainName.compareLabels((String)value1, (String)value2);
					case INTERVAL:
					case LONG:
					case OCTAL_LONG:
						return ((Long)value1).compareTo((Long)value2);
					case IP_ADDRESS:
						return ((InetAddress)value1).compareTo((InetAddress)value2);
					case SHORT:
						return ((Short)value1).compareTo((Short)value2);
					case TIME:
						return ((java.sql.Timestamp)value1).compareTo((java.sql.Timestamp)value2);
					case BIG_DECIMAL:
						return ((BigDecimal)value1).compareTo((BigDecimal)value2);
					case DOMAIN_LABEL:
						return ((DomainLabel)value1).compareTo((DomainLabel)value2);
					case DOMAIN_LABELS:
						return ((DomainLabels)value1).compareTo((DomainLabels)value2);
					case DOMAIN_NAME:
						return ((DomainName)value1).compareTo((DomainName)value2);
					case GECOS:
						return ((Gecos)value1).compareTo((Gecos)value2);
					case GROUP_ID:
						return ((GroupId)value1).compareTo((GroupId)value2);
					case HASHED_PASSWORD:
						return ((HashedPassword)value1).compareTo((HashedPassword)value2);
					case LINUX_ID:
						return ((LinuxId)value1).compareTo((LinuxId)value2);
					case MAC_ADDRESS:
						return ((MacAddress)value1).compareTo((MacAddress)value2);
					case MONEY:
						return ((Money)value1).compareTo((Money)value2);
					case MYSQL_DATABASE_NAME:
						return ((MySQLDatabaseName)value1).compareTo((MySQLDatabaseName)value2);
					case MYSQL_SERVER_NAME:
						return ((MySQLServerName)value1).compareTo((MySQLServerName)value2);
					case MYSQL_TABLE_NAME:
						return ((MySQLTableName)value1).compareTo((MySQLTableName)value2);
					case MYSQL_USERNAME:
						return ((MySQLUserId)value1).compareTo((MySQLUserId)value2);
					case NET_PORT:
						return ((Port)value1).compareTo((Port)value2);
					case POSTGRES_DATABASE_NAME:
						return ((PostgresDatabaseName)value1).compareTo((PostgresDatabaseName)value2);
					case POSTGRES_SERVER_NAME:
						return ((PostgresServerName)value1).compareTo((PostgresServerName)value2);
					case POSTGRES_USERNAME:
						return ((PostgresUserId)value1).compareTo((PostgresUserId)value2);
					case FIREWALLD_ZONE_NAME:
						return ((FirewalldZoneName)value1).compareTo((FirewalldZoneName)value2);
					default: throw new IllegalArgumentException("Unknown type: "+id);
				}
			}
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e.getLocalizedMessage(), e);
		}
	}

	// TODO: Should not exist once using Email validator type
	private static int compareEmailAddresses(String address1, String address2) throws SQLException, ValidationException {
		int diff=getDomainNameForEmail(address1).compareTo(getDomainNameForEmail(address2));
		if(diff!=0) return diff;
		return getUsernameForEmail(address1).compareTo(getUsernameForEmail(address2));
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case 0: return pkey;
			case COLUMN_NAME: return name;
			case 2: return sinceVersion;
			case 3: return lastVersion;
			default: throw new IllegalArgumentException("Invalid index: " + i);
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

	public AOServProtocol getSinceVersion(AOServConnector connector) throws SQLException, IOException {
		AOServProtocol obj = connector.getAoservProtocols().get(sinceVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + sinceVersion);
		return obj;
	}

	public String getLastVersion_version() {
		return lastVersion;
	}

	public AOServProtocol getLastVersion(AOServConnector connector) throws SQLException, IOException {
		if(lastVersion == null) return null;
		AOServProtocol obj = connector.getAoservProtocols().get(lastVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + lastVersion);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SCHEMA_TYPES;
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
	public void read(CompressedDataInputStream in) throws IOException {
		pkey         = in.readCompressedInt();
		name         = in.readUTF().intern();
		sinceVersion = in.readUTF().intern();
		lastVersion  = InternUtils.intern(in.readNullUTF());
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeUTF(name);
			out.writeCompressedInt(pkey);
		} else {
			out.writeCompressedInt(pkey);
			out.writeUTF(name);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_69) >= 0) {
			out.writeUTF(sinceVersion);
			out.writeNullUTF(lastVersion);
		}
	}

	@Override
	public String toStringImpl() {
		return name;
	}

	public String getString(Object value) {
		return getString(value, pkey);
	}

	public static String getString(Object value, int id) throws IllegalArgumentException {
		if(value == null) return null;
		switch(id) {
			case ACCOUNTING: return value.toString();
			case BOOLEAN: return value.toString();
			case DATE: return SQLUtility.getDate(((java.sql.Date)value).getTime());
			case DECIMAL_2: return SQLUtility.getDecimal(((Integer)value));
			case DECIMAL_3: return SQLUtility.getMilliDecimal(((Integer)value));
			case DOUBLE: return value.toString();
			case EMAIL: return (String)value; // TODO: com.aoindustries.net.Email
			case FKEY: return value.toString();
			case FLOAT: return value.toString();
			case HOSTNAME: return value.toString();
			case INT: return value.toString();
			case INTERVAL: return StringUtility.getDecimalTimeLengthString(((Long)value));
			case IP_ADDRESS: return value.toString();
			case LONG: return value.toString();
			case OCTAL_LONG: return Long.toOctalString(((Long)value));
			case PATH: return value.toString();
			case PHONE: return (String)value;
			case PKEY: return value.toString();
			case SHORT: return value.toString();
			case STRING: return (String)value;
			case TIME: return SQLUtility.getDateTime(((java.sql.Timestamp)value).getTime(), false);
			case URL: return (String)value;
			case USERNAME: return value.toString();
			case ZONE: return (String)value; // TODO: com.aoindustries.aoserv.client.validator.Zone
			case BIG_DECIMAL: return value.toString();
			case DOMAIN_LABEL: return value.toString();
			case DOMAIN_LABELS: return value.toString();
			case DOMAIN_NAME: return value.toString();
			case GECOS: return value.toString();
			case GROUP_ID: return value.toString();
			case HASHED_PASSWORD: return value.toString();
			case LINUX_ID: return value.toString();
			case MAC_ADDRESS: return value.toString();
			case MONEY: return value.toString();
			case MYSQL_DATABASE_NAME: return value.toString();
			case MYSQL_SERVER_NAME: return value.toString();
			case MYSQL_TABLE_NAME: return value.toString();
			case MYSQL_USERNAME: return value.toString();
			case NET_PORT: return value.toString();
			case POSTGRES_DATABASE_NAME: return value.toString();
			case POSTGRES_SERVER_NAME: return value.toString();
			case POSTGRES_USERNAME: return value.toString();
			case FIREWALLD_ZONE_NAME: return value.toString();
			default: throw new IllegalArgumentException("Unknown SchemaType: " + id);
		}
	}

	public Object parseString(String S) throws IllegalArgumentException {
		return parseString(S, pkey);
	}

	public static Object parseString(String S, int id) throws IllegalArgumentException {
		try {
			if(S == null) return null;
			switch(id) {
				case ACCOUNTING:
					return AccountingCode.valueOf(S);
				case EMAIL: // TODO: com.aoindustries.net.Email
				case PHONE:
				case STRING:
				case URL:
				case ZONE: // TODO: com.aoindustries.aoserv.client.validator.Zone
					return S;
				case USERNAME:
					return UserId.valueOf(S);
				case PATH:
					return UnixPath.valueOf(S);
				case HOSTNAME:
					return HostAddress.valueOf(S);
				case IP_ADDRESS:
					return InetAddress.valueOf(S);
				case BOOLEAN:
					if(
						S.equalsIgnoreCase("y")
						|| S.equalsIgnoreCase("yes")
						|| S.equalsIgnoreCase("t")
						|| S.equalsIgnoreCase("true")
					) return Boolean.TRUE;
					if(
						S.equalsIgnoreCase("n")
						|| S.equalsIgnoreCase("no")
						|| S.equalsIgnoreCase("f")
						|| S.equalsIgnoreCase("false")
					) return Boolean.FALSE;
					throw new IllegalArgumentException("Unable to parse boolean: "+S);
				case DATE:
					return new java.sql.Date(SQLUtility.getDate(S).getTime());
				case DECIMAL_2:
					return SQLUtility.getPennies(S);
				case DECIMAL_3:
					return SQLUtility.getMillis(S);
				case DOUBLE:
					return new Double(S);
				case FKEY:
				case INT:
				case PKEY:
					return Integer.parseInt(S);
				case FLOAT:
					return Float.valueOf(S);
				case INTERVAL:
					throw new UnsupportedOperationException("Interval parsing not yet supported");
				case LONG:
					return Long.valueOf(S);
				case OCTAL_LONG:
					return Long.parseLong(S, 8);
				case SHORT:
					return Short.valueOf(S);
				case TIME:
					return new java.sql.Timestamp(SQLUtility.getDateTime(S).getTime());
				case BIG_DECIMAL:
					return new BigDecimal(S);
				case DOMAIN_LABEL:
					return DomainLabel.valueOf(S);
				case DOMAIN_LABELS:
					return DomainLabels.valueOf(S);
				case DOMAIN_NAME:
					return DomainName.valueOf(S);
				case GECOS:
					return Gecos.valueOf(S);
				case GROUP_ID:
					return GroupId.valueOf(S);
				case HASHED_PASSWORD:
					return HashedPassword.valueOf(S);
				case LINUX_ID:
					return LinuxId.valueOf(Integer.parseInt(S));
				case MAC_ADDRESS:
					return MacAddress.valueOf(S);
				case MONEY:
					throw new IllegalArgumentException("Parsing from String to Money is not supported.");
				case MYSQL_DATABASE_NAME:
					return MySQLDatabaseName.valueOf(S);
				case MYSQL_SERVER_NAME:
					return MySQLServerName.valueOf(S);
				case MYSQL_TABLE_NAME:
					return MySQLTableName.valueOf(S);
				case MYSQL_USERNAME:
					return MySQLUserId.valueOf(S);
				case NET_PORT:
					{
						int slashPos = S.indexOf('/');
						if(slashPos == -1) throw new IllegalArgumentException("Slash (/) not found for Port: " + S);
						return Port.valueOf(
							Integer.parseInt(S.substring(0, slashPos)),
							com.aoindustries.net.Protocol.valueOf(S.substring(slashPos + 1).toUpperCase(Locale.ROOT))
						);
					}
				case POSTGRES_DATABASE_NAME:
					return PostgresDatabaseName.valueOf(S);
				case POSTGRES_SERVER_NAME:
					return PostgresServerName.valueOf(S);
				case POSTGRES_USERNAME:
					return PostgresUserId.valueOf(S);
				case FIREWALLD_ZONE_NAME:
					return FirewalldZoneName.valueOf(S);
				default:
					throw new IllegalArgumentException("Unknown SchemaType: "+id);
			}
		} catch(ValidationException e) {
			throw new IllegalArgumentException(e.getLocalizedMessage(), e);
		}
	}

	/*
	private static java.sql.Date getDate(java.sql.Date date) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(cal.getTimeInMillis());
	}*/
}
