package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Beginning with PostgreSQL 7.1, multiple character encoding formats are
 * supported, the <code>PostgresEncoding</code>s represent the possible
 * formats.
 *
 * @see  PostgresDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresEncoding extends GlobalObjectIntegerKey<PostgresEncoding> {
    
    static final int
        COLUMN_PKEY=0,
        COLUMN_POSTGRES_VERSION=2
    ;

    /**
     * The supported encodings.
     */
    public static final String
        ALT="ALT",                      // 7.1    7.2   7.3   8.0
        BIG5="BIG5",                    // 7.1    7.2   7.3
        EUC_CN="EUC_CN",                // 7.1    7.2   7.3   8.0
        EUC_JP="EUC_JP",                // 7.1    7.2   7.3   8.0
        EUC_KR="EUC_KR",                // 7.1    7.2   7.3   8.0
        EUC_TW="EUC_TW",                // 7.1    7.2   7.3   8.0
        GB18030="GB18030",              //              7.3
        GBK="GBK",                      //              7.3
        ISO_8859_5="ISO_8859_5",        //        7.2   7.3   8.0
        ISO_8859_6="ISO_8859_6",        //        7.2   7.3   8.0
        ISO_8859_7="ISO_8859_7",        //        7.2   7.3   8.0
        ISO_8859_8="ISO_8859_8",        //        7.2   7.3   8.0
        JOHAB="JOHAB",                  //              7.3   8.0
        KOI8="KOI8",                    // 7.1    7.2   7.3   8.0
        LATIN1="LATIN1",                // 7.1    7.2   7.3   8.0
        LATIN2="LATIN2",                // 7.1    7.2   7.3   8.0
        LATIN3="LATIN3",                // 7.1    7.2   7.3   8.0
        LATIN4="LATIN4",                // 7.1    7.2   7.3   8.0
        LATIN5="LATIN5",                // 7.1    7.2   7.3   8.0
        LATIN6="LATIN6",                //        7.2   7.3   8.0
        LATIN7="LATIN7",                //        7.2   7.3   8.0
        LATIN8="LATIN8",                //        7.2   7.3   8.0
        LATIN9="LATIN9",                //        7.2   7.3   8.0
        LATIN10="LATIN10",              //        7.2   7.3   8.0
        MULE_INTERNAL="MULE_INTERNAL",  // 7.1    7.2   7.3   8.0
        SJIS="SJIS",                    // 7.1    7.2   7.3
        SQL_ASCII="SQL_ASCII",          // 7.1    7.2   7.3   8.0
        TCVN="TCVN",                    //              7.3   8.0
        UHC="UHC",                      //              7.3
        UNICODE="UNICODE",              // 7.1    7.2   7.3   8.0
        WIN="WIN",                      // 7.1    7.2   7.3   8.0
        WIN874="WIN874",                //              7.3   8.0
        WIN1250="WIN1250",              // 7.1    7.2   7.3   8.0
        WIN1256="WIN1256"               //              7.3   8.0
    ;

    /**
     * The PostgreSQL default encoding is SQL_ASCII.
     */
    public static final String DEFAULT_ENCODING=SQL_ASCII;

    String encoding;
    int postgres_version;

    public Object getColumn(int i) {
	switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return encoding;
            case COLUMN_POSTGRES_VERSION: return Integer.valueOf(postgres_version);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getEncoding() {
	return encoding;
    }

    public PostgresVersion getPostgresVersion(AOServConnector connector) {
        PostgresVersion pv=connector.postgresVersions.get(postgres_version);
        if(pv==null) throw new WrappedException(new SQLException("Unable to find PostgresVersion: "+postgres_version));
        return pv;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_ENCODINGS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	encoding=result.getString(2);
        postgres_version=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	encoding=in.readUTF().intern();
        postgres_version=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(encoding);
        out.writeCompressedInt(postgres_version);
    }
}