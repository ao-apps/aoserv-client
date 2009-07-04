package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
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
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaType extends GlobalObjectIntegerKey<SchemaType> {

    static final int COLUMN_TYPE=0;
    static final String DATE_name = "date";

    /**
     * The different types of values.
     */
    public static final int
        ACCOUNTING=0,
        BOOLEAN=1,
        BYTE=2,
        CITY=3,
        COUNTRY=4,
        DATE=5,
        DECIMAL_2=6,
        DECIMAL_3=7,
        DOUBLE=8,
        EMAIL=9,
        FKEY=10,
        FLOAT=11,
        HOSTNAME=12,
        INT=13,
        INTERVAL=14,
        IP_ADDRESS=15,
        LONG=16,
        OCTAL_INT=17,
        OCTAL_LONG=18,
        PACKAGE=19,
        PKEY=20,
        PATH=21,
        PHONE=22,
        SHORT=23,
        STATE=24,
        STRING=25,
        TIME=26,
        URL=27,
        USERNAME=28,
        ZIP=29,
        ZONE=30,
        BIG_DECIMAL=31
    ;

    private static final BigDecimal
        bigDecimalNegativeOne = BigDecimal.valueOf(-1),
        bigDecimal100 = BigDecimal.valueOf(100),
        bigDecimal1000 = BigDecimal.valueOf(1000)
    ;

    private String type;

    public boolean alignRight() {
        return alignRight(pkey);
    }

    public static boolean alignRight(int type) {
        switch(type) {
            case BYTE:
            case DECIMAL_2:
            case DECIMAL_3:
            case DOUBLE:
            case FKEY:
            case FLOAT:
            case INT:
            case INTERVAL:
            case LONG:
            case OCTAL_INT:
            case OCTAL_LONG:
            case PKEY:
            case SHORT:
            case BIG_DECIMAL:
                return true;
            default:
                return false;
        }
    }

    /**
     * Casts one type of object to another.  These casts are allowed:
     *
     * <pre>
     *                                                                                                           B
     *              A                                            I        O                                      I
     *              C                 D  D                       P     O  C                                      G
     *              C                 E  E              H     I  _     C  T                             U        _
     *              O  B        C     C  C              O     N  A     T  A  P                          S        D
     *           T  U  O        O     I  I  D           S     T  D     A  L  A                 S        E        E
     *           O  N  O        U     M  M  O  E     F  T     E  D     L  _  C        P  S  S  T        R        C
     *              T  L  B  C  N  D  A  A  U  M  F  L  N     R  R  L  _  L  K  P  P  H  H  T  R  T     N     Z  I
     *              I  E  Y  I  T  A  L  L  B  A  K  O  A  I  V  E  O  I  O  A  K  A  O  O  A  I  I  U  A  Z  O  M
     *              N  A  T  T  R  T  _  _  L  I  E  A  M  N  A  S  N  N  N  G  E  T  N  R  T  N  M  R  M  I  N  A
     *     FROM     G  N  E  Y  Y  E  2  3  E  L  Y  T  E  T  L  S  G  T  G  E  Y  H  E  T  E  G  E  L  E  P  E  L
     *
     *  ACCOUNTING  X                                                        X                 X
     *     BOOLEAN     X  X           X  X  X        X     X        X  X  X              X     X                 X
     *        BYTE     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     *        CITY           X                                                                 X
     *     COUNTRY              X                                                              X
     *        DATE                 X  X  X  X        X     X        X  X  X              X     X  X              X
     *   DECIMAL_2     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     *   DECIMAL_3     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     *      DOUBLE     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     *       EMAIL                             X        X  X     X     X                       X     X  X     X
     *        FKEY                                X        X           X        X              X
     *       FLOAT     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     *    HOSTNAME                                      X  X     X     X                       X              X
     *         INT     X  X        X  X  X  X     X  X  X  X  X  X  X  X  X     X        X     X              X  X
     *    INTERVAL        X           X  X  X        X     X  X     X  X  X              X     X                 X
     *  IP_ADDRESS                                      X  X     X     X                       X              X
     *        LONG     X  X        X  X  X  X        X     X  X     X  X  X              X     X  X              X
     *   OCTAL_INT     X  X        X  X  X  X     X  X  X  X  X  X  X  X  X     X        X     X              X  X
     *  OCTAL_LONG     X  X        X  X  X  X        X     X  X     X  X  X              X     X  X              X
     *     PACKAGE  X                                                        X                 X
     *        PKEY                                X        X           X        X              X
     *        PATH                                                                 X           X
     *       PHONE                                                                    X        X
     *       SHORT     X  X        X  X  X  X        X     X  X     X  X  X              X     X                 X
     *       STATE                                                                          X  X
     *      STRING  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X  X
     *        TIME                 X                                X     X                    X  X
     *         URL                                      X  X     X     X           X           X     X        X
     *    USERNAME                                                                             X        X
     *         ZIP                                                                             X           X
     *        ZONE                                      X  X     X     X                       X              X
     * BIG_DECIMAL     X  X           X  X  X        X     X  X     X  X  X              X     X                 X
     * </pre>
     */
    public Object cast(AOServConnector conn, Object value, SchemaType castToType) throws IOException, SQLException {
        switch(pkey) {
            case ACCOUNTING:
                switch(castToType.getNum()) {
                    case ACCOUNTING:
                    case PACKAGE:
                    case STRING: return value;
                }
                break;
            case BOOLEAN:
                {
                    boolean bvalue=value==null?false:((Boolean)value).booleanValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value;
                        case BYTE: return value==null?null:new Byte(bvalue?(byte)-1:0);
                        case DECIMAL_2: return value==null?null:Integer.valueOf(bvalue?-100:0);
                        case DECIMAL_3: return value==null?null:Integer.valueOf(bvalue?-1000:0);
                        case DOUBLE: return value==null?null:new Double(bvalue?-1:0);
                        case FLOAT: return value==null?null:Float.valueOf(bvalue?-1:0);
                        case INT: return value==null?null:Integer.valueOf(bvalue?-1:0);
                        case LONG: return value==null?null:Long.valueOf(bvalue?(long)-1:0);
                        case OCTAL_INT: return value==null?null:Integer.valueOf(bvalue?-1:0);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(bvalue?(long)-1:0);
                        case SHORT: return value==null?null:Short.valueOf(bvalue?(short)-1:0);
                        case STRING: return value==null?null:bvalue?"true":"false";
                        case BIG_DECIMAL: return value==null?null:bvalue?bigDecimalNegativeOne:BigDecimal.ZERO;
                    }
                }
                break;
            case BYTE:
                {
                    byte bvalue=value==null?0:((Byte)value).byteValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:bvalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value;
                        case DECIMAL_2: return value==null?null:Integer.valueOf(bvalue*100);
                        case DECIMAL_3: return value==null?null:Integer.valueOf(bvalue*1000);
                        case DOUBLE: return value==null?null:new Double((double)bvalue);
                        case FLOAT: return value==null?null:Float.valueOf(bvalue);
                        case INT: return value==null?null:Integer.valueOf(bvalue);
                        case INTERVAL: return value==null?null:Long.valueOf(bvalue);
                        case LONG: return value==null?null:Long.valueOf(bvalue);
                        case OCTAL_INT: return value==null?null:Integer.valueOf(bvalue);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(bvalue);
                        case SHORT: return value==null?null:Short.valueOf(bvalue);
                        case STRING: return value==null?null:value.toString();
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(bvalue);
                    }
                }
                break;
            case CITY:
                switch(castToType.getNum()) {
                    case CITY: return value;
                    case STRING: return value;
                }
                break;
            case COUNTRY:
                switch(castToType.getNum()) {
                    case COUNTRY: return value;
                    case STRING: return value;
                }
                break;
            case DATE:
                {
                    long tvalue=value==null?0:((java.sql.Date)value).getTime();
                    switch(castToType.getNum()) {
                        case DATE: return value;
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)*1000));
                        case DOUBLE: return value==null?null:new Double(SQLUtility.getDaysFromMillis(tvalue));
                        case FLOAT: return value==null?null:Float.valueOf(SQLUtility.getDaysFromMillis(tvalue));
                        case INT: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)));
                        case LONG: return value==null?null:Long.valueOf(SQLUtility.getDaysFromMillis(tvalue));
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)(SQLUtility.getDaysFromMillis(tvalue)));
                        case OCTAL_LONG: return value==null?null:Long.valueOf(SQLUtility.getDaysFromMillis(tvalue));
                        case SHORT: return value==null?null:Short.valueOf((short)(SQLUtility.getDaysFromMillis(tvalue)));
                        case STRING: return value==null?null:SQLUtility.getDate(tvalue);
                        case TIME: return value==null?null:new java.sql.Date(SQLUtility.roundToDay(tvalue));
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(SQLUtility.getDaysFromMillis(tvalue));
                    }
                }
                break;
            case DECIMAL_2:
                {
                    int ivalue=value==null?0:((Integer)value).intValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:ivalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)(ivalue/100));
                        case DECIMAL_2: return value;
                        case DECIMAL_3: return value==null?null:Integer.valueOf(ivalue*10);
                        case DOUBLE: return value==null?null:new Double((double)ivalue/100);
                        case FLOAT: return value==null?null:Float.valueOf((float)ivalue/100);
                        case INT: return value==null?null:Integer.valueOf(ivalue/100);
                        case INTERVAL: return value==null?null:Long.valueOf(ivalue/100);
                        case LONG: return value==null?null:Long.valueOf(ivalue/100);
                        case OCTAL_INT: return value==null?null:Integer.valueOf(ivalue/100);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue/100);
                        case SHORT: return value==null?null:Short.valueOf((short)(ivalue/100));
                        case STRING: return value;
                        case BIG_DECIMAL: return value==null?null:new BigDecimal(SQLUtility.getDecimal(ivalue));
                    }
                }
                break;
            case DECIMAL_3:
                {
                    int ivalue=value==null?0:((Integer)value).intValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:ivalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)(ivalue/1000));
                        case DECIMAL_2: return value==null?null:Integer.valueOf(ivalue/10);
                        case DECIMAL_3: return value;
                        case DOUBLE: return value==null?null:new Double((double)ivalue/1000);
                        case FLOAT: return value==null?null:Float.valueOf((float)ivalue/1000);
                        case INT: return value==null?null:Integer.valueOf(ivalue/1000);
                        case INTERVAL: return value==null?null:Long.valueOf(ivalue/1000);
                        case LONG: return value==null?null:Long.valueOf(ivalue/1000);
                        case OCTAL_INT: return value==null?null:Integer.valueOf(ivalue/1000);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue/1000);
                        case SHORT: return value==null?null:Short.valueOf((short)(ivalue/1000));
                        case STRING: return value;
                        case BIG_DECIMAL: return value==null?null:new BigDecimal(SQLUtility.getDecimal(ivalue));
                    }
                }
                break;
            case DOUBLE:
                {
                    double dvalue=value==null?0:((Double)value).doubleValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:dvalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)dvalue);
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(dvalue*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(dvalue*1000));
                        case DOUBLE: return value;
                        case FLOAT: return value==null?null:Float.valueOf((float)dvalue);
                        case INT: return value==null?null:Integer.valueOf((int)dvalue);
                        case INTERVAL: return value==null?null:Long.valueOf((long)dvalue);
                        case LONG: return value==null?null:Long.valueOf((long)dvalue);
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)dvalue);
                        case OCTAL_LONG: return value==null?null:Long.valueOf((long)dvalue);
                        case SHORT: return value==null?null:Short.valueOf((short)dvalue);
                        case STRING: return value==null?null:value.toString();
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(dvalue);
                    }
                }
                break;
            case EMAIL:
                switch(castToType.getNum()) {
                    case EMAIL: return value;
                    case HOSTNAME: return value==null?null:getHostnameForEmail((String)value);
                    case INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname(getHostnameForEmail((String)value))));
                    case IP_ADDRESS: return value==null?null:getIPAddressForHostname(getHostnameForEmail((String)value));
                    case OCTAL_INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname(getHostnameForEmail((String)value))));
                    case STRING: return value;
                    case URL: return value==null?null:"mailto:"+((String)value);
                    case USERNAME: return value==null?null:getUsernameForEmail((String)value);
                    case ZONE: return value==null?null:getZoneForHostname(conn, getHostnameForEmail((String)value));
                }
                break;
            case FKEY:
                switch(castToType.getNum()) {
                    case FKEY: return value;
                    case INT: return value;
                    case OCTAL_INT: return value;
                    case PKEY: return value;
                    case STRING: return value==null?null:value.toString();
                }
                break;
            case FLOAT:
                {
                    float fvalue=value==null?0:((Float)value).floatValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:fvalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)fvalue);
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(fvalue*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(fvalue*1000));
                        case DOUBLE: return value==null?null:new Double(fvalue);
                        case FLOAT: return value;
                        case INT: return value==null?null:Integer.valueOf((int)fvalue);
                        case INTERVAL: return value==null?null:Long.valueOf((long)fvalue);
                        case LONG: return value==null?null:Long.valueOf((long)fvalue);
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)fvalue);
                        case OCTAL_LONG: return value==null?null:Long.valueOf((long)fvalue);
                        case SHORT: return value==null?null:Short.valueOf((short)fvalue);
                        case STRING: return value==null?null:value.toString();
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(fvalue);
                    }
                }
                break;
            case HOSTNAME:
                switch(castToType.getNum()) {
                    case HOSTNAME: return value;
                    case INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname((String)value)));
                    case IP_ADDRESS: return value==null?null:getIPAddressForHostname((String)value);
                    case OCTAL_INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname((String)value)));
                    case STRING: return value;
                    case ZONE: return value==null?null:getZoneForHostname(conn, (String)value);
                }
                break;
            case INT:
                {
                    int ivalue=value==null?0:((Integer)value).intValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:ivalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)ivalue);
                        case DATE: return value==null?null:new java.sql.Date(SQLUtility.getMillisFromDays(ivalue));
                        case DECIMAL_2: return value==null?null:Integer.valueOf(ivalue*100);
                        case DECIMAL_3: return value==null?null:Integer.valueOf(ivalue*1000);
                        case DOUBLE: return value==null?null:new Double(ivalue);
                        case FKEY: return value;
                        case FLOAT: return value==null?null:Float.valueOf((float)ivalue);
                        case HOSTNAME: return value==null?null:getHostnameForIPAddress(IPAddress.getIPAddressForInt(ivalue));
                        case INT: return value;
                        case INTERVAL: return value==null?null:Long.valueOf(ivalue);
                        case IP_ADDRESS: return value==null?null:IPAddress.getIPAddressForInt(ivalue);
                        case LONG: return value==null?null:Long.valueOf(ivalue);
                        case OCTAL_INT: return value;
                        case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue);
                        case PKEY: return value;
                        case SHORT: return value==null?null:Short.valueOf((short)ivalue);
                        case STRING: return value==null?null:value.toString();
                        case ZONE: return value==null?null:getZoneForHostname(conn, getHostnameForIPAddress(IPAddress.getIPAddressForInt(ivalue)));
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(ivalue);
                    }
                }
                break;
            case INTERVAL:
                {
                    long lvalue=value==null?0:((Long)value).longValue();
                    switch(castToType.getNum()) {
                        case BYTE: return value==null?null:new Byte((byte)lvalue);
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
                        case DOUBLE: return value==null?null:new Double((double)lvalue);
                        case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
                        case INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case INTERVAL: return value;
                        case LONG: return value;
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case OCTAL_LONG: return value;
                        case SHORT: return value==null?null:Short.valueOf((short)lvalue);
                        case STRING: return value==null?null:value.toString();
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
                    }
                }
                break;
            case IP_ADDRESS:
                switch(castToType.getNum()) {
                    case HOSTNAME: return value==null?null:getHostnameForIPAddress((String)value);
                    case INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress((String)value));
                    case IP_ADDRESS: return value;
                    case OCTAL_INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress((String)value));
                    case STRING: return value;
                    case ZONE: return value==null?null:getZoneForHostname(conn, getHostnameForIPAddress((String)value));
                }
                break;
            case LONG:
                {
                    long lvalue=value==null?0:((Long)value).longValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:lvalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)lvalue);
                        case DATE: return value==null?null:new java.sql.Date(lvalue);
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
                        case DOUBLE: return value==null?null:new Double((double)lvalue);
                        case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
                        case INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case INTERVAL: return value;
                        case LONG: return value;
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case OCTAL_LONG: return value;
                        case SHORT: return value==null?null:Short.valueOf((short)lvalue);
                        case STRING: return value==null?null:value.toString();
                        case TIME: return value==null?null:new java.sql.Date(lvalue);
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
                    }
                }
                break;
            case OCTAL_INT:
                {
                    int ivalue=value==null?0:((Integer)value).intValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:ivalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)ivalue);
                        case DATE: return value==null?null:new java.sql.Date(SQLUtility.getMillisFromDays(ivalue));
                        case DECIMAL_2: return value==null?null:Integer.valueOf(ivalue*100);
                        case DECIMAL_3: return value==null?null:Integer.valueOf(ivalue*1000);
                        case DOUBLE: return value==null?null:new Double(ivalue);
                        case FKEY: return value;
                        case FLOAT: return value==null?null:Float.valueOf((float)ivalue);
                        case HOSTNAME: return value==null?null:getHostnameForIPAddress(IPAddress.getIPAddressForInt(ivalue));
                        case INT: return value;
                        case INTERVAL: return value==null?null:Long.valueOf(ivalue);
                        case IP_ADDRESS: return value==null?null:IPAddress.getIPAddressForInt(ivalue);
                        case LONG: return value==null?null:Long.valueOf(ivalue);
                        case OCTAL_INT: return value;
                        case OCTAL_LONG: return value==null?null:Long.valueOf(ivalue);
                        case PKEY: return value;
                        case SHORT: return value==null?null:Short.valueOf((short)ivalue);
                        case STRING: return value==null?null:Integer.toOctalString(ivalue);
                        case ZONE: return value==null?null:getZoneForHostname(conn, getHostnameForIPAddress(IPAddress.getIPAddressForInt(ivalue)));
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(ivalue);
                    }
                }
                break;
            case OCTAL_LONG:
                {
                    long lvalue=value==null?0:((Long)value).longValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:lvalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)lvalue);
                        case DATE: return value==null?null:new java.sql.Date(lvalue);
                        case DECIMAL_2: return value==null?null:Integer.valueOf((int)(lvalue*100));
                        case DECIMAL_3: return value==null?null:Integer.valueOf((int)(lvalue*1000));
                        case DOUBLE: return value==null?null:new Double((double)lvalue);
                        case FLOAT: return value==null?null:Float.valueOf((float)lvalue);
                        case INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case INTERVAL: return value;
                        case LONG: return value;
                        case OCTAL_INT: return value==null?null:Integer.valueOf((int)lvalue);
                        case OCTAL_LONG: return value;
                        case SHORT: return value==null?null:Short.valueOf((short)lvalue);
                        case STRING: return value==null?null:value.toString();
                        case TIME: return value==null?null:new java.sql.Date(lvalue);
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(lvalue);
                    }
                }
                break;
            case PACKAGE:
                switch(castToType.getNum()) {
                    case ACCOUNTING: return value;
                    case PACKAGE: return value;
                    case STRING: return value;
                }
                break;
            case PKEY:
                switch(castToType.getNum()) {
                    case FKEY: return value;
                    case INT: return value;
                    case OCTAL_INT: return value;
                    case PKEY: return value;
                    case STRING: return value==null?null:value.toString();
                }
                break;
            case PATH:
                switch(castToType.getNum()) {
                    case PATH: return value;
                    case STRING: return value;
                }
                break;
            case PHONE:
                switch(castToType.getNum()) {
                    case PHONE: return value;
                    case STRING: return value;
                }
                break;
            case SHORT:
                {
                    short svalue=value==null?0:((Short)value).shortValue();
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:svalue!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:new Byte((byte)svalue);
                        case DATE: return value==null?null:new java.sql.Date(SQLUtility.getMillisFromDays(svalue));
                        case DECIMAL_2: return value==null?null:Integer.valueOf(svalue*100);
                        case DECIMAL_3: return value==null?null:Integer.valueOf(svalue*1000);
                        case DOUBLE: return value==null?null:new Double((double)svalue);
                        case FLOAT: return value==null?null:Float.valueOf((float)svalue);
                        case INT: return value==null?null:Integer.valueOf(svalue);
                        case INTERVAL: return value==null?null:Long.valueOf(svalue);
                        case LONG: return value==null?null:Long.valueOf(svalue);
                        case OCTAL_INT: return value==null?null:Integer.valueOf(svalue);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(svalue);
                        case SHORT: return value;
                        case STRING: return value==null?null:value.toString();
                        case BIG_DECIMAL: return value==null?null:BigDecimal.valueOf(svalue);
                    }
                }
                break;
            case STATE:
                switch(castToType.getNum()) {
                    case STATE: return value;
                    case STRING: return value;
                }
                break;
            case STRING:
                return castToType.parseString((String)value);
            case TIME:
                {
                    long lvalue=value==null?0:((java.sql.Date)value).getTime();
                    switch(castToType.getNum()) {
                        case DATE: return value;
                        case LONG: return value==null?null:Long.valueOf(lvalue);
                        case OCTAL_LONG: return value==null?null:Long.valueOf(lvalue);
                        case STRING: return value==null?null:SQLUtility.getDateTime(lvalue);
                        case TIME: return value;
                    }
                }
                break;
            case URL:
                switch(castToType.getNum()) {
                    case HOSTNAME: return value==null?null:new URL((String)value).getHost();
                    case INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname(new URL((String)value).getHost())));
                    case IP_ADDRESS: return value==null?null:getIPAddressForHostname(new URL((String)value).getHost());
                    case OCTAL_INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname(new URL((String)value).getHost())));
                    case PATH: return value==null?null:new URL((String)value).getPath();
                    case STRING: return value;
                    case URL: return value;
                    case ZONE: return value==null?null:getZoneForHostname(conn, new URL((String)value).getHost());
                }
                break;
            case USERNAME:
                switch(castToType.getNum()) {
                    case STRING: return value;
                    case USERNAME: return value;
                }
                break;
            case ZIP:
                switch(castToType.getNum()) {
                    case STRING: return value;
                    case ZIP: return value;
                }
                break;
            case ZONE:
                switch(castToType.getNum()) {
                    case HOSTNAME: return value;
                    case INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname((String)value)));
                    case IP_ADDRESS: return value==null?null:getIPAddressForHostname((String)value);
                    case OCTAL_INT: return value==null?null:Integer.valueOf(IPAddress.getIntForIPAddress(getIPAddressForHostname((String)value)));
                    case STRING: return value;
                    case ZONE: return value;
                }
                break;
            case BIG_DECIMAL:
                {
                    BigDecimal bvalue = (BigDecimal)value;
                    switch(castToType.getNum()) {
                        case BOOLEAN: return value==null?null:bvalue.compareTo(BigDecimal.ZERO)!=0?Boolean.TRUE:Boolean.FALSE;
                        case BYTE: return value==null?null:bvalue.byteValue();
                        case DECIMAL_2: return value==null?null:bvalue.multiply(bigDecimal100).intValue();
                        case DECIMAL_3: return value==null?null:bvalue.multiply(bigDecimal1000).intValue();
                        case DOUBLE: return value==null?null:bvalue.doubleValue();
                        case FLOAT: return value==null?null:bvalue.floatValue();
                        case INT: return value==null?null:bvalue.intValue();
                        case INTERVAL: return value==null?null:bvalue.longValue();
                        case LONG: return value==null?null:bvalue.longValue();
                        case OCTAL_INT: return value==null?null:bvalue.intValue();
                        case OCTAL_LONG: return value==null?null:bvalue.longValue();
                        case SHORT: return value==null?null:bvalue.shortValue();
                        case STRING: return value==null?null:bvalue.toString();
                        case BIG_DECIMAL: return value;
                    }
                }
                break;
        }
        throw new IllegalArgumentException("Unable to cast from "+type+" to "+castToType.getType());
    }
    private static String getHostnameForEmail(String email) {
        int pos=email.indexOf('@');
        if(pos==-1) throw new IllegalArgumentException("Unable to find @ in email address: "+email);
        return email.substring(pos+1);
    }
    private static String getUsernameForEmail(String email) {
        int pos=email.indexOf('@');
        if(pos==-1) throw new IllegalArgumentException("Unable to find @ in email address: "+email);
        return email.substring(0, pos);
    }
    private static String getIPAddressForHostname(String hostname) throws UnknownHostException {
        return InetAddress.getByName(hostname).getHostAddress();
    }
    private static String getHostnameForIPAddress(String ipAddress) throws UnknownHostException {
        return InetAddress.getByName(ipAddress).getCanonicalHostName();
    }

    private static String getZoneForHostname(AOServConnector conn, String hostname) throws IOException, IllegalArgumentException, SQLException {
        return conn.getDnsZones().getHostTLD(hostname);
    }

    public int compareTo(Object value1, Object value2) throws IllegalArgumentException, SQLException, UnknownHostException {
        return compareTo(value1, value2, pkey);
    }

    /**
     * Compares two values lexicographically.  The values must be of the same type.
     *
     * @param  value1  the first value being compared
     * @param  value2  the second value being compared
     * @param  type  the data type
     *
     * @return  the value <code>0</code> if the two values are equal;
     *          a value less than <code>0</code> if the first value
     *          is lexicographically less than the second value; and a
     *          value greater than <code>0</code> if the first value is
     *          lexicographically greater than the second value.
     *
     * @exception  IllegalArgumentException  if the type is invalid
     */
    public static int compareTo(Object value1, Object value2, int type) throws IllegalArgumentException, SQLException, UnknownHostException {
        if(value1==null) {
            return value2==null?0:-1;
        } else {
            if(value2==null) return 1;
            switch(type) {
                case ACCOUNTING:
                case CITY:
                case COUNTRY:
                case PACKAGE:
                case PATH:
                case PHONE:
                case STATE:
                case STRING:
                case URL:
                case USERNAME:
                case ZIP:
                    return StringUtility.compareToIgnoreCaseCarefulEquals((String)value1, (String)value2);
                case BOOLEAN:
                    return
                        ((Boolean)value1).booleanValue()
                        ?(((Boolean)value2).booleanValue()?0:1)
                        :(((Boolean)value2).booleanValue()?-1:0)
                    ;
                case BYTE:
                    return ((Byte)value1).compareTo((Byte)value2);
                case DATE:
                    {
                        long t1=SQLUtility.roundToDay(((java.sql.Date)value1).getTime());
                        long t2=SQLUtility.roundToDay(((java.sql.Date)value2).getTime());
                        return t1>t2?1:t1<t2?-1:0;
                    }
                case DECIMAL_2:
                    {
                        int i1=((Integer)value1).intValue();
                        int i2=((Integer)value2).intValue();
                        return i1>i2?1:i1<i2?-1:0;
                    }
                case DECIMAL_3:
                    {
                        int i1=((Integer)value1).intValue();
                        int i2=((Integer)value2).intValue();
                        return i1>i2?1:i1<i2?-1:0;
                    }
                case FKEY:
                case INT:
                case OCTAL_INT:
                case PKEY:
                    return ((Integer)value1).compareTo((Integer)value2);
                case DOUBLE:
                    return ((Double)value1).compareTo((Double)value2);
                case EMAIL:
                    return compareEmailAddresses((String)value1, (String)value2);
                case FLOAT:
                    return ((Float)value1).compareTo((Float)value2);
                case HOSTNAME:
                case ZONE:
                    return compareHostnames((String)value1, (String)value2);
                case INTERVAL:
                case LONG:
                case OCTAL_LONG:
                    return ((Long)value1).compareTo((Long)value2);
                case IP_ADDRESS:
                    return compareIPAddresses((String)value1, (String)value2);
                case SHORT:
                    return ((Short)value1).compareTo((Short)value2);
                case TIME:
                    return ((java.sql.Date)value1).compareTo((java.sql.Date)value2);
                case BIG_DECIMAL:
                    return ((BigDecimal)value1).compareTo((BigDecimal)value2);
                default: throw new IllegalArgumentException("Unknown type: "+type);
            }
        }
    }

    public static int compareEmailAddresses(String address1, String address2) throws SQLException {
        int hostnames=compareHostnames(
            getHostnameForEmail(address1),
            getHostnameForEmail(address2)
        );
        if(hostnames!=0) return hostnames;
        return getUsernameForEmail(address1).compareTo(getUsernameForEmail(address2));
    }

    public static int compareHostnames(String host1, String host2) {
        while(host1.length()>0 && host2.length()>0) {
            int pos=host1.lastIndexOf('.');
            String section1;
            if(pos==-1) {
                section1=host1;
                host1="";
            } else {
                section1=host1.substring(pos+1);
                host1=host1.substring(0, pos);
            }

            pos=host2.lastIndexOf('.');
            String section2;
            if(pos==-1) {
                section2=host2;
                host2="";
            } else {
                section2=host2.substring(pos+1);
                host2=host2.substring(0, pos);
            }

            int diff=StringUtility.compareToIgnoreCaseCarefulEquals(section1, section2);
            if(diff!=0) return diff;
        }
        return StringUtility.compareToIgnoreCaseCarefulEquals(host1, host2);
    }
    
    public static int compareIPAddresses(String ip1, String ip2) throws UnknownHostException {
        while(ip1.length()>0 && ip2.length()>0) {
            int pos=ip1.indexOf('.');
            String section1;
            if(pos==-1) {
                section1=ip1;
                ip1="";
            } else {
                section1=ip1.substring(0, pos);
                ip1=ip1.substring(pos+1);
            }

            pos=ip2.indexOf('.');
            String section2;
            if(pos==-1) {
                section2=ip2;
                ip2="";
            } else {
                section2=ip2.substring(0, pos);
                ip2=ip2.substring(pos+1);
            }

            int diff=Integer.parseInt(section1)-Integer.parseInt(section2);
            if(diff!=0) return diff;
        }
        return StringUtility.compareToIgnoreCaseCarefulEquals(ip1, ip2);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_TYPE: return type;
            case 1: return Integer.valueOf(pkey);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getNum() {
        return pkey;
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return type;
    }

    public String getString(Object value) {
        return getString(value, pkey);
    }

    public static String getString(Object value, int type) throws IllegalArgumentException {
        if(value==null) return null;
        switch(type) {
            case ACCOUNTING: return (String)value;
            case BOOLEAN: return value.toString();
            case BYTE: return value.toString();
            case CITY: return (String)value;
            case COUNTRY: return (String)value;
            case DATE: return SQLUtility.getDate(((java.sql.Date)value).getTime());
            case DECIMAL_2: return SQLUtility.getDecimal(((Integer)value).intValue());
            case DECIMAL_3: return SQLUtility.getMilliDecimal(((Integer)value).intValue());
            case DOUBLE: return value.toString();
            case EMAIL: return (String)value;
            case FKEY: return value.toString();
            case FLOAT: return value.toString();
            case HOSTNAME: return (String)value;
            case INT: return value.toString();
            case INTERVAL:
                if(value instanceof Integer) return StringUtility.getDecimalTimeLengthString(((Integer)value).intValue());
                if(value instanceof Long) return StringUtility.getDecimalTimeLengthString(((Long)value).longValue());
                throw new IllegalArgumentException("Unknown class for INTERVAL SchemaType: "+value.getClass().getName());
            case IP_ADDRESS: return (String)value;
            case LONG: return value.toString();
            case OCTAL_INT: return Integer.toOctalString(((Integer)value).intValue());
            case OCTAL_LONG: return Long.toOctalString(((Long)value).longValue());
            case PACKAGE: return (String)value;
            case PATH: return (String)value;
            case PHONE: return (String)value;
            case PKEY: return value.toString();
            case SHORT: return value.toString();
            case STATE: return (String)value;
            case STRING: return (String)value;
            case TIME: return SQLUtility.getDateTime(((java.sql.Date)value).getTime());
            case URL: return (String)value;
            case USERNAME: return (String)value;
            case ZIP: return (String)value;
            case ZONE: return (String)value;
            case BIG_DECIMAL: return ((BigDecimal)value).toString();
            default: throw new IllegalArgumentException("Unknown SchemaType: "+type);
        }
    }

    @Override
    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_TYPES;
    }

    public String getType() {
        return type;
    }

    @Override
    public void init(ResultSet result) throws SQLException {
        type=result.getString(1);
        pkey=result.getInt(2);
    }

    public Object parseString(String S) throws IllegalArgumentException {
        return parseString(S, pkey);
    }

    public static Object parseString(String S, int type) throws IllegalArgumentException {
        if(S==null) return null;
        switch(type) {
            case ACCOUNTING:
            case CITY:
            case COUNTRY:
            case EMAIL:
            case HOSTNAME:
            case IP_ADDRESS:
            case PACKAGE:
            case PATH:
            case PHONE:
            case STATE:
            case STRING:
            case URL:
            case USERNAME:
            case ZIP:
            case ZONE:
                return S;
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
            case BYTE:
                return new Byte(S);
            case DATE:
                return new java.sql.Date(SQLUtility.getDate(S).getTime());
            case DECIMAL_2:
                return Integer.valueOf(SQLUtility.getPennies(S));
            case DECIMAL_3:
                return Integer.valueOf(SQLUtility.getMillis(S));
            case DOUBLE:
                return new Double(S);
            case FKEY:
            case INT:
            case PKEY:
                return Integer.valueOf(Integer.parseInt(S));
            case FLOAT:
                return Float.valueOf(S);
            case INTERVAL:
                throw new UnsupportedOperationException("Interval parsing not yet supported");
            case LONG:
                return Long.valueOf(S);
            case OCTAL_INT:
                return Integer.valueOf(Integer.parseInt(S, 8));
            case OCTAL_LONG:
                return Long.valueOf(Long.parseLong(S, 8));
            case SHORT:
                return Short.valueOf(S);
            case TIME:
                return new java.sql.Date(SQLUtility.getDateTime(S).getTime());
            case BIG_DECIMAL:
                return new BigDecimal(S);
            default: throw new IllegalArgumentException("Unknown SchemaType: "+type);
        }
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
        type=in.readUTF().intern();
        pkey=in.readCompressedInt();
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(type);
        out.writeCompressedInt(pkey);
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
